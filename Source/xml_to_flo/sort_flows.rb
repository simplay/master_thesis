require 'fileutils'

dataset = ARGV[0]
fpath = "output/#{dataset}"
flow_files = Dir["#{fpath}/*.flo"]

fwf = flow_files.select do |fname|
  fname_elements = fname.split("_")
  fname_elements[1].to_i < fname_elements[2].split(".flo").first.to_i
end

fwf = fwf.sort_by do |item|
  item.split("_").last.split(".flo").first.to_i
end

bwf = flow_files.select do |fname|
  fname_elements = fname.split("_")
  fname_elements[1].to_i > fname_elements[2].split(".flo").first.to_i
end

bwf = bwf.sort_by do |item|
  item.split("_").last.split(".flo").first.to_i
end

File.open("#{fpath}/used_input.txt", 'w') do |file|
  file.puts "#use"
  file.puts 1
  file.puts fwf.count
  file.puts "#imgs"
  imgs = fwf.map do |fname|
    fname.split("flow_").last.split("_").first
  end
  imgs.each do |fname|
    file.puts fname+".ppm"
  end

  file.puts "#fwf"
  (fwf.map { |fname| fname.split("/").last }).each do |fname|
    file.puts fname
  end

  file.puts "#bwf"
  (bwf.map { |fname| fname.split("/").last }).each do |fname|
    file.puts fname
  end
end
