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
      matlab -nosplash -nodesktop -r \"run ${PWD}/#{@script_name}(#{args}) ; exit\"
    SCRIPT
    system("cd #{@path_to_script} && " + run_matlab.lstrip)
  end

  # Generate argument string of target matlab script using a given array of arguments.
  #
  # The hash-keys hint the type of the argument.
  #
  # @param arg_container [Array<ScriptArg>]
  # @return [String] matlab argument that can be used for a target script.
  def serialize_args(arg_container)
    arg_items = arg_container.map(&:to_arg)
    arg_items = arg_items.compact
    arg_items.join(", ")
  end

end
