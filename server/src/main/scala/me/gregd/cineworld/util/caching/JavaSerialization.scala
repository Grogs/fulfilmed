package me.gregd.cineworld.util.caching

import java.io.{ByteArrayOutputStream, ObjectOutputStream, ByteArrayInputStream, ObjectInputStream}
import me.gregd.cineworld.domain.Movie

object JavaSerialization {

  def createSerializer[T]: (T) => Array[Byte] = { t =>
    val b = new ByteArrayOutputStream()
    val o = new ObjectOutputStream(b)
    o.writeObject(t)
    b.toByteArray()
  }

  def createDeserializer[T]: (Array[Byte]) => T = { b =>
    new ObjectInputStream(new ByteArrayInputStream(b)).readObject().asInstanceOf[T]
  }

}
