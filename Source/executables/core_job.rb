require_relative "labeled_job_node"
class CoreJob

  # PlainStrArg.new("-d example"),
  # PlainStrArg.new("-task 2"),
  # PlainStrArg.new("-var 1"),
  # PlainStrArg.new("-nn 30"),
  # PlainStrArg.new("-nnm top"),
  # PlainStrArg.new("-lambda 0.1")
  def initialize
    datasets = FileList.new("../output/tracker_data/").collect
    tasks = [1, 2, 3, 4, 5]
    jobs = [
      LabeledJobNode.new(PlainStrArg, "-d", "Select a dataset:", datasets),
      LabeledJobNode.new(PlainStrArg, "-task", "Which task should be run:", tasks),
      LabeledJobNode.new(PlainStrArg, "-var", "Should the local variance used: "),
      LabeledJobNode.new(PlainStrArg, "-nn", "Number of nearest neighbors"),
      LabeledJobNode.new(PlainStrArg, "-nnm", "NN mode:"),
      LabeledJobNode.new(PlainStrArg, "-lambda", "lambda value:")
    ]
    @nodes = jobs.map(&:build)
  end

  def args
    @nodes
  end
end
