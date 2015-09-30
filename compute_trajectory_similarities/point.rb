class Point

  attr_reader :x, :y

  def initialize(args)
    @x = args[0]
    @y = args[1]
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
