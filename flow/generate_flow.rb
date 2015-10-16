require 'pry'
dataset = ARGV[0]
from_idx = ARGV[1]
to_idx = ARGV[2]

folder_path = "data/#{dataset}/"
dataset_fnames = Dir["#{folder_path}*.ppm"]
dataset_fnames = dataset_fnames.reject {|fname| fname.include? 'LDOF.ppm'}
len = dataset_fnames.length

# minus 2 since the first item in a ruby
#array has index 0 and we also want to access to end element (i.e. idx+1).
lower = 0
upper = len-2

# set lower and upper index
lower = from_idx.to_i unless from_idx.nil?
upper = to_idx.to_i unless to_idx.nil?

if lower < 0 or upper >= len
  raise "Error: invalid indices passed!"
end

# convert input images to png
blacklist = [".ppm", ".flo", ".txt"]
input_imgs_fnames = Dir["#{folder_path}*.*"].reject do |fname|
  blacklist.any? do |forbidden_name|
    fname.include?(forbidden_name)
  end
end

# find out file extension and convert to ppm images.
# requires Imagemagick
unless input_imgs_fnames.empty?
  fextension = input_imgs_fnames.first.split(".").last
  system("mogrify -format ppm #{folder_path}/*.#{fextension}")
end

# compute forward flow
puts "computing forward flow for dataset #{dataset}..."
(lower..upper).each do |idx|
  i1 = dataset_fnames[idx]
  i2 = dataset_fnames[idx+1]
  puts "computing forward flow from #{i1} to #{i2}"
  system("./ldof/ldof #{i1} #{i2}")
end

# rename generated forward flow files such that they exhibit the fwf_ prefix
flow_files = Dir["#{folder_path}*.flo"]
flow_files = flow_files.reject {|fnames| fnames.include? "fwf" or fnames.include? "bwf"}
flow_files.each do |fname|
  filename = File.basename(fname, File.extname(fname))
  new_name = "fwf_"+filename
  File.rename(fname, folder_path + new_name + File.extname(fname))
end

# reverse dataset in order to compute the backward flow
dataset_fnames.reverse!

# compute backward flow
puts "Start computing backward flow for dataset #{dataset}..."
(0..len-2).each do |idx|
  i1 = dataset_fnames[idx]
  i2 = dataset_fnames[idx+1]
  puts "computing backward flow from #{i1} to #{i2}"
  system("./ldof/ldof #{i1} #{i2}")
end

# rename generated forward flow files such that they exhibit the fwf_ prefix
flow_files = Dir["#{folder_path}*.flo"]
flow_files = flow_files.reject {|fnames| fnames.include? "fwf" or fnames.include? "bwf"}
flow_files.each do |fname|
  filename = File.basename(fname, File.extname(fname))
  new_name = "bwf_"+filename
  File.rename(fname, folder_path + new_name + File.extname(fname))
end

# generate complete used_input.txt file
# is used for tracking
File.open("#{folder_path}used_input.txt", 'w') do |file|
  file.puts "#use"
  file.puts "1\n#{len-1}"
  file.puts "#imgs"
  imgs = Dir["#{folder_path}*.ppm"].reject do |fname|
    fname.include?("LDOF")
  end

  imgs.each do |img|
    file.puts img.split("/").last
  end
  file.puts "#fwf"
  imgs = Dir["#{folder_path}*.flo"].select do |fname|
    fname.include?("fwf")
  end

  imgs.each do |img|
    file.puts img.split("/").last
  end
  file.puts "#bwf"
  imgs = Dir["#{folder_path}*.flo"].select do |fname|
    fname.include?("bwf")
  end

  imgs.each do |img|
    file.puts img.split("/").last
  end
end

puts "Generated forward-and backward flows and full 'used_input.txt' file"


