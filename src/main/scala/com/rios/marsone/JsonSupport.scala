package com.rios.marsone

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import com.rios.marsone.model.{ Plateau, Rover, Rovers }

import spray.json.{ DefaultJsonProtocol, RootJsonFormat }

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val plateauJsonFormat: RootJsonFormat[Plateau] = jsonFormat2(Plateau)
  implicit val roverJsonFormat: RootJsonFormat[Rover] = jsonFormat3(Rover)
  implicit val roversJsonFormat: RootJsonFormat[Rovers] = jsonFormat1(Rovers)
}
