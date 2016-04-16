package me.gregd.cineworld

import me.gregd.cineworld.domain.{Performance, Movie}

package object frontend {
  type Entry = (Movie, List[Performance])
}
