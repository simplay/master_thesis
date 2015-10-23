# generate optical flow field


# Select target dataset for generating trackings
datasets = Dir["data/ldof/**"]
datasets.each_with_index do |fname, idx|
  dataset = fname.split("/").last
  puts "[#{idx}] puts #{fname}"
end
puts "Select target dataset: "
selected = gets.chomp.to_i
selected_dataset = datasets[selected].split("/").last

puts "Starting computing trackings for '#{selected_dataset}'"
