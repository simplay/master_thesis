require 'pry'
class Loader

  def initialize(dataset, from_idx, to_idx, skip_comp)
    run_singlethreaded = true
    folder_path = "data/#{dataset}/"
    genarate_normalized_images(folder_path, skip_comp)
    fnames = sorted_dataset_fnames(folder_path)
    lower, upper = lookup_indices(from_idx, to_idx, fnames)
    generate_flows(fnames, dataset, lower, upper, skip_comp, run_singlethreaded)
    generate_association_file(folder_path, lower, upper)
  end

  private

  def generate_association_file(folder_path, lower, upper)
    # generate complete used_input.txt file
    # is used for tracking

    File.open("#{folder_path}used_input.txt", 'w') do |file|
      file.puts "#use"
      file.puts "#{lower+1}\n#{upper+1}"
      file.puts "#imgs"
      imgs = Dir["#{folder_path}*.ppm"].reject do |fname|
        fname.include?("LDOF")
      end
      len = imgs.count
      imgs = imgs.sort_by do |a| a.split("/").last.to_i end
      nupper = upper
      nupper = upper + 1 if upper < len-1
      imgs[lower..nupper].each do |img|
        file.puts img.split("/").last
      end
      file.puts "#fwf"
      imgs = Dir["#{folder_path}*.flo"].select do |fname|
        fname.include?("fwf")
      end
      imgs = imgs.sort_by do |a| a.split("_").last.split("L").first.to_i end

      imgs.each do |img|
        file.puts img.split("/").last
      end
      file.puts "#bwf"
      imgs = Dir["#{folder_path}*.flo"].select do |fname|
        fname.include?("bwf")
      end
      imgs = imgs.sort_by do |a| a.split("_").last.split("L").first.to_i end
      imgs.each do |img|
        file.puts img.split("/").last
      end
    end
  end


  def generate_flows(filepath_names, dataset, lower, upper, skip_comp, run_singlethreaded)
    unless skip_comp
      if run_singlethreaded
        index_range = (lower..upper).map { |idx| (idx-lower) }
        dataset_fnames = filepath_names[lower..(upper+1)]
        compute_flow(dataset_fnames, dataset, index_range, "Forward Flow")
        rename_generated_flows(dataset_fnames, "fwf")
        dataset_fnames.reverse!
        compute_flow(dataset_fnames, dataset, index_range, "Backward Flow")
        rename_generated_flows(dataset_fnames, "bwf")
      else
        puts "Not implemented yed"
      end
    end
  end

  def compute_flow(dataset_fnames, dataset, range, text)
    puts "Computing #{text} for dataset #{dataset}..."
    range.each do |idx|
      i1 = dataset_fnames[idx]
      i2 = dataset_fnames[idx+1]
      puts "Computing #{text} from #{i1} to #{i2}."
      system("./ldof/ldof #{i1} #{i2}")
    end
  end

  def rename_generated_flows(folder_path, flow_prefix)
    flow_files = Dir["#{folder_path}*.flo"]
    flow_files = flow_files.reject {|fnames| fnames.include? "fwf" or fnames.include? "bwf"}
    flow_files.each do |fname|
      filename = File.basename(fname, File.extname(fname))
      new_name = "#{flow_prefix}_"+filename
      File.rename(fname, folder_path + new_name + File.extname(fname))
    end
  end

  # Converts a given set of images to .ppm image files.
  #
  # @info: The input image set can be in an arbitry, but known image format.
  #   this method creates copies of the input images being in the .ppm file format.
  #   Makes use of Imagemagick's mogrify function.
  #
  # @param filepath [String] relative path to image dataset.
  # @param should_skip [Boolean] should we skip this converstion step.
  def genarate_normalized_images(filepath, should_skip)
    if should_skip
      puts "skipping computing flow fields"
    else
      blacklist = [".ppm", ".flo", ".txt"]
      input_imgs_fnames = Dir["#{filepath}*.*"].reject do |fname|
        blacklist.any? do |forbidden_name|
          fname.include?(forbidden_name)
        end
      end

      unless input_imgs_fnames.empty?
        fextension = input_imgs_fnames.first.split(".").last
        system("mogrify -format ppm #{filepath}/*.#{fextension}")
      end
    end
  end

  # Get am arry of all sorted file names.
  #
  # @info: Assumption: Image filenames are well enumarated.
  #   Filenames are sorted ascending.
  #
  # @param filepath [String] relative path to image dataset.
  # @return [Array<String>] set of sorted image filenames.
  def sorted_dataset_fnames(filepath)
    dataset_fnames = Dir["#{filepath}*.ppm"]
    dataset_fnames = dataset_fnames.reject { |fname| fname.include? 'LDOF.ppm' }
    dataset_fnames.sort_by { |a| a.split("/").last.to_i }
  end

  # Get dataset lookup indices range.
  #
  # @param from_idx [Ingeger, nil] lower given lookup index
  # @param to_idx [Integer, nil] upper given lookup index
  # @return [Integer] the lower lookup indices.
  # @return [Integer] the upper lookup indices.
  def lookup_indices(from_idx, to_idx, dataset_fnames)
    len = dataset_fnames.length
    lower = 0
    # Minus 2 since the first item in a ruby
    # Array has index 0 and we also want to access to end element (i.e. idx+1).
    upper = len-2
    lower = from_idx.to_i-1 unless from_idx.nil?
    upper = to_idx.to_i-2 unless to_idx.nil?
    if lower < 0 or upper-lower+1 >= len
      raise "Error: invalid indices passed!"
    end

    return lower, upper
  end


end
