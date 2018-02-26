package com.rios.marsone.routes

import akka.http.scaladsl.server.Directives.{pathPrefix, _}
import akka.http.scaladsl.server.Route

trait Routes extends PlateauRoutes with RoversRoutes with MissionRoutes {

  lazy val routes: Route =
    pathPrefix("api" / "v1") {
      plateauRoutes ~ roverRoutes ~ missionRoutes
    }

}
