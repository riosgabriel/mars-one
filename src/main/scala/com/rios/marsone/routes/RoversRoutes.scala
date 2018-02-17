package com.rios.marsone.routes

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{ pathEnd, pathPrefix, _ }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ get, post }
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.rios.marsone.JsonSupport
import com.rios.marsone.actors.ControlCenterActor._
import com.rios.marsone.actors.Rovers
import com.rios.marsone.model.Rover

trait RoversRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[RoversRoutes])

  implicit val timeout: Timeout

  def controlCenterActor: ActorRef

  lazy val roverRoutes: Route =
    concat(
      pathPrefix("rovers") {
        pathEnd {
          concat(
            get {
              val maybeRovers = (controlCenterActor ? GetRovers).mapTo[Rovers]
              complete(maybeRovers)
            },
            post {
              entity(as[Rover]) { rover =>
                val maybeDeployed = (controlCenterActor ? DeployRover(rover)).mapTo[ControlCenterResponse]

                onSuccess(maybeDeployed) {
                  case RoverDeployed(_) =>
                    complete(StatusCodes.Created)

                  case DuplicatedRover(message) =>
                    complete(StatusCodes.BadRequest -> ResponseMessage(message))

                  case PlateauNotSet(message) =>
                    complete(StatusCodes.PreconditionFailed -> ResponseMessage(message))

                  case unexpected =>
                    log.error(s"Unexpected response=$unexpected")
                    complete(StatusCodes.InternalServerError)
                }
              }
            }
          )
        }
      },
      pathPrefix("rovers" / IntNumber / "commands") { roverId =>
        pathEnd {
          post {
            entity(as[List[String]]) { commands =>
              val maybeCommands = (controlCenterActor ? Commands(roverId, commands)).mapTo[ControlCenterResponse]

              onSuccess(maybeCommands) {
                case ReceivedCommands(_) =>
                  complete(StatusCodes.Accepted)

                case RoverNotFound(_) =>
                  complete(StatusCodes.NotFound)

                case unexpected =>
                  log.error(s"Unexpected response=$unexpected")
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      }
    )
}
