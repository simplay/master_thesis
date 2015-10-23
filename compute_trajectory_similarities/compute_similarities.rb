require 'rubygems'
require 'optparse'
require_relative 'fileparser'

OUT_BASEPATH = "../output/trajectories/"
VERSION = '0.0.1'

flagged = []
usage_example_text = "E.g. Enter 'ruby -J-Xmx8000m compute_similarities.rb -f traj_out_car2_fc_18.txt'"
user_args = {}
opt_parser = OptionParser.new do |opt|
  opt.banner = usage_example_text
  opt.separator ""

  opt.on("-d", "--debug d", Integer, "Debug mode d that should be used when running the script.") do |debug|
    user_args[:debug] = debug
  end

  opt.on("-f", "--file f", String, "Dataset f that should be used located at '../output/trajectories/'.") do |file|
    user_args[:file] = file
  end

  opt.on("-v", "--variance v", Integer, "Should we use the local variance. By default true") do |variance|
    user_args[:variance] = variance
  end

  opt.on_tail("-h", "--help", "Show this message") do
    puts opt
    exit
  end
  opt.on_tail("--version", "Show version") do
    puts "#{VERSION}"
    exit
  end
end
begin
  opt_parser.parse!
  required_args = [:file]
  required_args.each do |arg|
    if user_args[arg].nil?
      flagged << arg
    end
  end
  raise OptionParser::MissingArgument unless flagged.empty?
rescue OptionParser::MissingArgument
  puts "Incorrect input argument(s) passed\n"
  flagged.each do |flagged_arg|
    puts "=> Required argument --#{flagged_arg.to_s} not passed to script."
  end
  puts opt_parser.help
  exit
end

use_local_variance = (user_args[:variance] == 0) ? false : true

filepath = OUT_BASEPATH + user_args[:file]
fp = Fileparser.new(filepath, user_args[:debug], use_local_variance)
