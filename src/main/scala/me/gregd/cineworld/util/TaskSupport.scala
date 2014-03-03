package me.gregd.cineworld.util

import java.util.{TimerTask, Timer}

trait TaskSupport {

  lazy val defaultTimer = TaskSupport.timer

  def schedule(task : => Any, frequency : Int, delay: Int = 0)(
    implicit timer: Timer = defaultTimer
  ) = timer.scheduleAtFixedRate(
    new TimerTask { def run() = task},
    delay,
    frequency
  )

}

object TaskSupport {
  implicit val timer = new Timer()

  implicit class TimeDSL(num:Int) {
    def seconds = num * 1000
    def minutes = num.seconds * 60
    def hours   = num.minutes * 60
  }

}

