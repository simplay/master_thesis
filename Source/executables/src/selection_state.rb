class SelectionState

  def self.instance
    @instance ||= SelectionState.new
  end

  def self.flush
    @instance = nil
  end

  def self.contains?(item)
    instance.history.find do |selection|
      item == selection
    end
  end

  def self.empty?
    instance.history.empty?
  end

  def initialize
    @user_selections = []
  end

  def previous_selection
    history.last
  end

  def append_to_history(item)
    @user_selections << item
  end

  def history
    @user_selections
  end

end
