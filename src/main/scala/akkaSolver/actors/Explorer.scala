package akkaSolver.actors

import akka.actor._
import akka.event.LoggingReceive

class Explorer(makeOpenList: ActorRefFactory => ActorRef,
               makeClosedList: ActorRefFactory => ActorRef,
               makeNeighbourFinder: ActorRefFactory => ActorRef,
               makeInitialStateParser: ActorRefFactory => ActorRef) extends Actor with ActorLogging {

    def receive: Receive = LoggingReceive {

      case _ => ???

  }
}

object Explorer {


  def props(openListProps: Props, closedListProps: Props, neighbourFinderProps: Props, initialStateParserProps: Props) = {
    Props(new Explorer(_.actorOf(openListProps), _.actorOf(closedListProps), _.actorOf(neighbourFinderProps), _.actorOf(initialStateParserProps)))
  }
}
