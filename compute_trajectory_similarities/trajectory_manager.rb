require_relative 'trajectory'

# TrajectoryManager keeps track of all trajectories.
# It allows fast insertions and lookups (in O(1)) by making use of a Hash datastructure.
class TrajectoryManager

  def initialize
    @trajectories = {}
  end

  def trajectories
    @trajectories.values
  end

  def min_label
    @trajectories.keys.min
  end

  def max_label
    @trajectories.keys.max
  end

  def find_trajectory_by(label)
    @trajectories.find {|traj| traj == label}
  end

  def filter_zero_length_trajectories
    trs = trajectories.select do |traj|
      traj.length > 0
    end
    binding.pry
  end

  def count
    trajectories.count
  end

  def sort_trajectories
    @trajectories = (@trajectories.sort_by{|label,tra| label}).to_h
  end

  # @param label [Integer] trajectory point label
  # @param frame_id [Integer] start frame id
  # @param point [Point] trajectory point to append.
  def append_trajectory_point(label, frame_id, point)
    trajectory = @trajectories[label]
    trajectory = Trajectory.new(frame_id, label) if trajectory.nil?
    trajectory.append_point(point)
    @trajectories[label] = trajectory
  end

end
