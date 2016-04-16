package me.gregd.cineworld.frontend

import me.gregd.cineworld.domain.{Movie, Performance}

import scala.collection.immutable.List

sealed abstract class Sort(val key: String, val description: String, val ordering: Ordering[Entry])
case object NextShowing extends Sort("showtime", "Next Showing", Ordering.by{
  case (_, performances) => if (performances.isEmpty) "99:99" else performances.map(_.time).min
})
case object ImdbRating extends Sort("imdb", "IMDb Rating (Descending)", Ordering.by{ e: Entry => e._1.rating.getOrElse(0.0)}.reverse)
case object RTCriticRating extends Sort("critic", "RT Critic Rating (Descending)", Ordering.by{ e: Entry => e._1.criticRating.getOrElse(0)}.reverse)
case object RTAudienceRating extends Sort("audience", "RT Audience Rating (Descending)", Ordering.by{ e: Entry => e._1.audienceRating.getOrElse(0)}.reverse)

case class Controller(render: FilmsState => Unit) {

//  def sortBy(s: Sort)(implicit state: FilmsState) = {
//    type Entry = (Movie, List[Performance])
//    val ordering = s match {
//      case Unordered => None
//      case NextShowing => None
//      case ImdbRating => Some(Ordering.by{ e: Entry => e._1.rating.getOrElse(0.0)}.reverse)
//      case RTAudienceRating => Some(Ordering.by{ e: Entry => e._1.audienceRating.getOrElse(0)}.reverse)
//      case RTCriticRating => Some(Ordering.by{ e: Entry => e._1.criticRating.getOrElse(0)}.reverse)
//    }
//    render(state)
//  }
}

case class Date(key: String, text: String)
object Today extends Date("today", "Today")
object Tomorrow extends Date("tomorrow", "Tomorrow")

case class FilmsState(
  isLoading: Boolean,
  cinema: String,
  films: Map[Movie, List[Performance]],
  sorts: List[Sort] = List(NextShowing, ImdbRating, RTCriticRating, RTAudienceRating),
  selectedSort: Sort = NextShowing,
  dates: List[Date] = List(Today, Tomorrow),
  selectedDate: Date = Today
)
