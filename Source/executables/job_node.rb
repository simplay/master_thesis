class JobNode
  def initialize(klass, query, collection = nil)
    @klass = klass
    @query = query
    @collection = collection
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
      selection = @collection[selection.to_i]
    end
    @klass.new(selection)
  end

end
