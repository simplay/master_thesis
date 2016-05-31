class ScriptArg

  def initialize(type, value)
    @type = type
    @value = value.to_s
  end

  def type
    @type
  end

  def to_arg
    ""
  end

end

class IntArg < ScriptArg

  def initialize(value) 
    super(:integer, value)
  end

  def to_arg
    "#{@value}"
  end
end

class StrArg < ScriptArg
  def initialize(value)
    super(:string, value)
  end

  def to_arg
    "\'#{@value}\'"
  end
end

class PlainStrArg < StrArg
  def initialize(value)
    super(value)
  end

  def to_arg
    "#{@value}"
  end
end

