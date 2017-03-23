import com.google.inject.AbstractModule
import monix.execution.Scheduler

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Scheduler]).toInstance(monix.execution.Scheduler.global)
  }
}
