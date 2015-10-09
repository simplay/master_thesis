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

  def most_sim_neighbors_of_trajectory(trajectory, n, is_pretty_string=false)
    most_similar_neighbor_labels = trajectory.most_similar_neighbors(n)
    most_sim_n_trajectories = most_similar_neighbor_labels.map do |label, similarity|
      [find_trajectory_by(label), similarity]
    end
  end

  def pretty_top_neighbors(trajectory, n)
    most_sim_neighbors_of_trajectory(trajectory, n).map do |item|
      [item.first.to_s, item.last]
    end
  end

  # Find trajectory in trajectory manager with a given label.
  #
  # @param label [Integer] label of trajectory we are looking for.
  # @return [Trajectory, Nil] trajectory with given label iff exists.
  def find_trajectory_by(label)
    hashy_traj = @trajectories.find {|traj_label, _| traj_label == label}
    hashy_traj.nil? ? nil : hashy_traj.last
  end

  def filter_zero_length_trajectories
    one_pointed_trs = trajectories.select(&:one_pointed?)
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
