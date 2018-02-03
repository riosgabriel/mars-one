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

import scala.concurrent.Future

trait RoversRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[RoversRoutes])

  implicit val timeout: Timeout

  def controlCenterActor: ActorRef

  // improve these ask calls
  lazy val roverRoutes: Route =
    pathPrefix("rovers") {
      pathEnd {
        concat(
          get {
            val maybeRovers = (controlCenterActor ? GetRovers).mapTo[Rovers]
            complete(maybeRovers)
          },
          post {
            entity(as[Rover]) { rover =>
              val roverDeployed: Future[Action] = (controlCenterActor ? DeployRover(rover)).mapTo[Action]

              onSuccess(roverDeployed) {
                case ActionPerformed(description) =>
                  complete(StatusCodes.Created -> description)

                case ActionNotPerformed(description) =>
                  complete(StatusCodes.PreconditionFailed -> description)
              }
            }
          }
        )
      }
    }
}
