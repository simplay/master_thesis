class LabeledJobNode
  def initialize(klass, label, query, collection = nil)
    @klass = klass
    @label = label
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
    @klass.new("#{@label} #{selection}")
  end
end
