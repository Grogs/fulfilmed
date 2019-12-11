package me.gregd.cineworld.util

import ch.qos.logback.classic.{Logger, LoggerContext}
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import enumeratum._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

case class LogEntry(level: Level, logger: String, thread: String, message: String, stacktrace: Seq[StackFrame])

case class StackFrame(clazz: String, method: String, line: Int)

sealed trait Level extends EnumEntry

object Level extends Enum[Level] {
  val values = findValues

  case object Error extends Level
  case object Warn  extends Level
  case object Info  extends Level
  case object Debug extends Level
}

class InMemoryLog {
  val maxEntries = 100

  private val entries = new java.util.concurrent.ConcurrentLinkedQueue[LogEntry]()

  def log: Seq[LogEntry] = entries.iterator().asScala.toSeq

  def add(logEntry: LogEntry): Unit = {
    entries.offer(logEntry)
    if (entries.size > maxEntries) {
      entries.poll()
    }
  }
}

object InMemoryLog extends InMemoryLog {
  val inMemoryAppender: InMemoryLogbackAppender = new InMemoryLogbackAppender(this)
  inMemoryAppender.setContext(LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext])
  inMemoryAppender.start()

  LoggerFactory.getLogger("ROOT").asInstanceOf[Logger].addAppender(inMemoryAppender)
}

class InMemoryLogbackAppender(log: InMemoryLog) extends AppenderBase[ILoggingEvent] {

  override def append(event: ILoggingEvent): Unit = {
    println(s"Received event $event")
    val entry = for {
      level <- Level.withNameInsensitiveOption(event.getLevel.levelStr)
      logger     = event.getLoggerName
      thread     = event.getThreadName
      msg        = event.getFormattedMessage
      stacktrace = Option(event.getCallerData).toSeq.flatten.map(data => StackFrame(data.getClassName, data.getMethodName, data.getLineNumber))
    } yield LogEntry(level, logger, thread, msg, stacktrace)

    entry.foreach(log.add)
  }
}
