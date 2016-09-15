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
    prob_node = LabeledJobNode.new(PlainStrArg, "-prob", "Cut Probability value in [0, 1]:")
    prob_node.set_skip_check(1, ['-task 2', '-task 3', '-task 5'])
    lambda_node = LabeledJobNode.new(PlainStrArg, "-lambda", "Which lambda value:")
    lambda_node.set_skip_check(1, ['-task 1', '-task 4'])
    jobs = [
      LabeledJobNode.new(PlainStrArg, "-d", "Select a dataset:", datasets),
      LabeledJobNode.new(PlainStrArg, "-task", "Which similarity task should be run:", tasks, true),
      LabeledJobNode.new(PlainStrArg, "-var", "Should the local variance used:", use_variance),
      LabeledJobNode.new(PlainStrArg, "-nn", "Number of nearest neighbors to return:"),
      LabeledJobNode.new(PlainStrArg, "-nnm", "NN mode:", nn_modes),
      lambda_node,
      prob_node
    ]
    @nodes = jobs.map(&:build).compact
  end

  def args
    @nodes
  end
end
