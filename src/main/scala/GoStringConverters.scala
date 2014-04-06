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
