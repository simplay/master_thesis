class DepthField
  def self.build(basefilepath=nil)
    @singleton ||= DepthField.new(basefilepath)
  end

  def initialize(filepath)
    @depth_map_files = []
    # load depth maps and store them in @depth_map_files
  end

  # Compute depth of a given frame at a given location.
  #
  # @info: Depth values equal zero are indicating invalid depth values,
  #   and thus they are ignored.
  #   A depth value equal zero us supposed to denote an invalid value.
  # @param frame_idx [Integer] index of frame
  # @param p [Point] 2d lookup point
  # @return [Float, False] bilinearly interpolated depth in given frame if computed
  #   depth value is valid and false otherwise.
  def interpolate_depth_at(frame_idx, p)
    px_i = p.x.floor
    py_i = p.y.floor
    px_i2 = px_i+1
    py_i2 = py_i+1
    dx = p.x - px_i
    dy = p.y - py_i

    f_00 = depth_at(px_i, py_i, frame_idx)
    f_01 = depth_at(px_i, py_i2, frame_idx)
    f_10 = depth_at(px_i2, py_i, frame_idx)
    f_11 = depth_at(px_i2, py_i2, frame_idx)

    sum = 0.0
    sum = sum + f_00*(1.0-dx)*(1.0-dy) unless f_00.nil?
    sum = sum + f_01*(1.0-dx)*dy unless f_00.nil?
    sum = sum + f_10*dx*(1.0-dy) unless f_00.nil?
    sum = sum + f_11*dx*dy unless f_00.nil?

    (sum == 0.0) ? false : sum

  end

  protected

  def depth_at(x,y,frame_idx)
    depth_map = depth_at_frame(frame_idx-1)
    depth = depth_map[x-1][y-1]
    (depth == 0.0) ? false : depth
  end

  def depth_at_frame(frame_idx)
    @depth_map_files[frame_idx]
  end

end
