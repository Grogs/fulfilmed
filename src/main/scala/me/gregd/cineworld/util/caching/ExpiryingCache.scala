package me.gregd.cineworld.util.caching

/**
 * Cache entries will be considered after some duration.
 * Cache entries will not be removed - merely ignored
 */
trait ExpiryingCache[T] { self: DatabaseCache[T] =>

  val maxAge: Long

  /**
   * A hook to be notified whe a cache entry is known to have expired
   *
   * Currently this will only be called when a attempt is made to access the expired entry
   */
  def expire(entry: CacheEntry): Unit = {}

  override def predicate(e: CacheEntry) = hasExpired(e)

  def hasExpired(e: CacheEntry): Boolean = {
    val now = System.currentTimeMillis
    val hasExpiried = (now - e.timestamp) < maxAge
    if (hasExpiried) expire(e)
    hasExpiried
  }
}
