require_relative 'point3f'
require_relative 'meta_info'

class CieLab

  def self.build(basefilepath=nil)
    @singleton ||= CieLab.new(basefilepath)
  end

  def initialize(filepath)
    @lab_files = []
    Dir[filepath+"/*"].each do |fname|
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
  # @param x [Integer] target row idx
  # @param y [Integer] target column idx
  # @param fidx [Integer] target frame idx
  # @return [Point3f] cie lab color value.
  def color_at(x, y, fidx)
    lab = @lab_files[fidx-1]
    lookup_idx = (y-1)*MetaInfo.width+(x-1)
    lab[lookup_idx]
  end

  # @return [Point3f] bilinearly interpolated cie lab color value.
  def bilinear_interpolated_color

  end

end
