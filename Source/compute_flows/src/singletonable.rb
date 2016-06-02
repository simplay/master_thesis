# Singletonable models the ability of being a singleton.
# A class is supposed to extend the Singletonable module in order
# to become a singleton. Being a singleton ensures to work always with
# the same instance of the Singletonable class.
#
# @example
#
#   class ASingletonClass
#     extend Singletonable
#   end
module Singletonable

  # Returns a singleton of the extending class
  # All state handling of a singleton class happens via
  # invocations on #singleton instead working directling on a direct
  # instance.
  #
  # @hint: Allows to have only one unique instance of this object.
  # @return [self] new instance of type of extending class.
  def singleton(*args)
    @unique_instance ||= itself(*args)
  end

  # Itself returns a new instance of the extending class.
  #
  # @return [self] a new instance of the extending class.
  def itself(*args)
    self.new(*args)
  end

  # Obtain internal singleton instance. That is the object that represents the class singleton
  # instance when calling AClass#singleton
  #
  # @return [self] internal singleton instance.
  def singleton_instance
    @unique_instance
  end

  # Flush internal singleton instance, i.e. set internal singleton instance to nil.
  # @hint: When calling Singletonable#singleton for the first time, the internal
  #   singleton instance is nil and a new singleton instance is build. Calling flush
  #   allows to have a new singleton instance. Useful For GameSettings when starting
  #   a new game during runtime.
  def flush
    @unique_instance = nil
  end

end
