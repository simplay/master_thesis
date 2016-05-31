class JavaProgram

  def initialize(path_to_script, script_name)
    @path_to_script = path_to_script
    @script_name = script_name
  end

  def execute(arg_container)
    args = serialize_args(arg_container)
    run_matlab = <<-SCRIPT
      java -jar #{@script_name} #{args}
    SCRIPT
    system("cd #{@path_to_script} && " + run_matlab.lstrip)
  end

  def serialize_args(arg_container)
    arg_items = arg_container.map(&:to_arg)
    arg_items = arg_items.compact
    arg_items.join(" ")
  end
end
