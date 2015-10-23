require 'rubygems'
require 'optparse'
require_relative 'fileparser'

OUT_BASEPATH = "../output/trajectories/"
VERSION = '0.0.1'

usage_example_text = "ruby -J-Xmx8000m compute_similarities.rb -p traj_out_car2_fc_18.txt -d 1"
user_args = {}
opt_parser = OptionParser.new do |opt|
  opt.banner = usage_example_text
  opt.separator ""

  opt.on("-d", "--debug d", Integer, "Debug mode that should be used when running the script.") do |debug|
    user_args[:debug] = debug
  end

  opt.on("-f", "--file f", String, "Dataset that should be used located at '../output/trajectories/'") do |file|
    user_args[:file] = file
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
  raise OptionParser::MissingArgument if user_args[:file].nil?
rescue OptionParser::MissingArgument
  puts "Incorrect input argument(s) passed\n"
  puts opt_parser.help
  exit
end

filepath = OUT_BASEPATH + user_args[:file]
fp = Fileparser.new(filepath, user_args[:debug])
