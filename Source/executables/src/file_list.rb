class FileList
  def initialize(file_path, file_extension = "")
    @file_path = file_path
    @file_extension = file_extension
  end

  def collect(substring_to_filter=nil)
    list = Dir["#{@file_path}*#{@file_extension}"]

    list = list.map do |item|
      item.split("/").last.gsub(@file_extension, "")
    end

    if @file_extension.empty?
      list = list.reject do |item|
        item.include?("\.")
      end
    end

    unless substring_to_filter.nil?
      list = list.map do |item|
        item.gsub(substring_to_filter, "")
      end
    end
    list
  end

end
