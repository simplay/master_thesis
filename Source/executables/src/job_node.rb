class JobNode
  def initialize(klass, query, collection = nil, use_index_value = false)
    @klass = klass
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
  end

  def build
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
    @klass.new(selection)
  end

  def options
    (@uses_hash) ? @keys : @collection
  end

  def use_index?
    @use_index_value
  end

end
