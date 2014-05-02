namespace java me.gregd.cineworld.domain.thrift
typedef i32 int

struct Movie {
  1: string title,
  2: string cineworldId,
  3: string format,
  4: optional string imdbId,
  5: optional double rating,
  6: optional int votes,
  7: optional int audienceRating,
  8: optional int criticRating,
}

service CinemaService {
    Movie getMovie(1:string title)
}

