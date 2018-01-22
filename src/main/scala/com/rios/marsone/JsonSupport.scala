package com.rios.marsone

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.rios.marsone.model.{Plateau, Rover}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val plateauJsonFormat: RootJsonFormat[Plateau] = jsonFormat2(Plateau)
  implicit val roverJsonFormat: RootJsonFormat[Rover] = jsonFormat3(Rover)
}
