package me.gregd.cineworld.dao.cinema.cineworld.raw

package object model {
  case class CinemaResp(id: Int, n: String, long: Double, lat: Double)
  case class MovieResp(BD: Seq[Day], code: String, TYP: Seq[String], n: String, TYPD: Seq[String])
  case class Day(date: String, P: Seq[Showing], d: Long)
  case class Showing(dt: Long, dub: Short, sub: Short, sold: Boolean, code: String, vn: String, is3d: Boolean, dattr: String, time: String, attr: String, vt: Int)
}
