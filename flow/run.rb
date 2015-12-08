require 'java'
require_relative 'src/loader'

dataset = ARGV[0] # name of subfolder in folder 'data'
from_idx = ARGV[1] # first image index, first image has index 1
to_idx = ARGV[2] # last image index, has index total image count
skip_comp = ARGV[3].nil? ? false : (ARGV[3].to_i == 1)

Loader.new(dataset, from_idx, to_idx, skip_comp)
