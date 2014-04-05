package se.hardchee.docker.proxy

import akka.actor.{Actor, Props, ActorSystem}
import akka.io.{IO, Tcp}

import spray.can.Http
import spray.routing.HttpServiceActor

class ProxyService(target: String) extends HttpServiceActor {
  def receive: Receive = runRoute {
    dynamic {
      complete("")
    }
  }
}

class Starter(target: String, bindHost: String, bindPort: Int) extends Actor {
  import context.system
  val handler = system.actorOf(Props(classOf[ProxyService], target), name = "handler")

  IO(Http) ! Http.Bind(handler, bindHost, bindPort)
  def receive: Receive = {
    case Tcp.Bound(c) => println(s"Got Bound($c)")
  }
}

object Main extends App {
  val target = args.headOption.getOrElse("tcp://127.0.0.1:8888")
  val (bindHost, bindPort) = ("localhost", 8080)

  implicit val system = ActorSystem()

  val starter = system.actorOf(Props(classOf[Starter], target, bindHost, bindPort), name = "starter")
}
