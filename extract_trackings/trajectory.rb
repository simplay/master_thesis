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

  # @return number of points contained in trajectory
  def count
    @points.count
  end

  def append_point(point)
    @points << point
  end

  def to_s
    header = "### L:#{label} S:#{start_frame} C:#{count} \n"
    header + @points.map {|point| point.to_s + "\n"}.join.rstrip
  end

end
