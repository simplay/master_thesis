class LabeledJobNode
  def initialize(klass, label, query, collection = nil, use_index_value = false)
    @klass = klass
    @label = label
    @query = query
    @uses_hash = false
    if collection.is_a? Hash
      @uses_hash = true
      @collection = collection.values
      @keys = collection.keys
    else
      @collection = collection
    end
    @use_index_value = use_index_value
    @should_run_skip_check = false
  end

  def set_skip_check(history_idx, expected_values)
    @history_idx = history_idx
    @expected_values = expected_values
    @should_run_skip_check = true
  end

  def run_skip_check?
    @should_run_skip_check
  end

  def build
    return if should_skip?
    puts @query
    unless @collection.nil?
      @collection.each_with_index do |item, idx|
        puts "[#{idx}] #{item}"
      end
    end
    print "Selection: "
    selection = gets.chomp
    unless @collection.nil?
      sel_idx = selection.to_i
      selection = options[sel_idx]
      selection = (sel_idx + 1) if use_index?
    end
    @klass.new("#{@label} #{selection}")
  end

  def should_skip?
    return false unless run_skip_check?
    return false if SelectionState.empty?
    history = SelectionState.instance.history
    @expected_values.include? history[@history_idx]
  end

  def options
    (@uses_hash) ? @keys : @collection
  end

  def use_index?
    @use_index_value
  end
end
