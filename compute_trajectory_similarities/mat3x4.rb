require_relative 'point3f'
class Mat3x4

  # @param items [Array] of Float values.
  #   items of a 3x4 matrix in row first order.
  def initialize(items)
    @data = items.each_slice(4).first
  end

  def row_at(row_idx)
    raise "Using incorrect row idx #{row_idx}" if row_idx < 1
    @data[row_idx-1]
  end

  # @param p [Point 3]
  def mult(vec)
    components = [1,2,3].map do |row_idx|
      row_k = row_at(row_idx)
      row_k[0]*vec.x + row_k[1]*vec.y + row_k[2]*vec.z + row_k[3]
    end
    Point3f.new(components)
  end
end
