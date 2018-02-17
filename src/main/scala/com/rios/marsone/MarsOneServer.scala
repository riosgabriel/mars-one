package com.rios.marsone

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.rios.marsone.actors.ControlCenterActor
import com.rios.marsone.routes.{ PlateauRoutes, RoversRoutes }
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.io.StdIn

object MarsOneServer extends App
    with PlateauRoutes
    with RoversRoutes {

  implicit val system: ActorSystem = ActorSystem("marsOneHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val config = ConfigFactory.load()

  implicit val executionContext: ExecutionContext = system.dispatcher

  lazy val routes: Route = plateauRoutes ~ roverRoutes

  implicit lazy val timeout: Timeout = Timeout(5 seconds)

  val controlCenterActor: ActorRef = system.actorOf(ControlCenterActor.props, "controlCenterActor")

  val serverBindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(routes, config.getString("interface"), config.getInt("http.port"))

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  StdIn.readLine()

  serverBindingFuture
    .flatMap(_.unbind())
    .onComplete { done =>
      done.failed.map(ex => println(s"Failed unbinding ex=$ex"))
      system.terminate()
    }
}
