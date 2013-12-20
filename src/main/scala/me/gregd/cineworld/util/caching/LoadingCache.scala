package me.gregd.cineworld.util.caching

/**
 * Created by Greg Dorrell on 14/12/2013.
 */
trait LoadingCache[T] { self: DatabaseCache[T] =>

  val loader: String => T
  override def put(key:String)(value: => T) = throw new NotImplementedError("You should not insert values into a loading cache")
  override def get(key: String): Option[T] = {
    val value = self.get(key)
    value orElse {
      val newValue = loader(key)
      put(key)(newValue)
      Option(newValue)
    }
  }

}
