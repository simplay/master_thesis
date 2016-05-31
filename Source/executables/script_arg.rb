class ScriptArg

  def initialize(type, value)
    @type = type
    @value = value
  end

  def to_arg
    case @type
    when :int
      "#{@value}"
    when :string
      "\'#{@value}\'"
    else
      ""
    end
  end

end

