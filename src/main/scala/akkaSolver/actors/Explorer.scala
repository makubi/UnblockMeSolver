package akkaSolver.actors

import akka.actor._
import akka.event.LoggingReceive
import akkaSolver.actors.Explorer.{InitialStateFound, Start}
import akkaSolver.actors.InitialStateParser.GetInitialState

class Explorer(makeOpenList: ActorRefFactory => ActorRef, makeClosedList: ActorRefFactory => ActorRef, makeNeighbourFinder: ActorRefFactory => ActorRef, makeInitialStateParser: ActorRefFactory => ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = idle

  def idle: Receive = LoggingReceive {

    case Start(initialState) => {
      val openList: ActorRef = makeOpenList(context)
      val closedList: ActorRef = makeClosedList(context)
      val neighbourFinder = makeNeighbourFinder(context)
      val initialStateParser = makeInitialStateParser(context)
      context.become(solving(openList, closedList, neighbourFinder, initialStateParser))
      initialStateParser ! GetInitialState(initialState)
    }
  }

  def solving(openList: ActorRef, closedList: ActorRef, neighbourFinder: ActorRef, initialStateParser: ActorRef): Receive = LoggingReceive {

    case Start => //do nothing - i'm busy
    case InitialStateFound(state) => {


    }


  }
}

object Explorer {

  implicit object StateOrdering extends Ordering[State] {
    def compare(a:State, b:State) = a.f compare b.f
  }

  case class State(state: String, g: Int, h: Int) {
    def f = g + h
  }

  case class Start(initialState: String)
  case class InitialStateFound(state: State)

  def props(openListProps: Props, closedListProps: Props, neighbourFinderProps: Props, initialStateParserProps: Props) = {
    Props(new Explorer(_.actorOf(openListProps), _.actorOf(closedListProps), _.actorOf(neighbourFinderProps), _.actorOf(initialStateParserProps)))
  }
}
