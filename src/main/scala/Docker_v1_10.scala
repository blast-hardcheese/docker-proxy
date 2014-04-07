package se.hardchee.docker.api

import scala.concurrent.duration._

import akka.actor.ActorContext
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import spray.can.Http
import spray.http.{HttpMethod, HttpRequest, HttpResponse, HttpEntity, Uri}
import spray.routing.HttpServiceActor

import se.hardchee.docker.api.types._
import se.hardchee.docker.proxy.ProxyService
import se.hardchee.docker_scala.converters.GoStringConverters

trait Common {
  self: HttpServiceActor =>

  def makeRequest(method: HttpMethod, uri: Uri, entity: HttpEntity)(implicit context: ActorContext) = {
    implicit val timeout: Timeout = 5.seconds
    import context.{system, dispatcher}

    onSuccess((IO(Http) ? HttpRequest(uri=uri, entity=entity)).mapTo[HttpResponse]) { resp =>
      val strippedHeaders = resp.headers.filterNot({ h => h.name == "Content-Length" || h.name == "Date" || h.name == "Content-Type" })
      complete(HttpResponse(
        status=resp.status,
        entity=resp.entity,
        headers=strippedHeaders,
        protocol=resp.protocol
      ))
    }
  }
}

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
