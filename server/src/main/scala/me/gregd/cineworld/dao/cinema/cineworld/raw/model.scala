package me.gregd.cineworld.dao.cinema.cineworld.raw

package object model {
  case class CinemaResp(excode: Int, id: Long, addr: String, idx: Int, n: String, pn: Option[String], long: Double, lat: Double, url: String)
  case class MovieResp(dur: Int, BD: Seq[Day], code: String, rdesc: String, TYP: Seq[String], rfn: String, rid: Int, rn: String, rtn: String, n: String, TYPD: Seq[String])
  case class Day(date: String, P: Seq[Showing], d: Long)
  case class Showing(dt: Long, dub: Short, sub: Short, sold: Boolean, code: Int, vn: String, is3d: Boolean, dattr: String, time: String, attr: String, vt: Int)
}
