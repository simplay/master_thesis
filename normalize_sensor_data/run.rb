require 'fileutils'
require 'pry'
dataset = ARGV[0]
fpname = "input/#{dataset}"
col_img = Dir["#{fpname}img_*.png"]
depth_img = Dir["#{fpname}/depth/depth_*.png"]
FileUtils::mkdir_p 'input/depth'
col_img.each_with_index do |fname, idx|
  fext = fname.split(".").last
  new_fname = "input/#{idx+1}.#{fext}"
  File.rename(fname, new_fname)
end

depth_img.each_with_index do |fname, idx|
  fext = fname.split(".").last
  new_fname = "input/depth/#{idx+1}.#{fext}"
  File.rename(fname, new_fname)
end
