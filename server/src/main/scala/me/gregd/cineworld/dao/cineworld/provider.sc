import java.time.{Clock => JClock, Instant, ZoneId, LocalDateTime}
import java.time.Clock.systemUTC
trait Provider[T] {
  def next: T
}
class Generator[T](generator: => T) extends Provider[T] {
  def next = generator
}
class Fixed[T](value: T) extends Provider[T] {
  def next = value
}
class Replayer[T](values: Iterator[T]) extends Provider[T] {
  def next = values.next()
}

trait Clock extends Provider[Instant]

class RealClock(underlying: JClock = systemUTC) extends Clock {
  def next = underlying.instant
}