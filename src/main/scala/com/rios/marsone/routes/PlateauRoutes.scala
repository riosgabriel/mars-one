package com.rios.marsone.routes

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.rios.marsone.JsonSupport
import com.rios.marsone.model.Plateau

trait PlateauRoutes extends JsonSupport {

  implicit def system: ActorSystem

  private lazy val log = Logging(system, classOf[PlateauRoutes])

  lazy val plateauRoutes: Route =
    pathPrefix("plateau") {
        pathEnd {
          get {
            complete(Plateau(5, 5))
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
