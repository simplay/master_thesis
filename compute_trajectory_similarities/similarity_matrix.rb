require 'pry'

class SimilarityMatrix
  def initialize(tracking_manager)
    @tm = tracking_manager
  end

  def to_mat
    traverse_all_pairs
  end

  private

  def traverse_all_pairs
    count = 0
    @tm.trajectories.each do |a|
      @tm.trajectories.each do |b|
        similarity(a,b)
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

    d_sp_a_b = avg_spatial_distance_between(a, b, max_min_frame, min_max_frame)

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
