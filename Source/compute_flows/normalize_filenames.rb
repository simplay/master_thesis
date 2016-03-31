require 'pry'
dataset = ARGV[0]
path = "data/" + dataset
counter = 1
fname_pairs = {}
File.open("#{path}associations.txt", 'r') do |file|
  while line = file.gets
    fname = file.gets.strip.split(" ").last.split("/").last
    fname_pairs[counter] = fname
    counter = counter + 1
  end
end
fname_pairs.each do |key, value|
  ext = "."+value.split(".").last
  binding.pry
  File.rename(path+value, path + key.to_s + ext)
end
