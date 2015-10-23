require 'pry'
require_relative 'point'
require_relative 'flow_variance'

class SimilarityMatrix
  BASE_PATH = "../output/similarities/"

  # see: segmentation of moving objects, section 4.
  LAMBDA = 0.1
  DT_THREH = 5
  ZERO_THRESH = 1.0e-12

  def initialize(tracking_manager, is_using_local_variance=true)
    @tm = tracking_manager
    puts "Using local variance for computing similarities: #{is_using_local_variance}"
    @is_using_local_variance = is_using_local_variance
  end

  def to_mat
    @tm.filter_trajectories_shorter_than(DT_THREH) unless $is_debugging
    traverse_all_pairs
    generate_dat_file
  end

  def use_local_variance?
    @is_using_local_variance
  end

  # Compute the similarities between a trajectory with a given label
  # and all other trajectories.
  #
  # @param label [Integer] label of target trajectory
  # @return [Trajectory] we computed its similarities.
  def trajectory_similarities_for(label)
    a = @tm.find_trajectory_by(label)
    trajectories.each do |b|
        value = similarity(a,b)
        a.append_similarity(b.label, value)
        b.append_similarity(a.label, value)
    end
    a
  end

  private

  # Generate a .dat file from the computed similaries stored in @tm
  #
  # @example: a regular matlab data file containing a 3x3 matrix
  #  0.81472,0.91338,0.2785
  #  0.90579,0.63236,0.54688
  #  0.12699,0.09754,0.95751
  def generate_dat_file
    base_filepathname = "#{BASE_PATH}#{$global_ds_name}"

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
  end

  def trajectories
    @tm.trajectories#.first(100)
  end

  # @todo: REMOVE the first(200) from @tm.trajectories.first(200)
  #   is used for debugging purposes.
  def traverse_all_pairs
    count = 0
    trs = trajectories
    trs.each do |a|
      trs.each do |b|
        value = similarity(a,b)
        a.append_similarity(b.label, value)
        b.append_similarity(a.label, value)
      end
      count = count + 1
      puts "progress: #{(count*@tm.count/(@tm.count**2).to_f)*100}%" if count % 20 == 0
    end
  end

  # Compute similarity between two given trajectories a and b
  # @param a [Trajectory] first trajectory
  # @param b [Trajectory] second trajectory
  def similarity(a, b)
    return 1.0 if a == b
    # Find overlapping part of two given trajectories:
    #
    # find latest start frame of trajectory pair
    max_min_frame = [a,b].map(&:start_frame).max
    # find earliest end frame of trajectory pair
    min_max_frame = [a,b].map(&:end_frame).min
    # Compute affinities w(A,B)
    d2_t_a_b = temporal_distances_between(a, b, max_min_frame, min_max_frame)
    return 0.0 if d2_t_a_b.empty?
    d_t_a_b = d2_t_a_b
    d2_a_b = d_t_a_b.max
    w_a_b = Math.exp(-LAMBDA*d2_a_b)
    (w_a_b < ZERO_THRESH) ? 0.0 : w_a_b
  end

  # Compute temoral distance between temporal overlapping segments
  # of two given trajectories.
  #
  # @param a [Trajectory] frist trajectory
  # @param b [Trajectory] other trajectory
  # @param lower_idx [Integer] index first overlapping trajectory frame
  # @param upper_idx [Integer] index last overlapping trajectory frame
  # @param dt [Integer] timestep size
  # @return [Array] of Float values denoting the temporal distance
  #   between two trajectory segment pairs.
  def temporal_distances_between(a, b, lower_idx, upper_idx, dt=1)
    common_frame_count = upper_idx-lower_idx+1
    return [] if common_frame_count < 2
    timestep = $is_debugging ? dt : common_frame_count

    # ensure that we only iterate over trajectories that are longer that 4 segments
    u = [upper_idx-timestep, upper_idx - DT_THREH].max
    l = lower_idx
    return [] if u < l # when forward diff cannot be computed

    # compute average spatial distance between 2 trajectories
    # over all overalapping segments.
    d_sp_a_b = avg_spatial_distance_between(a, b, l, u)
    (l..u).map do |idx|
      # compute foreward diff over T for A,B
      dt_A = foreward_differece_on(a, timestep, idx)
      dt_B = foreward_differece_on(b, timestep, idx)
      dt_AB = dt_A.sub(dt_B).length_squared
      sigma_t = use_local_variance? ? local_sigma_t_at(idx, a, b) : sigma_t_at(idx)

      # formula 4
      d_sp_a_b*(dt_AB/sigma_t)
      # do something here
    end
  end

  # perform lookup in appropriate variance value frame.
  def sigma_t_at(frame_idx)
    FlowVariance.at_frame(frame_idx)
  end

  # perform lookup in appropriate variance value frame.
  def local_sigma_t_at(idx, a, b)
    pa = a.point_at(idx)
    pb = b.point_at(idx)
    s_a = FlowVariance.build.bilinear_interpolated_variance_for(pa, idx)
    s_b = FlowVariance.build.bilinear_interpolated_variance_for(pb, idx)
    (s_a+s_b)/2.0
  end

  # Compute the value of the tangent of a given trajectory at a given location.
  #
  # @info: implementation of formula 3
  # @param Trajectory [Trajectory] trajectory we want to compute its tangent
  # @param dt [Integer] timestep size used for computing finite difference scheme.
  # @param frame_idx [Integer] lookup index of target frame.
  #   starts counting at 1.
  # @return [Point] of Float values encoding the partial derivative of that point.
  def foreward_differece_on(trajectory, dt, frame_idx)
    t = [dt, DT_THREH].min
    p_i = trajectory.point_at(frame_idx)
    p_i_pl_t = trajectory.point_at(frame_idx+t)
    p = p_i_pl_t.copy.sub(p_i)
    c = dt.to_f
    Point.new([p.x/c,p.y/c])
  end

  # compute the average spatial distance between all overlapping points of two given
  # trajectories a and b.
  #
  # @param a [Trajectory] first trajectory
  # @param b [Trajectory] second trajectory
  def avg_spatial_distance_between(a, b, lower_idx, upper_idx)
    binding.pry if upper_idx-lower_idx + 1 < 1# should no happen!
    len = 0.0
    (lower_idx..upper_idx).each do |idx|
      pa = a.point_at(idx)
      pb = b.point_at(idx)
      len = len + pa.copy.sub(pb).length
    end
    len = len / (upper_idx-lower_idx+1)
  end
end
