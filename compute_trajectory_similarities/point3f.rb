require_relative 'meta_info'
require_relative 'point'

class Point3f
  SCALE = 0.001 # conversion from mm to meters
  attr_reader :x, :y, :z

  def initialize(args)
    @x = args[0]
    @y = args[1]
    @z = args[2]
  end

  # Build a 3d point from a trajectory point.
  #
  # P = [(u-p_x^d)/f_x^d *z, (v-p_y^d)/f_y^d *z), z]
  # E*P = [xx, yy, zz]
  # (x/z * f_x^c + p_x^c, y/z * f_y^c + p_y^c)
  #
  # @param p [Point] 2d point from extracted trajectory
  # @param frame_idx [Integer] depth map associated to given frame index.
  # @return [Point3f] 2d trajectory point with depth.
  def self.build_from(p, frame_idx, cameras_overlapping=true)
    z = DepthField.build.interpolate_depth_at(frame_idx, p)
    return z if z == false #binding.pry 

    if MetaInfo.build.calibration_file?
      depth = z*depth_scale(true)
      _x = ((p.x-MetaInfo.build.p_d.x) / MetaInfo.build.f_d.x)*depth
      _y = ((p.y-MetaInfo.build.p_d.y) / MetaInfo.build.f_d.y)*depth
      p3 = Point3f.new([_x, _y, depth])
      pp3 = MetaInfo.build.extrinsic_camera_mat.mult(p3)
      return pp3 if cameras_overlapping
      depth = pp3.z
      x = (pp3.x*MetaInfo.build.f_c.x)/depth + MetaInfo.build.p_c.x
      y = (pp3.y*MetaInfo.build.f_c.y)/depth + MetaInfo.build.p_c.y
      Point.new([x, y])
    else
      Point3f.new([p.x, p.y, z])
    end
    ## lookup from depth map using the frame index
    # a bilinear interpolation will be required
    # depth_map = $dmm.map_of(frame_idx)
    # z = depth_map.interpolated_value(p)
  end

  def out_of_range?
    x > MetaInfo.build.width or y > MetaInfo.build.height or x < 1 or y < 1
  end

  def self.depth_scale(use_meters)
    use_meters ? SCALE : 1.0
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
