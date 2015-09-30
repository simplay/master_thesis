class Trajectory

  # @param start_frame [Integer] number of frame this trajectory starts at.
  # @param label [Integer] label of points this trajectory belongs to.
  def initialize(start_frame, label)
    @points = []
    @start_frame = start_frame
    @label = label
  end

  def label
    @label
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
    @points[idx]
  end

  # @return number of points contained in trajectory
  def count
    @points.count
  end

  def append_point(point)
    @points << point
  end

end
