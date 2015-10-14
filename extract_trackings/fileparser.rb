require_relative 'trajectory_manager'
require_relative 'point'
require 'pry'

class Fileparser

  OUT_PATH = "../output/trajectories/"
  RUN_DEBUG_MODE = false

  # @param filepath [String] path to target tracking files.
  def initialize(filepath, mode)
    @tm = TrajectoryManager.new
    $run_debug_mode = mode
    @filepath = filepath
    parse
  end

  def parse
    @file_count = 0
    Dir.foreach(@filepath) do |filename|
      # skip hidden files or those included in the BLACKLIST
      next if filename =~ /^\..*/ or in_blacklist?(filename)
      puts "parsing file #{filename.to_s}..." if $run_debug_mode
      File.open(@filepath+filename, "r") do |file|
        file_id = filename.split("_").last.split(".txt").first.to_i
        parse_file_lines(file, file_id)
        @file_count = @file_count + 1
      end
      puts "finished parsing file #{filename}" if $run_debug_mode
    end
  end

  BLACKLIST = ['global_variances',
               'local_variances'
  ]
  # Is a given filename in the global BLACKLIST.
  def in_blacklist?(filename)
    BLACKLIST.any? do |not_allowed_word|
      filename.include? not_allowed_word
    end
  end

  def write_file
    output_filename = @filepath.split("/").last+"_fc_#{@file_count}"
    puts "writing trajectory file ..."
    filename = "#{OUT_PATH}traj_out_#{output_filename}.txt"
    generate_trajectory_file(filename)
    puts "wrote trajectories into file #{filename}"
  end

  def generate_trajectory_file(name)
    File.open(name, 'w') do |file|
      file.write(@tm.to_s)
    end
  end

  def perform_trajectory_sanity_check
    @tm.trajectories.values.all? &:points_ok?
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
      clean_row = line.split("]").first.split("[").last
      row_items = clean_row.split(" ")
      label = row_items[1].to_i
      point_data = row_items[2..5].map &:to_f
      point = Point.new(point_data)
      @tm.append_trajectory_point(label, file_id, point)
    end
  end

end
