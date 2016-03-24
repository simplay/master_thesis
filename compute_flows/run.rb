require 'java'
require_relative 'src/loader'

dataset = ARGV[0] # name of subfolder in folder 'data'
variant = ARGV[1].to_i # which variant should be run
from_idx = ARGV[2] # first image index, first image has index 1
to_idx = ARGV[3] # last image index, has index total image count
skip_comp = ARGV[4].nil? ? false : (ARGV[3].to_i == 1)
if dataset.nil? or variant.nil?
  raise ArgumentError.new "No dataset or no variant passed."
end
Loader.new(dataset, variant, from_idx, to_idx, skip_comp)
