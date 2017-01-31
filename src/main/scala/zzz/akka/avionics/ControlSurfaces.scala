package zzz.akka.avionics

import akka.actor.{Actor, ActorRef}

// The ControlSurfaces object carries messages for
// controlling the plane
object ControlSurfaces {

  // amount is a value between -1 and 1.  The altimeter
  // ensures that any value outside that range is truncated
  // to be within it.
  case class StickBack(amount: Float)

  case class StickForward(amount: Float)

  case class StickLeft(amount: Float)
  case class StickRight(amount: Float)
  case class HasControl(somePilot: ActorRef)
}

// Pass in the Altimeter as an ActorRef so that we can send
// messages to it
class ControlSurfaces(plane: ActorRef, altimeter: ActorRef,
                      heading: ActorRef) extends Actor {

  import ControlSurfaces._
  import Altimeter._
  import HeadingIndicator._

  def receive = controlledBy(context.system.deadLetters)

  def controlledBy(somePilot: ActorRef): Receive = {
    case StickBack(amount) if sender == somePilot =>
      altimeter ! RateChange(amount)
    case StickForward(amount) if sender == somePilot =>
      altimeter ! RateChange(-1 * amount)
    case StickLeft(amount) if sender == somePilot =>
      heading ! BankChange(-1 * amount)
    case StickRight(amount) if sender == somePilot =>
      heading ! BankChange(amount)
    // Only the plane can tell us who's currently in control
    case HasControl(entity) if sender == plane =>
      // Become a new instance, where the entity, which the
      // plane told us about, is now the entity that
      // controls the plane
      context.become(controlledBy(entity))
  }

//  def receive = {
//    // Pilot pulled the stick back by a certain amount,
//    // and we inform the Altimeter that we're climbing
//    case StickBack(amount) =>
//      altimeter ! RateChange(amount)
//    // Pilot pushes the stick forward and we inform the
//    // Altimeter that we're descending
//    case StickForward(amount) =>
//      altimeter ! RateChange(-amount)
//  }
}
