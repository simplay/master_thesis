require_relative 'fileparser'

BASEPATH = "../output/trackings/"
target_datasetpath = ARGV.first.to_s
# assumption: first index is 1
from_idx = ARGV[1]
to_idx = ARGV[2]
filepath = BASEPATH + target_datasetpath
fp = Fileparser.new(filepath, false, from_idx, to_idx)
fp.write_file
puts "trajectory point check yields: #{Fileparser.new(filepath, true).perform_trajectory_sanity_check}"
