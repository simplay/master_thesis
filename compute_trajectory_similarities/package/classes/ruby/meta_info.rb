require_relative 'point'
require_relative 'mat3x4'

class MetaInfo

  def self.build(basefilepath=nil, dataset=nil)
    @singleton ||= MetaInfo.new(basefilepath, dataset)
  end

  def self.width
    build.width
  end

  def self.height
    build.height
  end

  # Get the width of a farme.
  #
  # @info: corresponds to the number of columns.
  # @return [Integer] frame width.
  def width
    @x ||= 1
  end

  # Get the height of a frame.
  #
  # @info: corresponds to the number of rows.
  # @return [Integer] frame height.
  def height
    @y ||= 1
  end

  # focal lengths of color camera
  def f_c
    @intrinsic_calib_data[0]
  end

  # principal points of color camera
  def p_c
    @intrinsic_calib_data[1]
  end

  # focal lengths of depth camera
  def f_d
    @intrinsic_calib_data[2]
  end

  # principal points of depth camera
  def p_d
    @intrinsic_calib_data[0]
  end

  def extrinsic_camera_mat
    @extrinsic_cam_mat
  end

  def calibration_file?
    @has_calibration_file
  end

  def initialize(filepath, dataset)
    lookup_path = "#{filepath}#{dataset}/meta/"

    meta_info = Dir["#{filepath}#{dataset}/meta/dataset.txt"].first
    res = []
    File.open(meta_info, 'r') do |f|
      while line = f.gets
        res << line.chomp.to_f
      end
    end
    unless meta_info.empty?
      @x = res[0]
      @y = res[1]
    end
    calibration_file = Dir["#{filepath}#{dataset}/meta/calib.txt"].first
    @has_calibration_file = !calibration_file.nil?
    puts "Using calibration matrix: #{calibration_file.nil?}"
    unless calibration_file.nil?
      lines = []
      File.open(calibration_file, 'r') do |f|
        while line = f.gets
          lines << line.chomp
        end
      end

      @intrinsic_calib_data = [1,2,5,6].map do |idx|
        to_point(lines, idx)
      end

      rows = []
      [8,9,10].each do |idx|
        items = lines[idx].split(" ")
        rows << items.map(&:to_f)
      end
      @extrinsic_cam_mat = Mat3x4.new(rows)

    end

  end

  private

  def to_point(lines, idx)
    items = lines[idx].split(" ")
    Point.new(items.map(&:to_f))
  end
end
