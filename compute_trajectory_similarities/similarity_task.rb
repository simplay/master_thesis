require 'java'
require 'thread'
require_relative 'point'
require_relative 'flow_variance'

java_import 'java.util.concurrent.Callable'
java_import 'java.util.concurrent.FutureTask'
java_import 'java.util.concurrent.LinkedBlockingQueue'
java_import 'java.util.concurrent.ThreadPoolExecutor'
java_import 'java.util.concurrent.TimeUnit'

class SimilarityTask
  include Callable

  # see: segmentation of moving objects, section 4.
  LAMBDA = 0.1
  LAMBDA_D = 70000.0
  DT_THREH = 5
  ZERO_THRESH = 1.0e-12

  # Setting this to 0 makes cluster consisting of 1 pixel vanish.
  EIGENSIM_VALUE = 0.0

  # Constants defined in Motion Trajectory Segmentation via Min. Cost Multicuts.
  # Used in formula (7)
  BETA_0 = 2.0
  BETA_0_TILDE = 6.0
  BETA_1 = -0.02
  BETA_2 = -4.0
  BETA_3 = -0.02

  def initialize(a, trajectories)
    @a = a
    @trajectories = trajectories
  end

  # Compute the similarities between a trajectory with a given label
  # and all other trajectories.
  #
  # @param label [Integer] label of target trajectory
  # @return [Trajectory] we computed its similarities.
  def trajectory_similarities_for(tm, label)
    a = tm.find_trajectory_by(label)
    trajectories.each do |b|
        value = similarity(a,b)
        a.append_similarity(b.label, value)
        b.append_similarity(a.label, value)
    end
    a
  end

  def traverse_all_pairs(tm)
    count = 0
    trs = @trajectories
    trs.each do |a|
      trs.each do |b|
        value = compute_affinity(a,b)
        a.append_similarity(b.label, value)
        b.append_similarity(a.label, value)
      end
      count = count + 1
      puts "progress: #{(count*tm.count/(tm.count**2).to_f)*100}%" if count % 20 == 0
    end

  end

  def call
    @trajectories.each do |b|
      value = compute_affinity(@a,b)
      @a.append_similarity(b.label, value)
      b.append_similarity(@a.label, value)
    end
  end

  def compute_affinity(a,b)
    if $use_sum_affinity
      similarity_alternative(a,b)
    else
      similarity(a,b)
    end
  end

  def use_local_variance?
    @is_using_local_variance
  end

  # alternative approach described in formula (7)
  def similarity_alternative(a, b)
    return EIGENSIM_VALUE if a == b
    # Find overlapping part of two given trajectories:
    #
    # find latest start frame of trajectory pair
    max_min_frame = [a,b].map(&:start_frame).max
    # find earliest end frame of trajectory pair
    min_max_frame = [a,b].map(&:end_frame).min
    # Compute affinities w(A,B)
    d_motions = motion_dist(a, b, max_min_frame, min_max_frame)
    d_motion = d_motions.empty? ? 0.0 : 255.0*d_motions.max
    d_spatial = avg_spatial_distance_between(a, b, max_min_frame, min_max_frame)
    d_color = color_dist(a, b, max_min_frame, min_max_frame)

    return 0.0 if min_max_frame-max_min_frame + 1 < 1

    z_ABs = [
      BETA_0_TILDE + BETA_1*d_motion + BETA_2*d_spatial + BETA_3*d_color,
      BETA_0 + BETA_1*d_motion
    ]
    z_AB = z_ABs.max
    1.0 / (1.0 + Math.exp(-z_AB))
  end

  def color_dist(a, b, lower_idx, upper_idx)
    len = 0.0
    (lower_idx..upper_idx).each do |idx|
      pa = a.point_at(idx)
      pb = b.point_at(idx)
      lab_a = CieLab.bilinear_interpolated_color_for(pa, idx)
      lab_b = CieLab.bilinear_interpolated_color_for(pb, idx)
      len = len + lab_a.copy.sub(lab_b).length
    end
    len = len / (upper_idx-lower_idx+1)
  end

  def motion_dist(a,b,lower_idx, upper_idx, dt=1)
    common_frame_count = upper_idx-lower_idx+1
    return [] if common_frame_count < 2
    timestep = $is_debugging ? dt : common_frame_count

    # ensure that we only iterate over trajectories that are longer that 4 segments
    u = [upper_idx-timestep, upper_idx - DT_THREH].max
    l = lower_idx
    return [] if u < l # when forward diff cannot be computed

    (l..u).map do |idx|
      # compute foreward diff over T for A,B
      dt_A = foreward_differece_on(a, timestep, idx)
      dt_B = foreward_differece_on(b, timestep, idx)
      dt_AB = dt_A.sub(dt_B).length_squared
      sigma_t = use_local_variance? ? local_sigma_t_at(idx, a, b) : sigma_t_at(idx)
      sigma_t = sigma_t #+ 1.0
      Math.sqrt(dt_AB/sigma_t)
    end

  end

  # Compute similarity between two given trajectories a and b
  # @param a [Trajectory] first trajectory
  # @param b [Trajectory] second trajectory
  def similarity(a, b)
    return EIGENSIM_VALUE if a == b
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
    w_a_b = Math.exp(-lambda_val*d2_a_b)
    (w_a_b < ZERO_THRESH) ? 0.0 : w_a_b
  end

  def lambda_val
    $uses_depth_data ? LAMBDA_D : LAMBDA
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
    d_AB_values = []
    d_spacial_temp_values = (l..u).map do |idx|
      # compute foreward diff over T for A,B
      dt_A = foreward_differece_on(a, timestep, idx)
      dt_B = foreward_differece_on(b, timestep, idx)
      dt_AB = dt_A.sub(dt_B).length_squared
      d_AB_values << dt_AB
      #sigma_t = use_local_variance? ? local_sigma_t_at(idx, a, b) : sigma_t_at(idx)
      #sigma_t = sigma_t + 1.0
      #d_sp_a_b*(dt_AB/sigma_t)
      d_sp_a_b*dt_AB
    end
    n = d_AB_values.count
    return [] if n == 1
    mean_d_AB = (d_AB_values.inject(0.0) {|sum, el| sum + el})/(n-1)
    # Estimator for std s = 1/(n-1) * sum_i^n (X_i - mean(X))^2
    # Variance is equal to s^2
    var_d_AB = (d_AB_values.inject(0.0) {|sum, el| sum + ((el-mean_d_AB)**2.0)})/(n-1)
    sigma_t = var_d_AB #+ 1.0
    d_spacial_temp_values.map { |item| item / sigma_t}
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
    #binding.pry if upper_idx-lower_idx + 1 < 1# should no happen!
    return 0.0 if upper_idx-lower_idx + 1 < 1# should no happen!
    len = 0.0
    (lower_idx..upper_idx).each do |idx|
      pa = a.point_at(idx)
      pb = b.point_at(idx)
      len = len + pa.copy.sub(pb).length
    end
    len = len / (upper_idx-lower_idx+1)
  end


end
