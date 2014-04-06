package se.hardchee.docker.proxy

import scala.concurrent.duration._

import akka.actor.{Actor, Props, ActorSystem}
import akka.io.{IO, Tcp}
import akka.pattern.ask
import akka.util.Timeout

import spray.can.Http
import spray.http.{HttpRequest, HttpResponse}
import spray.routing.HttpServiceActor

import java.nio.charset.StandardCharsets.UTF_8

import se.hardchee.docker.api.v1_10

class ProxyService(val targetHost: String, val targetPort: Int) extends HttpServiceActor with v1_10 {
  def receive: Receive = runRoute {
    v1_10 ~
    dynamic {
      println(">=====")
      scheme("http") {
        extract(_.request) { request =>
          import context.{system, dispatcher}
          implicit val timeout: Timeout = 5.seconds

          val uri = request.uri
          val entity = request.entity

          val newUri = uri.withHost(targetHost).withPort(targetPort)
          val newRequest = HttpRequest(request.method, uri=newUri, entity=request.entity, protocol=request.protocol)

          println(s"Request: $newRequest")
          println(s"Entity: $entity")

          onSuccess((IO(Http) ? newRequest).mapTo[HttpResponse]) { resp =>
            val json: String = resp.entity.data.asString(UTF_8)
            println(s"Response: $json")
            println("<=====")
            complete(json)
          }
        }
      }
    }
  }
}

class Starter(targetHost: String, targetPort: Int, bindHost: String, bindPort: Int) extends Actor {
  import context.system
  val handler = system.actorOf(Props(classOf[ProxyService], targetHost, targetPort), name = "handler")

  IO(Http) ! Http.Bind(handler, bindHost, bindPort)
  def receive: Receive = {
    case Tcp.Bound(c) => println(s"Got Bound($c)")
  }
}

object Main extends App {
  val (targetHost, targetPort) = args.headOption.getOrElse("127.0.0.1:8888").split(":") match { case Array(h, p) => (h, p.toInt) }
  val (bindHost, bindPort) = ("localhost", 8080)

  implicit val system = ActorSystem()

  val starter = system.actorOf(Props(classOf[Starter], targetHost, targetPort, bindHost, bindPort), name = "starter")
}
