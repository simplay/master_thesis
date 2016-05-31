class ScJob
  def initialize
    datasets = FileList.new("../output/similarities/", ".dat").collect
    splits = datasets.map do |item| item.split("_") end
    datasets = splits.map(&:first).uniq

    puts "please select the number of your target dataset listed in the brackets."
    datasets.each_with_index do |item, idx|
      puts "[#{idx}] #{item}"
    end
    print "Selection: "
    sel_id = gets.chomp
    ds = datasets[sel_id.to_i]


    prefixes = splits.map do |item| item[1..-2].join("_") end
    prefixes = splits.map do |item| (item[1..-2].join("_") if item.first == ds) end
    prefixes = prefixes.compact
    jobs = [
      JobNode.new(StrArg, "Select Input data prefix:", prefixes),
      JobNode.new(IntArg, "Number of eigenvectors:"),
      JobNode.new(IntArg, "Number of clusters:"),
      JobNode.new(StrArg, "Output filename prefix:")
    ]
    @nodes = jobs.map(&:build)
    @nodes = [StrArg.new(ds)] + @nodes
  end

  def args
    @nodes
  end
end
