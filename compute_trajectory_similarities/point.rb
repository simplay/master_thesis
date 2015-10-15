# A Point refers to a particular tracked location in some frame.
# In other words every Point depicts a set of lookup indices
# in a given frame where
#   x corresponds to the row index (m in Matlab)
#   y corresponds to the column index (n in Matlab)
class Point

  attr_reader :x, :y

  def initialize(args)
    @x = args[0]
    @y = args[1]
  end

  # @todo: do not hardcode 480 and 640
  def out_of_range?
    x > 480 or y > 640 or x < 0 or y < 0
  end

  def copy
    Point.new([x,y])
  end

  def sub(other)
    @x = @x - other.x
    @y = @y - other.y
    self
  end

  # length according to the l2 norm.
  def length
    Math.sqrt(length_squared)
  end

  # squared length according to the l2 norm.
  def length_squared
    x**2 + y**2
  end

end
