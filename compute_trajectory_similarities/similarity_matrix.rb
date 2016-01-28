require 'pry'
require 'java'
require 'thread'
require_relative 'similarity_task'

java_import 'java.util.concurrent.Callable'
java_import 'java.util.concurrent.FutureTask'
java_import 'java.util.concurrent.LinkedBlockingQueue'
java_import 'java.util.concurrent.ThreadPoolExecutor'
java_import 'java.util.concurrent.TimeUnit'
java_import 'java.lang.Runtime'

class SimilarityMatrix
  BASE_PATH = "../output/similarities/"
  USE_THREADING = true
  MAX_POOL_THREADS = 16
  $core_pool_threads = Runtime.getRuntime.availableProcessors

  def initialize(tracking_manager, is_using_local_variance=true)
    @tm = tracking_manager
    puts "Using local variance for computing similarities: #{is_using_local_variance}"
    @is_using_local_variance = is_using_local_variance
    $is_using_local_variance = @is_using_local_variance
  end

  def to_mat
    puts "Using LAMBDA = #{lambda_val}" unless $use_sum_affinity
    start = Time.now
    puts "Computing similarities..."
    if USE_THREADING
      puts "Running Multithreading using #{$core_pool_threads} threads."
    else
      puts "Running on a single core..."
    end
    @tm.filter_trajectories_shorter_than(SimilarityTask::DT_THREH) unless $is_debugging

    if USE_THREADING
      @task_count = 0
      run_executor
    else
      SimilarityTask.new(nil, trajectories).traverse_all_pairs(@tm)
    end
    # remove zero trajectories
    #c = @tm.filter_zero_sim_trajectories
    #puts "Filtered #{@tm.count - c.count} zero sim trajectories."
    finish_sim = Time.now
    diff = finish_sim - start
    puts "Computed affinities in #{diff} seconds"
    puts "Generating .dat files..."
    generate_dat_file
    finish_total = Time.now
    diff = finish_total - start
    puts "Total time elapsed: #{diff} seconds"
  end

  def use_local_variance?
    @is_using_local_variance
  end

  def run_executor
    executor = ThreadPoolExecutor.new($core_pool_threads,
                                      MAX_POOL_THREADS,
                                      60, # keep alive time
                                      TimeUnit::SECONDS,
                                      LinkedBlockingQueue.new)
    k = trajectories.count+1
    tasks = trajectories.map do |trajectory|
      # only traverse upper triangle
      t = FutureTask.new SimilarityTask.new(trajectory, trajectories.last(k))
      k = k - 1
      t
    end

    tasks.each do |task|
      executor.execute(task)
    end

    # wait for all threads to complete
    @counter = java.util.concurrent.atomic.AtomicInteger.new
    tasks.each do |task|
      report_progress
      task.get
    end

    executor.shutdown
  end


  # Compute the similarities between a trajectory with a given label
  # and all other trajectories.
  #
  # @param label [Integer] label of target trajectory
  # @return [Trajectory] we computed its similarities.
  def trajectory_similarities_for(label)
    SimilarityTask.new(nil,trajectories).trajectory_similarities_for(tm, label)
  end

  private

  def report_progress
    p = @counter.incrementAndGet
    puts "Progress: #{100.0*(p.to_f/trajectories.count)}%" if (p.to_s.to_i % 20 == 0)
    @task_count = @task_count + 1
  end

  def lambda_val
    $uses_depth_data ? SimilarityTask::LAMBDA_D : SimilarityTask::LAMBDA
  end

  # Generate a .dat file from the computed similaries stored in @tm
  #
  # @example: a regular matlab data file containing a 3x3 matrix
  #  0.81472,0.91338,0.2785
  #  0.90579,0.63236,0.54688
  #  0.12699,0.09754,0.95751
  def generate_dat_file
    base_filepathname = $has_filename ? "#{BASE_PATH}#{$fname}"
                                      : "#{BASE_PATH}#{$global_ds_name}"
    sim_filepath = "#{base_filepathname}_sim.dat"
    labels_filepath = "#{base_filepathname}_labels.txt"

    @tm.sort_trajectories
    File.open(sim_filepath, 'w') do |file|
      trajectories.each do |trajectory|
        sorted_sim = trajectory.similarities.sort.to_h
        a_row = sorted_sim.values.map(&:to_s).join(",")
        file.puts a_row
      end
    end

    File.open(labels_filepath, 'w') do |file|
      sorted_keys = trajectories.first.similarities.keys.sort
      a_row = sorted_keys.map(&:to_s).join(" ")
      file.puts a_row
    end

    puts "Wrote the following files:"
    puts "#{sim_filepath}"
    puts "#{labels_filepath}"
  end

  def trajectories
    @tm.trajectories#.first(100)
  end

end
