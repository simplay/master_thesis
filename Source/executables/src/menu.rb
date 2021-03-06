class Menu

  FLOW = "1"
  INIT = "2"
  CORE = "3"
  SC = "4"
  KL = "5"
  EXIT = "-1"

  MODES = {
    FLOW => "Generating motion flow fields...",
    INIT => "Extracting relevant core data...",
    CORE => "Generating similarity matrix...",
    SC => "Running Spectral clustering...",
    KL => "Running Kernighan Lin...",
    EXIT => "Exiting Program..."
  }

  def initialize
    puts "Motion Segmentation Pipeline Script"
    puts "==================================="

    loop do
      display_menu_selection
      puts ""
      print "Selection: "
      selection = gets.chomp

      system("clear")
      SelectionState.flush

      case selection
      when FLOW
        puts MODES[selection]
        flow = RubyScript.new("../compute_flows/", "run.rb")
        args = [
          PlainStrArg.new("-guided")
        ]
        flow.execute(args)
      when INIT
        puts MODES[selection]
        init_data = MatlabScript.new("../initialize_data/", "run_init_data")
        args = InitJob.new.args
        init_data.execute(args)
      when CORE
        puts MODES[selection]
        core = JavaProgram.new("./", "core.jar")
        args = CoreJob.new.args
        core.execute(args)

      when SC
        puts MODES[selection]
        sc = MatlabScript.new("../segmentation/", "run_sc")
        args = ScJob.new.args
        args2 = [
          StrArg.new("example"),
          StrArg.new("pd_top_30"),
          IntArg.new(2),
          IntArg.new(3),
          StrArg.new("pd_top")
        ]
        sc.execute(args)

      when KL
        puts MODES[selection]
        kl = JavaProgram.new("./segmentation/", "kernighan_lin.jar")
        args = KerLinJob.new.args
        kl.execute(args)

      when EXIT
        puts MODES[selection]
        break
      else
        puts "Unknown selection #{selection}"
      end
    end
  end

  def display_menu_selection
    puts ""
    puts "==================================="
    puts "Please enter the number of a run-mode\nlisted below in the brackets."
    puts "==================================="

    # list all menu nodes
    max_key_len = MODES.keys.map(&:length).max
    MODES.each do |key, value|
      del = " " * (max_key_len - key.length)
      puts "+ #{del}[#{key}] #{value}"
    end
  end
end
