class InitJob
  def initialize
    list = FileList.new("../../Data/").collect
    boolean_list = {0 => "no", 1 => "yes"}
    sampling_list = {
      1 => "every pixe",
      2 => "every 2nd pixel",
      4 => "every 4th pixel",
      5 => "every 5th pixel",
      6 => "every 6th pixel",
      8 => "every 8th pixel",
      10 => "every 10th pixel"
    }
    jobs = [
      JobNode.new(StrArg, "Select a Dataset:", list),
      JobNode.new(IntArg, "Which Sampling Rate should be used:", sampling_list),
      JobNode.new(IntArg, "Should Tracking Data be extracted:", boolean_list),
      JobNode.new(IntArg, "Should Flow Variance be extracted:", boolean_list),
      JobNode.new(IntArg, "Should Color data be extracted:", boolean_list),
      JobNode.new(IntArg, "Should Depth field be extracted:", boolean_list),
      JobNode.new(IntArg, "Shoud Depth Variances be extracted:", boolean_list)
    ]
    @nodes = jobs.map(&:build)
  end

  def args
    @nodes
  end
end
