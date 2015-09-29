require_relative 'fileparser'

BASEPATH = "../output/trackings/"
target_datasetpath = ARGV.first.to_s
filepath = BASEPATH + target_datasetpath
Fileparser.new(filepath, false).write_file
puts "trajectory point check yields: #{Fileparser.new(filepath, true).perform_trajectory_sanity_check}"
