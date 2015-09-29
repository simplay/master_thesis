require_relative 'trajectory'

# TrajectoryManager keeps track of all trajectories.
# It allows fast insertions and lookups (in O(1)) by making use of a Hash datastructure.
class TrajectoryManager

  def initialize
    @trajectories = {}
  end

  def trajectories
    @trajectories
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

  def to_s
    @trajectories.values.map {|traj| traj.to_s + "\n"}.join
  end

end
