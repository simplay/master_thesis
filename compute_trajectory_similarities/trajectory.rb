class Trajectory

  # @param start_frame [Integer] number of frame this trajectory starts at.
  # @param label [Integer] label of points this trajectory belongs to.
  def initialize(start_frame, label)
    @points = []
    @start_frame = start_frame
    @label = label
    @similarities = {}
  end

  # The lenght of a trajectory is the total number of
  # vertices minus one, i.e. the number of edges.
  #
  # @return [Integer] length of trajectory
  def length
    @points.count - 1
  end

  def similarities
    @similarities
  end

  def label
    @label
  end

  def append_similarity(other_label, value)
    @similarities[other_label] = value
  end

  def start_frame
    @start_frame
  end

  # convention: very first frame has index 1
  # therefore subtract 1 from final index.
  def end_frame
    start_frame+count-1
  end

  # first frame is supposed to denote the index 0
  def point_at(frame_idx)
    idx = frame_idx - start_frame
    if idx >= count
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

end
