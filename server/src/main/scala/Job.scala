import me.gregd.cineworld.CinemaService
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}

import scala.concurrent.Future

object Job extends App {
  val cinemaService: CinemaService = _

  val eventualCinemas = cinemaService.getCinemas()

  val eventualListings: Future[List[(Cinema, Map[Movie, List[Performance]])]] = eventualCinemas.flatMap(cinemas =>
    Future.traverse(cinemas)(cinema =>
      cinemaService.getMoviesAndPerformances(cinema.id, "today").map(
        cinema -> _
      )
    )
  )

  eventualListings.map(listings =>
    for {
      (cinema, performances) <- listings
    } yield
      ()
  )

}
