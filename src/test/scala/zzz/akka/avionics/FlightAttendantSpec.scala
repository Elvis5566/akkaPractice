package zzz.akka.avionics

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{MustMatchers, WordSpec, WordSpecLike}
import com.typesafe.config.ConfigFactory

object TestFlightAttendant {
  def apply() = new FlightAttendant
    with AttendantResponsiveness {
    val maxResponseTimeMS = 1
  }
}


class FlightAttendantSpec extends TestKit(ActorSystem("FlightAttendantSpec", ConfigFactory.parseString("akka.scheduler.tick-duration = 1ms")))
  with ImplicitSender with WordSpecLike with MustMatchers {

  import FlightAttendant._

  "FlightAttendant" should {
    "get a drink when asked" in {
      val a = TestActorRef(Props(TestFlightAttendant()))
      a ! GetDrink("Soda")
      expectMsg(Drink("Soda"))
    }
  }
}