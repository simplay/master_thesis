require_relative 'trajectory_manager'
require 'pry'

class Fileparser

  # @param filepath [String] path to target tracking files.
  def initialize(filepath)
    @tm = TrajectoryManager.new
    Dir.foreach(filepath) do |filename|
      # skip hidden files
      next if filename =~ /^\..*/
      puts filename.to_s
      File.open(filepath+filename, "r") do |file|
        file_id = filename.split("_").last.split(".txt").first.to_i
        parse_file_lines(file, file_id)
      end
      puts "finished parsing file #{filename}"
    end
  end

  # TRACKED = 0;
  # LABEL = 1;
  # AX = 2;
  # AY = 3;
  # BX = 4;
  # BY = 5;
  # CONT = 7;
  def parse_file_lines(file, file_id)
    while(line = file.gets)
      binding.pry
      clean_row = line.split("]").first.split("[").last
      row_items = clean_row.split(" ")
      label = row_items[1].to_i
      point_data = row_items[2..5]
    end
  end

end
