package com.rios.marsone

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.rios.marsone.actors.Rovers
import com.rios.marsone.model.{ CardinalDirection, Plateau, Rover }
import fommil.sjs._
import shapeless._
import spray.json.{ DefaultJsonProtocol, RootJsonFormat }

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol with FamilyFormats {

  implicit val cardinalDirectionFormat: RootJsonFormat[CardinalDirection] = cachedImplicit
  implicit val plateauJsonFormat: RootJsonFormat[Plateau] = jsonFormat2(Plateau)
  implicit val roverJsonFormat: RootJsonFormat[Rover] = jsonFormat4(Rover)
  implicit val roversJsonFormat: RootJsonFormat[Rovers] = jsonFormat1(Rovers)
}
