//package me.gregd.cineworld.util
//
//import scala.collection.mutable
//import shapeless.Generic
//
///**
//* Created by Greg Dorrell on 05/04/2014.
//*/
//class Convertions {
//  type Converter[A,B] = A => B
//  type Mappable[T] = {
//    def apply: {
//      def tupled(t:T)
//    }
//    def unapply: Option[T]
//  }
//  var converters = mutable.Map()[(Class[_],Class[_]),Converter]
//
//  implicit def caseClassConverter[A:TypeTag,B:TypeTag] = {
//    val aMems = typeOf[A].members.filter(_.isMethod).filter(_.asMethod.isCaseAccessor)
//    val bMems = typeOf[B].members.filter(_.isMethod).filter(_.asMethod.isCaseAccessor)
//
//  }
//
//  def register[A <: Mappable, B <: Mappable]: Unit = {
//    converters += {
//      classOf[A].get
//    }
//  }
//}
//
//object Convertions extends Convertions {
//  def convert[A,B](from:A): B = {
//    val aToB = (b:B) =>  Generic[A].from(Generic[B].to(b))
//  }
//}