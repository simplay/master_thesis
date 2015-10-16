dataset = ARGV[0]
from_idx = ARGV[1]
to_idx = ARGV[2]
folder_path = "data/#{dataset}/"
dataset_fnames = Dir["#{folder_path}*.ppm"]
dataset_fnames = dataset_fnames.reject {|fname| fname.include? 'LDOF.ppm'}
len = dataset_fnames.length

# compute forward flow
puts "computing forward flow for dataset #{dataset}..."
(0..len-2).each do |idx|
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
