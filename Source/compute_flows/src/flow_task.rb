require 'java'
require 'thread'
require_relative 'system_information'

java_import 'java.util.concurrent.Callable'
java_import 'java.util.concurrent.FutureTask'
java_import 'java.util.concurrent.LinkedBlockingQueue'
java_import 'java.util.concurrent.ThreadPoolExecutor'
java_import 'java.util.concurrent.TimeUnit'

class FlowTask

  include Callable

  def initialize(dataset, fnames, flow_store_path)
    @dataset = dataset
    @fnames = fnames
    @path = flow_store_path + "/"
  end

  def call
    dataset_fnames = @fnames
    compute_flow(dataset_fnames, @dataset, "Forward Flow", true)
    dataset_fnames.reverse!
    compute_flow(dataset_fnames, @dataset, "Backward Flow", false)
  end

  protected

  def compute_flow(dataset_fnames, dataset, text, is_fwf)
    total = dataset_fnames.count
    dataset_fnames.each_with_index do |_, idx|
      if idx+1 < total
        @i1 = dataset_fnames[idx]
        @i2 = dataset_fnames[idx+1]
        system("#{flow_method(is_fwf)}")
      end
    end
  end

  def flow_method
    raise MethodError.new "Not implemented yet."
  end

end

class LdofFlowTask < FlowTask

  def flow_method(is_fwf)
    f_name = @i1.split(".ppm").first + "LDOF.flo"
    prefix = (is_fwf) ? "fwf_" : "bwf_"
    elements = f_name.split("/")
    ren_cmd = "mv #{f_name} #{@path + prefix + elements.last}"
    puts "#{ren_cmd}"
    "./ldof/#{ldof_binary} #{@i1} #{@i2} && #{ren_cmd}"
  end

  def ldof_binary
    if SystemInformation.running_on_mac?
      'ldof'
    elsif SystemInformation.running_on_linux?
      'ldof_linux'
    end
  end

end

class HsFlowTask < FlowTask

  def flow_method(is_fwf)
    f_name = @i1.split(".ppm").first + "LDOF"
    prefix = (is_fwf) ? "fwf_" : "bwf_"
    elements = f_name.split("/")
    out_name = prefix + elements.last
    hs = MatlabScript.new('hs/', 'generateFlow')
    del = "../"
    args = [del + @i1, del + @i2, del + @path, out_name]
    hs.execute(args)
  end

end

class SrsfFlowTask < FlowTask
  def flow_method(is_fwf)
    idx1 = @i1.split("/").last.split(".").first
    idx2 = @i2.split("/").last.split(".").first
    dataset_path = "../" + @path.split("srsf/").first

    # Apply a rigid and non-rigid motion estimation
    # skip all depths cues further apart than 240 cm
    cmd = "cd srsf/ && ./semirigSF #{dataset_path} #{idx1} #{idx2} 2 2 3 1 2 1 240 10"
    puts cmd
    cmd
  end
end

# MatlabScript is a wrapper class allowing to run any matlab script in (J)RUBY.
class MatlabScript

  # Create a new runable matlab script.
  #
  # @example given a matlab script `test.m` in `../segmentation/`
  #   MatlabScript.new("../segmentation/", "test")
  # @param path_to_script [String] relative path to target matlab script.
  # @param script_name [String] name of script without its file extension.
  def initialize(path_to_script, script_name)
    @path_to_script = path_to_script
    @script_name = script_name
  end

  # Executes this script using a given argument list.
  #
  # @example given a matlab script `test.m` in `../segmentation/``"
  #   MatlabScript.new("../segmentation/", "test").execute({:int => 1, :string => "foobar"})}
  #   #=> "1 <=> foobar"
  # @param arg_container [Array<ScriptArg>]
  def execute(arg_container)
    args = serialize_args(arg_container)
    run_matlab = <<-SCRIPT
      matlab -nosplash -nodesktop -r \"#{@script_name}(#{args}); exit\"
    SCRIPT
    system("cd #{@path_to_script} && pwd && " + run_matlab.lstrip)
  end

  # Generate argument string of target matlab script using a given array of arguments.
  #
  # The hash-keys hint the type of the argument.
  #
  # @param arg_list [Array<String>]
  # @return [String] matlab argument that can be used for a target script.
  def serialize_args(arg_list)
    (arg_list.map { |item| "\'#{item}\'"}).join(", ")
  end

end
