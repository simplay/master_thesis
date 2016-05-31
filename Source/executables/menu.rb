require_relative 'matlab_script.rb'
require_relative 'script_arg'
class Menu

  CORE = "1"
  SC = "2"
  EXIT = "-1"

  MODES = {
    CORE => "generate similarity matrix",
    SC => "spectral clustering",
    EXIT => "exit program"
  }

  def initialize
    puts "Motion Segmentation Pipeline Script"
    puts "==================================="
    puts "Please enter the number of a run-mode listed below in the brackets."

    # list all menu nodes
    max_key_len = MODES.keys.map(&:length).max
    MODES.each do |key, value|
      del = " " * (max_key_len - key.length)
      puts "+ #{del}[#{key}] #{value}"
    end

    loop do
      selection = gets.chomp
      case selection
      when SC
        puts "Running spectral clustering..."
        sc = MatlabScript.new("../segmentation/", "run_sc")
        args = [
          StrArg.new("example"),
          StrArg.new("pd_top_30"),
          IntArg.new(2),
          IntArg.new(3),
          StrArg.new("pd_top")
        ]
        sc.execute(args)
      when EXIT
        break
      end
    end
  end
end
