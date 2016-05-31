require_relative 'matlab_script.rb'
require_relative 'script_arg'
require 'pry'

# MatlabScript.new("../segmentation/", "test").execute({:int => 1, :string => "foobar"})

sc = MatlabScript.new("../segmentation/", "run_sc")
args = [
  ScriptArg.new(:string, "example"),
  ScriptArg.new(:string, "pd_top_30"),
  ScriptArg.new(:int, 2),
  ScriptArg.new(:int, 3),
  ScriptArg.new(:string, "pd_top")
]
binding.pry
sc.execute(args)
