class KerLinJob
  # @example parameter input
  #-d /two_chairs_sed_both_2000 -cc 3 -dc 0 -mic 5 -rc 2 -ipm ebo -prefix test
  def initialize
    datasets = FileList.new("../output/similarities/", ".dat").collect("_sim")
    datasets = datasets.select do |item|
      ["sd", "sed"].any? { |sd_task| item.include?(sd_task) }
    end
    ds_node = LabeledJobNode.new(PlainStrArg, "-d", "Select a dataset:", datasets)
    cc_node = LabeledJobNode.new(PlainStrArg, "-cc", "Enter the number of clusters:")
    dc_node = LabeledJobNode.new(PlainStrArg, "-dc", "Enter the number of dummy nodes:")
    mic_node = LabeledJobNode.new(PlainStrArg, "-mic", "Enter the max. number of iteration per opt. step:")
    rc_node = LabeledJobNode.new(PlainStrArg, "-rc", "Enter the nuber of opt. repetitions:")
    jobs = [
      ds_node,
      cc_node,
      dc_node,
      mic_node,
      rc_node
    ]
    @nodes = jobs.map(&:build).compact
  end

  def args
    ipm = PlainStrArg.new("-ipm ebo")
    @nodes << ipm
  end

end
