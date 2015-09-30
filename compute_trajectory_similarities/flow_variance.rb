class FlowVariance

  def self.build(filepath=nil)
    @singleton ||= FlowVariance.new(filepath)
  end

  # assumption: first frame has index 1
  def self.at_frame(idx)
    build.global_variances[idx-1]
  end

  def initialize(filepath)
    @global_variances = []
    File.open(filepath, "r") do |file|
      while(line = file.gets)
        @global_variances << line.strip.to_f
      end
    end
  end

  def global_variances
    @global_variances
  end

end
