package fakes

import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.model.TmdbMovie
import me.gregd.cineworld.domain.Movie

import scala.concurrent.Future

object FakeTheMovieDB extends TheMovieDB(null, null) {


  override def alternateTitles(m: Movie): Seq[String] = Nil

  override def fetchImdbId(tmdbId: String): Future[Option[String]] = Future.successful(None)

  override def fetchNowPlaying(): Future[List[TmdbMovie]] = Future.successful(
    List(
      TmdbMovie(
        Some("/45Y1G5FEgttPAwjTYic6czC9xCn.jpg"),
        false,
        "In the near future, a weary Logan cares ",
        "2017-03-02",
        List(28.0, 18.0, 878.0),
        263115,
        "Logan",
        "en",
        "Logan",
        Some("/5pAGnkFYSsFJ99ZxDIYnhQbQFXs.jpg"),
        134.791894,
        1648.0,
        false,
        7.7
      ),
      TmdbMovie(
        Some("/aoUyphk4nwffrwlZRaOa0eijgpr.jpg"),
        false,
        "Explore the mysterious and dangerous hom",
        "2017-03-10",
        List(878.0, 28.0, 12.0, 14.0),
        293167,
        "Kong: Skull Island",
        "en",
        "Kong: Skull Island",
        Some("/pGwChWiAY1bdoxL79sXmaFBlYJH.jpg"),
        81.879768,
        621.0,
        false,
        6.1
      ),
      TmdbMovie(
        Some("/tnmL0g604PDRJwGJ5fsUSYKFo9.jpg"),
        false,
        "A live-action adaptation of Disney's ver",
        "2017-03-17",
        List(14.0, 10749.0),
        321612,
        "Beauty and the Beast",
        "en",
        "Beauty and the Beast",
        Some("/6aUWe0GSl69wMTSWWexsorMIvwU.jpg"),
        74.721658,
        537.0,
        false,
        7.2
      ),
      TmdbMovie(
        Some("/1SwAVYpuLj8KsHxllTF8Dt9dSSX.jpg"),
        false,
        "A young black man visits his white girlf",
        "2017-03-17",
        List(35.0, 27.0, 9648.0, 53.0),
        419430,
        "Get Out",
        "en",
        "Get Out",
        Some("/aVyJnGubW0OBvqmqVg2z2wCuEsn.jpg"),
        14.59733,
        360.0,
        false,
        7.0
      ),
      TmdbMovie(
        Some("/hm0Z5tpRlSzPO97U5e2Q32Y0Xrb.jpg"),
        false,
        "European mercenaries searching for black",
        "2017-02-17",
        List(28.0, 12.0, 14.0, 53.0),
        311324,
        "The Great Wall",
        "en",
        "The Great Wall",
        Some("/rym1ecQJVj1NhZnigRePtiusorF.jpg"),
        12.619775,
        390.0,
        false,
        5.9
      ),
      TmdbMovie(
        Some("/cDbEiJIRwFcx2GsClJ1hDUY5Vwj.jpg"),
        false,
        "An account of Boston Police Commissioner",
        "2017-02-23",
        List(18.0, 36.0, 53.0),
        388399,
        "Patriots Day",
        "en",
        "Patriots Day",
        Some("/tiBL4PeaCPKGBz3qO4dJP2KzKop.jpg"),
        11.382296,
        170.0,
        false,
        6.7
      ),
      TmdbMovie(
        Some("/h2mhfbEBGABSHo2vXG1ECMKAJa7.jpg"),
        false,
        "The six-member crew of the International",
        "2017-03-24",
        List(27.0, 878.0, 53.0),
        395992,
        "Life",
        "en",
        "Life",
        Some("/hES8wGmkxHa54z7hqUMpw5TIs09.jpg"),
        10.868581,
        10.0,
        false,
        5.5
      ),
      TmdbMovie(
        Some("/y5KrW9mxeUmxUIYwNZOgnkYKQ8y.jpg"),
        false,
        "A group of high-school kids, who are inf",
        "2017-03-24",
        List(28.0, 12.0, 878.0),
        305470,
        "Power Rangers",
        "en",
        "Power Rangers",
        Some("/eQkaPwMpJARFoPvbbNz2Z0Kye4O.jpg"),
        10.505247,
        9.0,
        false,
        7.8
      ),
      TmdbMovie(
        Some("/zkXnKIwX5pYorKJp2fjFSfNyKT0.jpg"),
        false,
        "John Wick is forced out of retirement by",
        "2017-02-17",
        List(53.0, 28.0, 80.0),
        324552,
        "John Wick: Chapter 2",
        "en",
        "John Wick: Chapter 2",
        Some("/4TBLjAhQe1zJfR3zdHMWTrwbdLd.jpg"),
        10.174384,
        870.0,
        false,
        6.6
      ),
      TmdbMovie(
        Some("/byeTgTgG7M1RN2c7njWWIkSkNig.jpg"),
        false,
        "An ambitious young executive is sent to ",
        "2017-03-24",
        List(18.0, 27.0, 9648.0, 53.0),
        340837,
        "A Cure for Wellness",
        "en",
        "A Cure for Wellness",
        Some("/6oXUTHVAjPrNKvVFZhxNlWv7jua.jpg"),
        5.156399,
        170.0,
        false,
        5.5
      )
    )
  )
}
