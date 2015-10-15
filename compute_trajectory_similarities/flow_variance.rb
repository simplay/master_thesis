class FlowVariance

  def self.build(basefilepath=nil)
    @singleton ||= FlowVariance.new(basefilepath)
  end

  # assumption: first frame has index 1
  def self.at_frame(idx)
    build.global_variances[idx-1]
  end

  def initialize(filepath)
    @global_variances = []
    File.open(filepath+"/global_variances.txt", "r") do |file|
      while(line = file.gets)
        @global_variances << line.strip.to_f
      end
    end
    l_var_fnames = Dir[filepath+"/*"].select do |fnames|
      fnames.match(/local_variances/)
    end
    @local_variance_files = []
    l_var_fnames.each do |fname|
      local_variances = []
      File.open(fname, "r") do |file|
        while(line = file.gets)
          row_items = line.split("[").last.split("]").first.split(" ").map(&:to_f)
          local_variances << row_items
        end
      end
      @local_variance_files << local_variances
    end
  end

  def global_variances
    @global_variances
  end

  def local_variance_at_frame(frame_idx)
    @local_variance_files[frame_idx]
  end

  # Access a specific local variance value for a given variance
  # at a given row and column.
  #
  # @hint: x, y, frame_idx start counting at 1
  #   but first index in ruby arrays is equalt to 0.
  # @param x [Integer] is row index
  # @param y [Integer] is column index
  # @param frame_idx [Integer] is the target frame index
  # @return [Float] local variance value at given location and frame.
  def local_variance_at(x,y,frame_idx)
    raise "Index out of bound" if x<1 or y<1 or frame_idx < 1
    variances = local_variance_at_frame(frame_idx-1)
    variances[x-1][y-1]
  end

  # Obtain the interpolated variance for a given point in a target frame.
  #
  # @param p [Point] point we want to evaluate its variance
  #   x component of a trajectory point corresponds to the row index
  #   y component of a trajectory point corresponds to the column index
  #   within the target frame.
  # @param frame_idx [Integer] index of targetr frame
  #   first frame has index 1
  #
  # @example: Get bilinear interpolated variance of a point p in frame 2.
  #   p = Point([1.1, 1.5])
  #   bilinear_interpolated_variance_for(p, 2)
  #   #=> 95.82370085371792
  # @return [Float] bilinear interpolated local variance value at a given frame.
  def bilinear_interpolated_variance_for(p, frame_idx)
    px_i = p.x.floor
    py_i = p.y.floor
    px_i2 = px_i+1
    py_i2 = py_i+1
    dx = p.x - px_i
    dy = p.y - py_i

    f_00 = local_variance_at(px_i, py_i, frame_idx)
    f_01 = local_variance_at(px_i, py_i2, frame_idx)
    f_10 = local_variance_at(px_i2, py_i, frame_idx)
    f_11 = local_variance_at(px_i2, py_i2, frame_idx)

    f_00*(1.0-dx)*(1.0-dy) + f_01*(1.0-dx)*dy + f_10*dx*(1.0-dy) + f_11*dx*dy

  end

end
