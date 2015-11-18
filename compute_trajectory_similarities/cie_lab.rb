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

  # Fetch the CIE l*a*b* color value at a given frame for a given location.
  #
  # @info: first index of any frame, row and column is equal to 1.
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
    lab[lookup_idx.to_i]
  end

  # @return [Point3f] bilinearly interpolated cie lab color value.
  def bilinear_interpolated_color

  end

end
