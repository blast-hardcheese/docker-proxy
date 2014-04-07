package se.hardchee.docker_scala.converters

import spray.httpx.unmarshalling.FromStringDeserializer
import spray.httpx.unmarshalling.MalformedContent

trait GoStringConverters {
  implicit val goString2BooleanConverter = new FromStringDeserializer[Boolean] {
    def apply(value: String) = value match {
      case "1" | "true" | "True"   ⇒ Right(true)
      case "0" | "false" | "False" ⇒ Right(false)
      case x                       ⇒ Left(MalformedContent("'" + x + "' is not a valid Boolean value"))
    }
  }
}

trait ToStringSerializers {
  abstract class ToStringSerializer[T] {
    def apply(a: T): String
  }

  implicit val Boolean2StringConverter = new ToStringSerializer[Boolean] {
    def apply(v: Boolean) = v.toString
  }

  implicit val Int2StringConverter = new ToStringSerializer[Int] {
    def apply(v: Int) = v.toString
  }

  implicit val Long2StringConverter = new ToStringSerializer[Long] {
    def apply(v: Long) = v.toString
  }

  implicit def transform[T, U](v: Option[T])(implicit ev: ToStringSerializer[T]): Option[String] = {
    v.map(ev.apply)
  }
}
