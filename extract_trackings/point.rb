class Point

  attr_reader :ax, :ay, :bx, :by

  # @param point_data [Array] Float values
  #   point_data[0] is from x component
  #   point_data[1] is from y component
  #   point_data[2] is to x component
  #   point_data[3] is to y component
  def initialize(point_data, is_debug=$run_debug_mode)
    @ax = point_data[0]
    @ay = point_data[1]
    @bx = point_data[2]
    @by = point_data[3]
    @is_debug = is_debug
  end

  def to_s
    delimiter = (@is_debug)? " " : "\n"
    "#{@ax} #{@ay}#{delimiter}#{@bx} #{@by} \n"
  end

  def collapsed_to_s
    "#{@bx} #{@by} \n"
  end

end
