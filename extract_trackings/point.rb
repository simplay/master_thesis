class Point

  # @param point_data [Array] Float values
  #   point_data[0] is from x component
  #   point_data[1] is from y component
  #   point_data[2] is to x component
  #   point_data[3] is to y component
  def initialize(point_data)
    @ax = point_data[0]
    @ay = point_data[1]
    @bx = point_data[2]
    @by = point_data[3]
  end

  def to_s
    "#{@ax} #{@ay} #{@bx} #{@by}"
  end

end
