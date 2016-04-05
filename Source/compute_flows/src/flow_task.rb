require 'java'
require 'thread'

java_import 'java.util.concurrent.Callable'
java_import 'java.util.concurrent.FutureTask'
java_import 'java.util.concurrent.LinkedBlockingQueue'
java_import 'java.util.concurrent.ThreadPoolExecutor'
java_import 'java.util.concurrent.TimeUnit'

class FlowTask

  include Callable

  def initialize(dataset, fnames, flow_store_path)
    @dataset = dataset
    @fnames = fnames
    @path = flow_store_path + "/"
  end

  def call
    dataset_fnames = @fnames
    compute_flow(dataset_fnames, @dataset, "Forward Flow", true)
    dataset_fnames.reverse!
    compute_flow(dataset_fnames, @dataset, "Backward Flow", false)
  end

  protected

  def compute_flow(dataset_fnames, dataset, text, is_fwf)
    total = dataset_fnames.count
    dataset_fnames.each_with_index do |_, idx|
      if idx+1 < total
        @i1 = dataset_fnames[idx]
        @i2 = dataset_fnames[idx+1]
        system("#{flow_method(is_fwf)}")
      end
    end
  end

  def flow_method
    raise MethodError.new "Not implemented yet."
  end

end

class LdofFlowTask < FlowTask

  def flow_method(is_fwf)
    f_name = @i1.split(".ppm").first + "LDOF.flo"
    prefix = (is_fwf) ? "fwf_" : "bwf_"
    elements = f_name.split("/")
    ren_cmd = "mv #{f_name} #{@path + prefix + elements.last}"
    puts "#{ren_cmd}"
    "./ldof/ldof #{@i1} #{@i2} && #{ren_cmd}"
  end

end

class SrsfFlowTask < FlowTask
  def flow_method(is_fwf)
    idx1 = @i1.split("/").last.split(".").first
    idx2 = @i2.split("/").last.split(".").first
    dataset_path = "../" + @path.split("srsf/").first
    cmd = "cd srsf/ && ./semirigSF #{dataset_path} #{idx1} #{idx2} 1"
    puts cmd
    cmd
  end
end
