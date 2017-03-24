import com.google.inject.AbstractModule
import me.gregd.cineworld.util.{Clock, RealClock}
import monix.execution.Scheduler

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Scheduler]).toInstance(monix.execution.Scheduler.global)
    bind(classOf[Clock]).toInstance(RealClock)
  }
}
