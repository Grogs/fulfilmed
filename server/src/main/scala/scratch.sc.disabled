import scala.concurrent.duration.FiniteDuration



  case class Person[B<:Behaviour](name: String, children: ManagedIO[List[Person[_]]]) {
//    def children: Managed[List[Person], Behaviour] = Managed.apply[Behaviour](Nil)
//    val children: Managed[List[Person[_]], Behaviour] =
  }

  class Dao[B<:Behaviour](managed: B){
    def getPerson(name: String): Person[Behaviour] = Person(name, managed({System.out.flush();println("\n#EVALUATED#\n; ");System.out.flush();Nil}))
  }

  trait ManagedIO[T] {
    def value: T
  }
  object ManagedIO {
    def apply[T](gen: => T)(implicit behaviour: Behaviour) = behaviour(gen)
  }

  trait Behaviour {
    def apply[T](gen: => T): ManagedIO[T]
    def apply[T](io: IO[T]): ManagedIO[T]
  }

  implicit object OnDemand extends Behaviour {
    def apply[T](gen: => T) = new ManagedIO[T] {def value = gen}  }

  class Cached(debounce: FiniteDuration, refreshAfterWrite: Option[FiniteDuration]) extends Behaviour {
    def apply[T](gen: => T) = new ManagedIO[T] {def value = gen}
  }

  implicit object Snapshot extends Behaviour {
    def apply[T](gen: => T) = new ManagedIO[T] {val value = gen}
  }

  implicit object Lazy extends Behaviour {
    def apply[T](gen: => T) = new ManagedIO[T] {lazy val value = gen}
  }


  //    class LiveExecutor[T >: Null <: AnyRef](obs: Observable[T]) extends Executor[Live] {
  //      var value: T = null
  //      obs.sow.map(value = _ )
  //      obs.subscribe(value = _ )
  //    }

    val m = ManagedIO(println(8))(Lazy)
    println(m.value)
  val lazyDao = new Dao(Lazy)
  println("retrieve")
  val pl = lazyDao.getPerson("greg")
  println("retrieved")
  println("access")
  pl.children.value
  println("accessed")
  println("access")
  pl.children.value
  println("accessed")
  println("\n------\n")
val snapshotDao = new Dao(Snapshot)
println("retrieve")
val ps = snapshotDao.getPerson("greg")
println("retrieved")
println("access")
ps.children.value
println("accessed")
println("access")
ps.children.value
println("accessed")
println("\n------\n")
  val onDemandDao = new Dao(OnDemand)
  println("retrieve")
  val pod = onDemandDao.getPerson("greg")
  println("retrieved")
  println("access")
  pod.children.value
  println("accessed")
  println("access")
  pod.children.value
  println("accessed")
//  implicit class ManagedSyntax[T](fun: => T) {
//    def managed[M[_] <: Managed]: M[T] = new M[T](fun)
//  }
//}
