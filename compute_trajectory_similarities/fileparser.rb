require_relative 'trajectory_manager'
require_relative 'point'
require_relative 'similarity_matrix'
require_relative 'flow_variance'
require 'pry'

class Fileparser

  OUT_PATH = "../output/trackings/"
  RUN_DEBUG_MODE = false
  IS_TRA_DEBUG = true

  # @param filepath [String] path to target tracking files.
  def initialize(filepath)
    @tm = TrajectoryManager.new
    @sim_mat = SimilarityMatrix.new(@tm)
    @filepath = filepath
    parse
    dataset = filepath.split("out_").last.split("_").first
    $global_ds_name = dataset
    FlowVariance.build(OUT_PATH+dataset+"/"+"global_variances.txt")


    puts "computing sims for traj"
    a = @sim_mat.trajectory_similarities_for(2033)
    top_n_neighbors = @tm.most_sim_neighbors_of_trajectory(a, 5)
    binding.pry

    @sim_mat.to_mat unless IS_TRA_DEBUG
  end

  private

  def parse
    File.open(@filepath, "r") do |file|
      frame_count = @filepath.split("fc_").last.split(".").first
      parse_file_lines(file, frame_count)
    end
  end

  def parse_file_lines(file, file_id)
    while(line = file.gets)
      if line =~/###/
        # is a header line
        header = line.split(" ")[1..-1]
        @label = header[0].partition("L:").last.to_i
        @start_frame = header[1].partition("S:").last.to_i
      else
        point_data = line.split(" ").map &:to_f
        point = Point.new(point_data)
        @tm.append_trajectory_point(@label, @start_frame, point)
      end
    end
  end

end
