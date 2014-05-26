package akkaSolver.actors

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive

class ClosedList extends Actor with ActorLogging {
  override def receive: Actor.Receive = LoggingReceive {
    case _ =>
  }
}

object ClosedList {

  def props() = Props(new ClosedList)

}

