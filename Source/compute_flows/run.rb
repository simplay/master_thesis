require 'java'
require_relative 'src/loader'

require 'pry'

if ARGV[0] == '-guided'
  datasets = Dir["../../Data/*"].entries.select { |e| File.directory? e }
  puts "Select a target dataset by its number in brackets:"
  datasets.each_with_index do |ds, idx|
    puts "[#{idx}] #{ds.split("Data/").last}"
  end
  printf "Selected dataset: "
  dataset_idx = STDIN.gets.chomp.to_i
  dataset = datasets[dataset_idx] + "/"
  puts "Path to selected dataset: #{dataset}"

  puts "Select Flow method by its number in brackets"
  Loader::FLOW_METHODS.each_with_index do |method, idx|
    puts "[#{idx}] #{method}"
  end
  printf "Selected method: "
  method_idx = STDIN.gets.chomp.to_i
  selected_method = Loader::FLOW_METHODS[method_idx]
  puts "Selected flow method name: #{selected_method}"
else
  dataset = ARGV[0] # name of subfolder in folder 'data'
  selected_method = ARGV[1] # which variant should be run
end
from_idx = ARGV[2] # first image index, first image has index 1
to_idx = ARGV[3] # last image index, has index total image count
skip_comp = ARGV[4].nil? ? false : (ARGV[3].to_i == 1)
if dataset.nil? or selected_method.nil?
  raise ArgumentError.new "No dataset or no variant passed."
end

Loader.new(dataset, selected_method, from_idx, to_idx, skip_comp)
