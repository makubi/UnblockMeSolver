package akkaSolver.actors

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive






class NeighbourFinder extends Actor with ActorLogging {
  override def receive: Actor.Receive = LoggingReceive {


    case _ =>
  }
}

object NeighbourFinder {

  def props() = Props(new NeighbourFinder)



}
