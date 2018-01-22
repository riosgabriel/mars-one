package com.rios.marsone.routes

import scala.concurrent.Future

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{ pathEnd, pathPrefix, put, _ }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ delete, get, post }
import akka.http.scaladsl.server.directives.RouteDirectives.complete

import com.rios.marsone.actors.ControlCenterActor.GetRovers
import com.rios.marsone.JsonSupport
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import com.rios.marsone.actors.Rovers

trait RoversRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[RoversRoutes])

  def controlCenterActor: ActorRef

  implicit lazy val timeout: Timeout = Timeout(5.seconds)

  lazy val roverRoutes: Route =
    pathPrefix("rovers") {
      pathEnd {
        get {
          val users: Future[Rovers] =
            (controlCenterActor ? GetRovers).mapTo[Rovers]

          complete(users)
        } ~
        post {
          complete(StatusCodes.OK)
        } ~
        put {
          complete(StatusCodes.OK)
        } ~
        delete {
          complete(StatusCodes.OK)
        }
      }
    }
}
