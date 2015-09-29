require_relative 'fileparser'

BASEPATH = "../output/trackings/"
target_datasetpath = ARGV.first.to_s
filepath = BASEPATH + target_datasetpath
Fileparser.new(filepath, false).parse
puts Fileparser.new(filepath, true).perform_trajectory_sanity_check
