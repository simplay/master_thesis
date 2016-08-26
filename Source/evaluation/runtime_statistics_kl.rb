require 'pry'
STATISTICS_FILE_PATH = '../output/logs/'
OUTPUT_FILE_PATH = '../output/runtimes/'
timestamp = Time.now.to_i

measurements = []
Dir["#{STATISTICS_FILE_PATH}*kl*"].each do |file_path_name|
  file_content = File.read(file_path_name)
  continue if file_content.nil?
  structured_content = File.read(file_path_name).split(/\n/)
  cluster_count = structured_content[0].scan(/-cc\s(\d)+/).first.first.to_i
  iters = (1..cluster_count-1).inject(0) { |sum, item| sum += item }
  trajectory_count = (structured_content.find {|line| line.include?("Similarity Reading done: Processed") }).split("Processed").last.split("vertices").first.strip.to_i

  prod_it = iters * trajectory_count
  total_time_elapsed = (structured_content.find {|line| line.include?("Total elapsed time") }).split(":").last.split("seconds").first.strip.to_f
  measurements << [prod_it, total_time_elapsed]
end
measurements = measurements.reject do |sample|
  sample[0] == 0 || sample[1] == 0
end

# refine extracted data
measurements = measurements.sort_by do |sample|
  sample.first
end

# output avg time per trajectory
item_count = measurements.count
aggregated_count = measurements.inject(0) do |combine, item|
  combine = combine + item[0].to_i
end
avg_item_count = aggregated_count.to_f / item_count
aggregated_time = measurements.inject(0) do |combine, item|
  combine = combine + item[1].to_i
end
avg_time = aggregated_time.to_f / item_count
puts "AVG time per trajectory: #{avg_time.to_f / avg_item_count}s"

# Write to output
fname = "kl_measurments_#{timestamp}.txt"
fpname = "#{OUTPUT_FILE_PATH}#{fname}"
File.open(fpname, 'w') do |file|
  measurements.each do |sample|
    file.write("#{sample[0]} #{sample[1]}\n")
  end
end
puts "Wrote statistics file `#{fpname}`."
