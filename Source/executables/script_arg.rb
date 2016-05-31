class ScriptArg

  def initialize(type, value)
    @type = type
    @value = value
    raise ArgumentError, "Value is not of type #{normalized_type}" unless value_has_given_type?
  end

  def to_arg
    ""
  end

  def value_has_given_type?
    klass = Kernel.const_get normalized_type
    @value.is_a? klass
  end

  def normalized_type
    @type.to_s.split("_").collect(&:capitalize).join
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

