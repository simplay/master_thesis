require_relative 'fileparser'
OUT_BASEPATH = "../output/trajectories/"
filepath = OUT_BASEPATH + ARGV.first.to_s
run_in_debug_mode = ARGV[1].nil? ? false : true
fp = Fileparser.new(filepath, run_in_debug_mode)
