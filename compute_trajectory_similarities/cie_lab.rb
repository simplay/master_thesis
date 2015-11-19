require_relative 'point3f'
require_relative 'meta_info'

class CieLab

  def self.build(dataset=nil)
    @singleton ||= CieLab.new(dataset)
  end

  def initialize(dataset)
    filepath = "../output/cie_lab_color_imgs/#{dataset}"
    @lab_files = []
    fpnames = Dir[filepath+"/*"]
    fpnames = fpnames.sort_by do |a_fname|
      a_fname.split("lab_").last.split(".").first.to_i
    end
    puts "Loading #{fpnames.count} cie lab files"
    fpnames.each do |fname|
      lab_file = {}
      idx = 1
      File.open(fname, 'r') do |file|
        while(line = file.gets)
          a_row = line.split("(").last.split(")").first
          components = a_row.split.map(&:to_f)
          lab_file[idx] = Point3f.new(components)
          idx = idx + 1
        end
      end
      @lab_files << lab_file
    end
  end

  def self.color_at(x, y, fidx)
    build.color_at(x, y, fidx)
  end

  def self.bilinear_interpolated_color_for(p, frame_idx)
    build.bilinear_interpolated_color_for(p, frame_idx)
  end

  # Fetch the CIE l*a*b* color value at a given frame for a given location.
  #
  # @info: first index of any frame, row and column is equal to 1.
  #   Retrun nil in case no color cold be fetched
  # @example:
  #   color_at(1,1,1) gives you the the cie lab color value
  #   in frame 1 at its pixel location (1,1)
  #
  #   color_at(20,30,5) gives you the cie lab color value
  #   in frame 5 at its pixel location (20,30)
  #   that is the value at its 20-th row and its 30-th column.
  #
  #   row correspond to the height of an image
  #   columns to the widht of an image
  #
  #   last element:
  #     MetaInfo.height + MetaInfo.height*(MetaInfo.width-1)
  #
  # @param x [Integer] target row idx
  # @param y [Integer] target column idx
  # @param fidx [Integer] target frame idx
  # @return [Point3f] cie lab color value.
  def color_at(x, y, fidx)
    raise "Index should be > 0 but (x=#{x},y=#{y},fidx=#{fidx})" if x<1 or y<1 or fidx<1
    lab = @lab_files[fidx-1]
    lookup_idx = (y-1)*MetaInfo.height + x
    color_value = lab[lookup_idx.to_i]
    return nil if color_value.nil?
    color_value.copy
  end

  # @info: Returns the zero vector in case the color could not be interpolated
  def bilinear_interpolated_color_for(p, frame_idx)
    px_i = p.x.floor
    py_i = p.y.floor
    px_i2 = px_i+1
    py_i2 = py_i+1
    dx = p.x - px_i
    dy = p.y - py_i

    f_00 = color_at(px_i, py_i, frame_idx)
    f_01 = color_at(px_i, py_i2, frame_idx)
    f_10 = color_at(px_i2, py_i, frame_idx)
    f_11 = color_at(px_i2, py_i2, frame_idx)

    return Point3f.new([0.0, 0.0, 0.0]) if [f_00,f_01,f_10,f_11].map(&:nil?).any?

    c_00 = f_00.scale_by((1.0-dx)*(1.0-dy))
    c_01 = f_01.scale_by((1.0-dx)*dy)
    c_10 = f_10.scale_by(dx*(1.0-dy))
    c_11 = f_11.scale_by(dx*dy)

    c_00.add(c_01).add(c_10).add(c_11)
  end

end
