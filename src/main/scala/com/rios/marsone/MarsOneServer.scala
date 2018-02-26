package com.rios.marsone

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.rios.marsone.actors.ControlCenterActor
import com.rios.marsone.routes.Routes
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object MarsOneServer extends App with Routes {

  implicit val system: ActorSystem = ActorSystem("marsOneHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val extension = MarsOneExtension(system)
  val settings: MarsOneSettings = extension.marsOneSettings

  val config = ConfigFactory.load()

  implicit val executionContext: ExecutionContext = system.dispatcher

  implicit lazy val timeout: Timeout = Timeout(5 seconds)

  val controlCenterActor: ActorRef = system.actorOf(ControlCenterActor.props, "controlCenterActor")

  val serverBindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(routes, settings.http.interface, settings.http.port)

}
