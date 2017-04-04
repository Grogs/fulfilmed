import com.google.inject.AbstractModule
import me.gregd.cineworld.dao.movies.RatingsCache
import me.gregd.cineworld.util.{Clock, RealClock}
import monix.execution.Scheduler
import org.mapdb._
import scala.collection.JavaConverters._
import scala.concurrent.duration._

class Module extends AbstractModule {

  val ratingsCache = {
//    val db = DBMaker.fileDB("ratingsCache.mapdb").transactionEnable().make()
//    val map = db
//      .hashMap("ratings-cache", Serializer.STRING, Serializer.JAVA.asInstanceOf[Serializer[(Double, Int)]])
//      .expireAfterCreate(1.day.toMillis)
//      .expireAfterUpdate(1.day.toMillis)
//      .createOrOpen()
//    new RatingsCache(map.asScala, db.commit())
    new RatingsCache(collection.mutable.Map(), ())
  }

  override def configure(): Unit = {
    bind(classOf[Scheduler]).toInstance(monix.execution.Scheduler.global)
    bind(classOf[Clock]).toInstance(RealClock)
    bind(classOf[RatingsCache]).toInstance(ratingsCache)
  }
}

