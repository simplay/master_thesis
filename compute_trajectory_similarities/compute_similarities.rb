require_relative 'fileparser'
OUT_BASEPATH = "../output/trajectories/"
filepath = OUT_BASEPATH + ARGV.first.to_s
fp = Fileparser.new(filepath)
