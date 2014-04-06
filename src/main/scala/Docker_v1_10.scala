package se.hardchee.docker.api

import spray.routing.HttpServiceActor

import se.hardchee.docker.api.types._
import se.hardchee.docker.proxy.ProxyService
import se.hardchee.docker_scala.converters.GoStringConverters

trait v1_10 extends HttpServiceActor with GoStringConverters {
  self: ProxyService =>

  val v1_10 =
    pathPrefix("v1.10") {
      dynamic {
        path("containers" / "json") {
          case class QueryString(all: Option[Boolean], before: Option[ContainerID], limit: Option[Int], since: Option[ContainerID], size: Option[Boolean])

          parameters('all.as[Boolean] ?, 'before.as[ContainerID] ?, 'limit.as[Int] ?, 'since.as[ContainerID] ?, 'size.as[Boolean] ?).as(QueryString) { params =>
            complete("Placeholder")
          }
        }
      }
    }
}
