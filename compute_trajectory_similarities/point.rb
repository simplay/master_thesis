require_relative 'meta_info'

# A Point refers to a particular tracked location in some frame.
# In other words every Point depicts a set of lookup indices
# in a given frame where
#   x corresponds to the row index (m in Matlab)
#   y corresponds to the column index (n in Matlab)
class Point

  attr_reader :x, :y

  def initialize(args, is_loaded=false)
    @x = args[0]
    @y = args[1]

    if is_loaded
      @x = @x+1
      @y = @y+1
    end

  end

  def out_of_range?
    x > MetaInfo.build.width or y > MetaInfo.build.height or x < 1 or y < 1
  end

  def copy
    Point.new([x,y])
  end

  def sub(other)
    @x = @x - other.x
    @y = @y - other.y
    self
  end

  # computes (f * p.x, f * p.y)
  def scale_by(f)
    @x = f*@x
    @y = f*@y
    self
  end

  # computes (p.x/f, p.y/f)
  def div_by(f)
    scale_by(1.0/f)
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
