class InitJob
  def initialize
    list = FileList.new("../../Data/").collect
    jobs = [
      JobNode.new(StrArg, "Select Dataset", list),
      JobNode.new(IntArg, "Sampling Rate"),
      JobNode.new(IntArg, "Tracking Data"),
      JobNode.new(IntArg, "Flow Variance"),
      JobNode.new(IntArg, "Color"),
      JobNode.new(IntArg, "Depth"),
      JobNode.new(IntArg, "Depth Variances")
    ]
    @nodes = jobs.map(&:build)
  end

  def args
    @nodes
  end
end
