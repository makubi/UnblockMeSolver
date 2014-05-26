package akkaSolver.actors

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive


class OpenList extends Actor with ActorLogging {
  override def receive: Actor.Receive = LoggingReceive {
    case _ =>
  }
}

object OpenList {

  def props() = Props(new OpenList)

}

