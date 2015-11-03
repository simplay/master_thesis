require_relative 'meta_info'

class Point3f

  attr_reader :x, :y, :z

  def initialize(args)
    @x = args[0]
    @y = args[1]
    @z = args[2]
  end

  # Build a 3d point from a trajectory point.
  #
  # @param p [Point] 2d point from extracted trajectory
  # @param frame_idx [Integer] depth map associated to given frame index.
  # @return [Point3f] 2d trajectory point with depth.
  def self.build_from(p, frame_idx)
    z = 1.0 ## lookup from depth map using the frame index
    # a bilinear interpolation will be required
    # depth_map = $dmm.map_of(frame_idx)
    # z = depth_map.interpolated_value(p)
    p_3 = Point3f.new([p.x, p.y, z])
  end

  # @todo: do not hardcode 480 and 640
  def out_of_range?
    x > MetaInfo.build.width or y > MetaInfo.build.height or x < 0 or y < 0
  end

  def copy
    Point3f.new([x,y,z])
  end

  def sub(other)
    @x = @x - other.x
    @y = @y - other.y
    @z = @z - other.z
    self
  end

  # length according to the l2 norm.
  def length
    Math.sqrt(length_squared)
  end

  # squared length according to the l2 norm.
  def length_squared
    x**2 + y**2 + z**2
  end

end
