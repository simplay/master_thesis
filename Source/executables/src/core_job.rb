class CoreJob

  # PlainStrArg.new("-d example"),
  # PlainStrArg.new("-task 2"),
  # PlainStrArg.new("-var 1"),
  # PlainStrArg.new("-nn 30"),
  # PlainStrArg.new("-nnm top"),
  # PlainStrArg.new("-lambda 0.1")
  def initialize
    datasets = FileList.new("../output/tracker_data/").collect
    tasks = ["SD", "PD", "PED", "SED", "PAED"]
    nn_modes = ["top", "both", "all"]
    use_variance = {0 => "no", 1 => "yes"}
    jobs = [
      LabeledJobNode.new(PlainStrArg, "-d", "Select a dataset:", datasets),
      LabeledJobNode.new(PlainStrArg, "-task", "Which task should be run:", tasks, true),
      LabeledJobNode.new(PlainStrArg, "-var", "Should the local variance used:", use_variance),
      LabeledJobNode.new(PlainStrArg, "-nn", "Number of nearest neighbors"),
      LabeledJobNode.new(PlainStrArg, "-nnm", "NN mode:", nn_modes),
      LabeledJobNode.new(PlainStrArg, "-lambda", "lambda value:")
    ]
    @nodes = jobs.map(&:build)
  end

  def args
    @nodes
  end
end
