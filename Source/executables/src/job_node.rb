class JobNode
  def initialize(klass, query, collection = nil, use_index_value = false)
    @klass = klass
    @query = query
    @collection = collection
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
      selection = @collection[sel_idx]
      selection = (sel_idx + 1) if use_index?
    end
    @klass.new(selection)
  end

  def use_index?
    @use_index_value
  end

end
