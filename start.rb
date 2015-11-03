require 'pry'
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

# tracking script parameters
dataset = "'#{selected_dataset}'";
step_size = 8;
mode = 5;
display = false;
write_tracking_files = true;
sig_s = 8;
sig_r = 5;
show_video = false;
compute_trackings = false;

function_call = "run_tracking(#{dataset}, #{step_size}, #{compute_trackings}, #{mode}, #{display}, #{write_tracking_files}, #{sig_s}, #{sig_r}, #{show_video})"
matlab_call = "tracking/#{function_call}"
run_matlab = "matlab -nodisplay -nosplash -nodesktop -r \"run('${PWD}/#{matlab_call}'); quit\""
run_matlab = "matlab -nodisplay -nosplash -nodesktop -r \"run('#{matlab_call}'); quit\""
binding.pry
