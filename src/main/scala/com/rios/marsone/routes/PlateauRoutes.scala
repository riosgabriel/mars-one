package com.rios.marsone.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{get, post}
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.rios.marsone.JsonSupport
import com.rios.marsone.actors.ControlCenterActor.{GetPlateau, SetPlateau}
import com.rios.marsone.model.Plateau

trait PlateauRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[PlateauRoutes])

  implicit val timeout: Timeout

  def controlCenterActor: ActorRef

  // Improve the ask (?) calls to bang (!)
  lazy val plateauRoutes: Route =
    pathPrefix("plateau") {
      pathEnd {
        concat(
          get {
            val maybePlateau = (controlCenterActor ? GetPlateau).mapTo[Option[Plateau]]
            complete(maybePlateau)
          },
          post {
            entity(as[Plateau]) { plateau =>
              val maybeSet = controlCenterActor ? SetPlateau(plateau)

              onSuccess(maybeSet) { _ =>
                complete(StatusCodes.Created)
              }
            }
          }
        )
      }
    }

}
