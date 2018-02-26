package com.rios.marsone.routes

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ get, post }
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.rios.marsone.JsonSupport
import com.rios.marsone.actors.ControlCenterActor._
import com.rios.marsone.model.Plateau

trait PlateauRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[PlateauRoutes])

  implicit val timeout: Timeout

  def controlCenterActor: ActorRef

  lazy val plateauRoutes: Route =
    pathPrefix("plateau") {
      pathEnd {
        concat(
          get {
            val maybePlateau = (controlCenterActor ? GetPlateau).mapTo[Option[Plateau]]

            onSuccess(maybePlateau) {
              case Some(plateau) => complete(StatusCodes.OK -> plateau)
              case None => complete(StatusCodes.NotFound)
            }
          },
          post {
            entity(as[Plateau]) { plateau =>
              val maybeSet = controlCenterActor ? SetPlateau(plateau)

              onSuccess(maybeSet) {
                case PlateauSet(message) =>
                  complete(StatusCodes.Created -> ResponseMessage(message))

                case PlateauAlreadySet(message) =>
                  complete(StatusCodes.BadRequest -> ResponseMessage(message))

                case unexpected =>
                  log.error(s"Unexpected response=$unexpected")
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        )
      }
    }

}
