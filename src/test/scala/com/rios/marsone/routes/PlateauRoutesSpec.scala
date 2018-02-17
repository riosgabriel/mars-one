package com.rios.marsone.routes

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.rios.marsone.actors.ControlCenterActor
import com.rios.marsone.model.Plateau
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.duration._

class PlateauRoutesSpec extends WordSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest
    with PlateauRoutes
    with BeforeAndAfterAll {

  override implicit val timeout: Timeout = 5 seconds

  override def controlCenterActor: ActorRef = system.actorOf(ControlCenterActor.props)

  lazy val routes = plateauRoutes

  "PlateauRoutes" should {
    "return no plateau if no present (GET /plateau)" in {
      val request = HttpRequest(uri = "/plateau")

      request ~> routes ~> check {
        status should ===(StatusCodes.NotFound)
      }
    }

    "be able to add plateau (POST /plateau)" in {
      val plateau = Plateau(10, 10)

      val plateauEntity = Marshal(plateau).to[MessageEntity].futureValue

      val request = Post("/plateau").withEntity(plateauEntity)

      request ~> routes ~> check {
        status shouldEqual StatusCodes.Created
      }
    }
  }

}
