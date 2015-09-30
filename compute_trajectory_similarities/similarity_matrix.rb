require 'pry'
require_relative 'point'

class SimilarityMatrix

  # see: segmentation of moving objects, section 4.
  LAMBDA = 0.1

  def initialize(tracking_manager)
    @tm = tracking_manager
  end

  def to_mat
    traverse_all_pairs
  end

  private

  def trajectories
    @tm.trajectories.first(200)
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
    binding.pry
  end

  # Compute similarity between two given trajectories a and b
  # @param a [Trajectory] first trajectory
  # @param b [Trajectory] second trajectory
  def similarity(a, b)

    # Find overlapping part of two given trajectories:
    #
    # find latest start frame of trajectory pair
    max_min_frame = [a,b].map(&:start_frame).max
    # find earliest end frame of trajectory pair
    min_max_frame = [a,b].map(&:end_frame).min

    # Compute affinities w(A,B)
    d2_t_a_b = temporal_distances_between(a, b, max_min_frame, min_max_frame)
    return 0.0 if d2_t_a_b.empty?
    d_t_a_b = d2_t_a_b.map {|item| Math.sqrt(item)}
    d2_a_b = d_t_a_b.max
    w_a_b = Math.exp(-LAMBDA*d2_a_b)
  end

  def temporal_distances_between(a, b, lower_idx, upper_idx)
    return [] if upper_idx-lower_idx == -1
    d_sp_a_b = avg_spatial_distance_between(a, b, lower_idx, upper_idx)
    common_frame_count = upper_idx-lower_idx+1
    (lower_idx..upper_idx).map do |idx|

      # compute foreward diff over T for A,B
      dt_A = foreward_differece_on(a, common_frame_count, idx)
      dt_B = foreward_differece_on(b, common_frame_count, idx)
      dt_AB = dt_A.sub(dt_B).length_squared

      # TODO: compute these values
      sigma_t = sigma_t_at(idx)

      # formula 4
      d_sp_a_b*(dt_AB/(sigma_t**2))
      # do something here
    end
  end

  # perform lookup in appropriate sigma value frame.
  # TODO implement me
  def sigma_t_at(frame_idx)
    1
  end

  # implementation of formula 3
  def foreward_differece_on(trajectory, common_frame_count, idx)
    t = [common_frame_count, 5].min-1
    p_i = trajectory.point_at(idx)
    p_i_pl_t = trajectory.point_at(idx+t)
    p = p_i_pl_t.copy.sub(p_i)
    c = common_frame_count.to_f
    Point.new([p.x/c,p.y/c])
  end

  # compute the average spatial distance between all overlapping points of two given
  # trajectories a and b.
  #
  # @param a [Trajectory] first trajectory
  # @param b [Trajectory] second trajectory
  def avg_spatial_distance_between(a, b, lower_idx, upper_idx)
    return 0 if upper_idx-lower_idx == -1
    len = 0
    (lower_idx..upper_idx).each do |idx|
      pa = a.point_at(idx)
      pb = b.point_at(idx)
      len = len + pa.copy.sub(pb).length
    end
    len = len / (upper_idx-lower_idx+1)
  end
end
