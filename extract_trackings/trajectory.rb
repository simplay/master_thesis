class Trajectory

  # @param start_frame [Integer] number of frame this trajectory starts at.
  # @param label [Integer] label of points this trajectory belongs to.
  def initialize(start_frame, label, is_debug=$RUN_DEBUG_MODE)
    @points = []
    @start_frame = start_frame
    @label = label
    @is_debug = is_debug
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

  def collapsed_points
    head = @points.first.to_s
    body = @points[1..-1].map(&:collapsed_to_s).join
    head + body
  end

  def to_s
    header = "### L:#{label} S:#{start_frame} C:#{count} \n"
    if @is_debug
      header + @points.map {|point| point.to_s + "\n"}.join.rstrip
    else
      header + collapsed_points.rstrip
    end
  end

end
