require 'java'
require 'thread'
require 'pry'
require_relative 'flow_task'

java_import 'java.util.concurrent.Callable'
java_import 'java.util.concurrent.FutureTask'
java_import 'java.util.concurrent.LinkedBlockingQueue'
java_import 'java.util.concurrent.ThreadPoolExecutor'
java_import 'java.util.concurrent.TimeUnit'
java_import 'java.lang.Runtime'

class Loader
require 'pry'
  FLOW_METHODS = [
    "LDOF",
    "SRSF"
  ]

  MAX_POOL_THREADS = 16
  $core_pool_threads = Runtime.getRuntime.availableProcessors

  def initialize(dataset_path, variant, from_idx, to_idx, skip_comp)
    dataset = dataset_path.split(/\//).last
    case variant
    when FLOW_METHODS[0]
      @task_type = LdofFlowTask
      folder_path = dataset_path

      @subfolder_path = "#{folder_path}#{FLOW_METHODS[0].downcase}"
      Dir.mkdir(@subfolder_path) unless File.exist?(@subfolder_path)

      genarate_normalized_images(folder_path, skip_comp)
      fnames = sorted_dataset_fnames(folder_path)
      lower, upper = lookup_indices(from_idx, to_idx, fnames)
      generate_flows(fnames, dataset, lower, upper) unless skip_comp
      generate_association_file(folder_path, lower, upper)
    when FLOW_METHODS[1]
      @task_type = SrsfFlowTask
      folder_path = dataset_path
      @subfolder_path = "#{folder_path}#{FLOW_METHODS[1].downcase}"
      Dir.mkdir(@subfolder_path) unless File.exist?(@subfolder_path)
      genarate_normalized_images(folder_path, skip_comp)
      fnames = sorted_dataset_fnames(folder_path)
      lower, upper = lookup_indices(from_idx, to_idx, fnames)
      generate_flows(fnames, dataset, lower, upper) unless skip_comp
      # run matlab code
      ds_name = dataset_path.split("Data/").last.split("/").first
      run_matlab = <<-FOO
        matlab -nodisplay -nosplash -nodesktop -r \"run ${PWD}/xml_to_flo(\'#{ds_name}\') ; exit\"
      FOO
      system("cd xml_to_flo/ && " + run_matlab) unless skip_comp

      generate_association_file(folder_path, lower, upper)
    end
  end

  private

  def generate_association_file(folder_path, lower, upper)
    # generate complete used_input.txt file
    # is used for tracking

    File.open("#{folder_path}used_input.txt", 'w') do |file|
      file.puts "#use"
      file.puts "#{lower+1}\n#{upper+1}"
      file.puts "#imgs"

      imgs = Dir["#{folder_path}*.ppm"].reject do |fname|
        fname.include?("LDOF") or fname.include?("Flow") or fname.include?("flow")
      end

      len = imgs.count
      imgs = imgs.sort_by do |a| a.split("/").last.to_i end
      nupper = upper
      nupper = upper + 1 if upper < len-1
      imgs[lower..nupper].each do |img|
        file.puts img.split("/").last
      end

      file.puts "#fwf"
      imgs = Dir["#{@subfolder_path+"/"}*.flo"].select do |fname|
        fname.include?("fw")
      end
      imgs = imgs.sort_by do |a| a.split("_").last.split("L").first.to_i end
      imgs.each do |img|
        file.puts img.split("/").last
      end

      file.puts "#bwf"
      imgs = Dir["#{@subfolder_path+"/"}*.flo"].select do |fname|
        fname.include?("bw")
      end
      imgs = imgs.sort_by do |a| a.split("_").last.split("L").first.to_i end
      imgs.each do |img|
        file.puts img.split("/").last
      end
    end
  end

  def generate_flows(filepath_names, dataset, lower, upper)
    @task_count = 0
    dataset_files = filepath_names[lower..(upper+1)]
    executor = ThreadPoolExecutor.new($core_pool_threads,
                                      MAX_POOL_THREADS,
                                      60, # keep alive time
                                      TimeUnit::SECONDS,
                                      LinkedBlockingQueue.new)

    sliced_range = []
    file_count = dataset_files.count
    dataset_files.each_with_index do |item, idx|
      if idx+1 < file_count
        sliced_range << [item, dataset_files[idx+1]]
      end
    end

    @total_tasks = sliced_range.length-1
    tasks = sliced_range.map do |fnames|
      FutureTask.new(@task_type.new(dataset, fnames, @subfolder_path) )
    end

    tasks.each do |task|
      executor.execute(task)
    end

    # wait for all threads to complete
    @counter = java.util.concurrent.atomic.AtomicInteger.new
    tasks.each do |task|
      report_progress
      task.get
    end

    executor.shutdown
  end

  # Converts a given set of images to .ppm image files.
  #
  # @info: The input image set can be in an arbitry, but known image format.
  #   this method creates copies of the input images being in the .ppm file format.
  #   Makes use of Imagemagick's mogrify function.
  #
  # @param filepath [String] relative path to image dataset.
  # @param should_skip [Boolean] should we skip this converstion step.
  def genarate_normalized_images(filepath, should_skip)
    if should_skip
      puts "skipping computing flow fields"
    else
      blacklist = [".ppm", ".flo", ".txt"]
      input_imgs_fnames = Dir["#{filepath}*.*"].reject do |fname|
        blacklist.any? do |forbidden_name|
          fname.include?(forbidden_name)
        end
      end

      unless input_imgs_fnames.empty?
        fextension = input_imgs_fnames.first.split(".").last
        system("mogrify -format ppm #{filepath}/*.#{fextension}")
      end
    end
  end

  # Get am arry of all sorted file names.
  #
  # @info: Assumption: Image filenames are well enumarated.
  #   Filenames are sorted ascending.
  #
  # @param filepath [String] relative path to image dataset.
  # @return [Array<String>] set of sorted image filenames.
  def sorted_dataset_fnames(filepath, f_ext=".ppm")
    dataset_fnames = Dir["#{filepath}*#{f_ext}"]
    dataset_fnames = dataset_fnames.reject do |fname|
      fname.include? 'LDOF.ppm' or fname.include?('Flow')
    end
    dataset_fnames.sort_by { |a| a.split("/").last.to_i }
  end

  # Get dataset lookup indices range.
  #
  # @param from_idx [Ingeger, nil] lower given lookup index
  # @param to_idx [Integer, nil] upper given lookup index
  # @return [Integer] the lower lookup indices.
  # @return [Integer] the upper lookup indices.
  def lookup_indices(from_idx, to_idx, dataset_fnames)
    len = dataset_fnames.length
    lower = 0
    # Minus 2 since the first item in a ruby
    # Array has index 0 and we also want to access to end element (i.e. idx+1).
    upper = len-2
    lower = from_idx.to_i-1 unless from_idx.nil?
    upper = to_idx.to_i-2 unless to_idx.nil?
    if lower < 0 or upper-lower+1 >= len
      raise "Error: invalid indices passed!"
    end

    return lower, upper
  end

  def report_progress
    p = @counter.incrementAndGet
    puts "Progress: #{100.0*((p.to_f-1)/@total_tasks)}%"
    @task_count = @task_count + 1
  end

end
