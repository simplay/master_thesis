require 'java'
require 'thread'

java_import 'java.util.concurrent.Callable'
java_import 'java.util.concurrent.FutureTask'
java_import 'java.util.concurrent.LinkedBlockingQueue'
java_import 'java.util.concurrent.ThreadPoolExecutor'
java_import 'java.util.concurrent.TimeUnit'

class FlowTask

  include Callable

  def initialize(dataset, fnames)
    @dataset = dataset
    @fnames = fnames
  end

  def call
    dataset_fnames = @fnames
    compute_flow(dataset_fnames, @dataset, "Forward Flow")
    dataset_fnames.reverse!
    compute_flow(dataset_fnames, @dataset, "Backward Flow")
  end

  protected

  def compute_flow(dataset_fnames, dataset, text)
    total = dataset_fnames.count
    #puts "Computing #{text} for dataset #{dataset}..."
    dataset_fnames.each_with_index do |_, idx|
      if idx+1 < total
        @i1 = dataset_fnames[idx]
        @i2 = dataset_fnames[idx+1]
        system("./#{flow_method}")
      end
    end
  end

  def flow_method
    raise MethodError.new "Not implemented yet."
  end

end

class LdofFlowTask < FlowTask

  def flow_method
    "ldof/ldof #{@i1} #{@i2}"
  end

end

class SrsfFlowTask < FlowTask
  def flow_method
    idx1 = @i1.split("/").last.split(".").first
    idx2 = @i2.split("/").last.split(".").first
    cmd = "srsf/semirigSF #{idx1} #{idx2} 1 #{@dataset}"
    puts cmd
    cmd
  end
end
