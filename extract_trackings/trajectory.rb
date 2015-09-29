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

  def append_point(point)
    @points << point
  end

end
