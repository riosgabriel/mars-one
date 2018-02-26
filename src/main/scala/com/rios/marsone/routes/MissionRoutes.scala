package com.rios.marsone.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{pathEnd, pathPrefix, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.rios.marsone.JsonSupport
import com.rios.marsone.actors.ControlCenterActor.{AbortMission, ControlCenterResponse, MissionAborted}

trait MissionRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[MissionRoutes])

  implicit val timeout: Timeout

  def controlCenterActor: ActorRef

  lazy val missionRoutes: Route =
    pathPrefix("mission") {
      pathEnd {
        get {
          val maybeTerminate = (controlCenterActor ? AbortMission).mapTo[ControlCenterResponse]

          onSuccess(maybeTerminate) {
            case MissionAborted(message) =>
              complete(StatusCodes.OK -> ResponseMessage(message))

            case unexpected =>
              log.error(s"Unexpected response=$unexpected")
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
}
