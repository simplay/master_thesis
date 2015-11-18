require_relative 'trajectory_manager'
require_relative 'point'
require_relative 'similarity_matrix'
require_relative 'flow_variance'
require_relative 'depth_field'
require_relative 'point3f'
require_relative 'meta_info'
require_relative 'cie_lab'

require 'pry'

class Fileparser

  OUT_PATH = "../output/trackings/"
  DEPTH_FPATH = "../output/depths/"
  RUN_DEBUG_MODE = false
  IS_TRA_DEBUG = true
  HAS_DEPTH_DATA = false

  # @param filepath [String] path to target tracking files.
  def initialize(filepath, debug_mode, is_using_local_variance, uses_depth_data, use_sum_affinity)
    dataset = filepath.split("out_").last.split("_").first
    MetaInfo.build("../data/ldof/", dataset)
    puts "Selected dataset: "+ filepath.split("/").last
    @debug_mode = debug_mode
    $use_sum_affinity = use_sum_affinity
    $uses_depth_data = uses_depth_data
    $is_debugging = in_simple_mode? ? true : false
    puts "Script runs in debug mode: #{$is_debugging}"
    puts "Summing constraints for computing affinities: #{$use_sum_affinity}"
    FlowVariance.build(OUT_PATH+dataset)
    DepthField.build(DEPTH_FPATH, dataset) if has_depth_data?
    CieLab.build(dataset)
    @tm = TrajectoryManager.new
    puts "Computing affinity matrix..."
    @sim_mat = SimilarityMatrix.new(@tm, is_using_local_variance)
    @filepath = filepath
    parse
    $global_ds_name = dataset

    puts "Using depth information: #{has_depth_data?}"

    if in_demo_mode?
      a = @sim_mat.trajectory_similarities_for(15)
      top_n_neighbors = @tm.most_sim_neighbors_of_trajectory(a, 5)
      binding.pry
    else
      @sim_mat.to_mat
    end
  end

  private

  def in_demo_mode?
    @debug_mode == 2
  end

  def in_simple_mode?
    @debug_mode == 1
  end

  def has_depth_data?
    $uses_depth_data
  end

  def parse
    File.open(@filepath, "r") do |file|has_depth_data?
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
        @frame_idx = @start_frame
      else
        point_data = line.split(" ").map &:to_f
        point = Point.new(point_data)
        next if point.out_of_range?
        if has_depth_data?
          point = Point3f.build_from(point, @frame_idx)
          next if point == false
        end
        @tm.append_trajectory_point(@label, @start_frame, point)
        @frame_idx = @frame_idx + 1
      end
    end
  end

end
