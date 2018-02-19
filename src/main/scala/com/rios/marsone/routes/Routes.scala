package com.rios.marsone.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.{ pathPrefix, _ }

trait Routes extends PlateauRoutes with RoversRoutes {

  lazy val routes: Route =
    pathPrefix("api" / "v1") {
      plateauRoutes ~ roverRoutes
    }

}
