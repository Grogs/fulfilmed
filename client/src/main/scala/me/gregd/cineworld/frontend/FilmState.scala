package me.gregd.cineworld.frontend

import me.gregd.cineworld.domain.{Movie, Performance}

sealed abstract class Sort(val key: String, val ordering: Ordering[Entry])
case object NextShowing extends Sort("showtime", Ordering.by{
  case (_, performances) => if (performances.isEmpty) "99:99" else performances.map(_.time).min
})
case object ImdbRating extends Sort("imdb", Ordering.by{ e: Entry => e._1.rating.getOrElse(0.0)}.reverse)
case object RTCriticRating extends Sort("critic", Ordering.by{ e: Entry => e._1.criticRating.getOrElse(0)}.reverse)
case object RTAudienceRating extends Sort("audience", Ordering.by{ e: Entry => e._1.audienceRating.getOrElse(0)}.reverse)

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
  films: Map[Movie, List[Performance]],
  sort: Sort,
  dates: List[Date],
  selectedDate: Date,
  controller: Controller
)
