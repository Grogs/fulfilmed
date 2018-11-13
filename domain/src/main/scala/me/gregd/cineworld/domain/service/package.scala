package me.gregd.cineworld.domain
import cats.ApplicativeError

package object service {
  type ApplicativeThrowable[F[_]] = ApplicativeError[F, Throwable]
}
