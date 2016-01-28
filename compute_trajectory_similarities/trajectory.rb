class Trajectory

  # @param start_frame [Integer] number of frame this trajectory starts at.
  # @param label [Integer] label of points this trajectory belongs to.
  def initialize(start_frame, label)
    @points = []
    @start_frame = start_frame
    @label = label
    @similarities = {}
    @spatial_distances = {}
    @mutex = Mutex.new
    @mutex_avg_sp = Mutex.new
    @is_invalid = false
    @has_sim_greater_zero = false
  end

  def valid?
    !invalid?
  end

  def spatial_distances
    @spatial_distances
  end

  # Checks whether this trajectory is valid
  #
  # @info: A trajectory can be invalid due to many cases.
  #   case invalid depth data: if one of its trajectory points
  #   has not meaningful depth data associated, then the whole
  #   trajectory is invalid.
  #
  # @return [Boolean] true if invalid otherwise false.
  def invalid?
    @is_invalid
  end

  # Retrieve the most similar neighbor trajectories
  def most_similar_neighbors(n)
    smallest_top_n = @similarities.values.max(n).last
    most_similar_neighs = @similarities.select do |_, similarity|
      similarity >= smallest_top_n
    end
  end

  def sim_greater_zero?
    @has_sim_greater_zero
  end

  def contains_weird_points?
    @points.any?(&:out_of_range?)
  end

  # Retrieve all trajectory points.
  #
  # @return [Array] of Point instances.
  def points
    @points
  end

  # The lenght of a trajectory is the total number of
  # vertices minus one, i.e. the number of edges.
  #
  # @return [Integer] length of trajectory
  def length
    @points.count - 1
  end

  # Does this trajectory contain only one point?
  #
  # @return [Boolean] true if trajectory has length 0
  def one_pointed?
    length == 0
  end

  def similarities
    @similarities
  end

  # Retrieve the label of this trajectory. This label
  # is the identifying point label among all points that belong to
  # this trajectory.
  #
  # @return [Integer] label.
  def label
    @label
  end

  # Append a similarity value computed by this trajectory
  # and another having the given label.
  #
  # @param other_label [Integer] label of other trajectory
  #   we used to compute the similarity value between us.
  # @param value [Float] computed simularity value
  def append_similarity(other_label, value)
    @mutex.synchronize do
      @has_sim_greater_zero = true if value > 0.0
      @similarities[other_label] = value
    end
  end

  # Append the avg spatial distance to a given other trajectory.
  #
  # @info: The other trajectory is implicitely encoded by its label.
  #   the avg spatial distance is in pixel units.
  # @param other_label [Integer] identifying label of other trajectory.
  # @param sp_dist [Float] avg pixel distance between overlapping
  #   points of this and the other trajectory.
  def append_avg_spatial_dist(other_label, sp_dist)
    @mutex_avg_sp.synchronize do
      @spatial_distances[other_label] = sp_dist
    end
  end

  def start_frame
    @start_frame
  end

  # Retrieve the frame index in which this trajectory ends.
  # @hint: very first frame has index 1
  #   therefore subtract 1 from final index.
  # @return [Integer] index of last frame.
  def end_frame
    start_frame+count-1
  end

  # first frame is supposed to denote the index 0
  def point_at(frame_idx)
    idx = frame_idx - start_frame
    if idx >= count
      puts "in point_at at frame #{point_at}"
      binding.pry
      boundary_value
    else
      @points[idx]
    end
  end

  # replicate boundary value
  # @todo: check validity of this assumption
  #   maybe return 0
  def boundary_value
    @points[count-1]
  end

  # @return number of points contained in trajectory
  def count
    @points.count
  end

  def append_point(point)
    @points << point
  end

  def mark_as_invalid
    @is_invalid = true
  end

  def to_s
    "#{@label} #{count} #{start_frame} #{end_frame}"
  end

end
