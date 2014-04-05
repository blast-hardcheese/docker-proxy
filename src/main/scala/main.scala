package se.hardchee.docker.proxy

import akka.actor.{Actor, Props, ActorSystem}
import akka.io.{IO, Tcp}

import spray.can.Http
import spray.routing.HttpServiceActor

class ProxyService(targetHost: String, targetPort: Int) extends HttpServiceActor {
  def receive: Receive = runRoute {
    dynamic {
      complete("")
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
