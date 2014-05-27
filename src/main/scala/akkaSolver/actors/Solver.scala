package akkaSolver.actors

import akka.actor.{ActorRefFactory, ActorRef, Actor, Props}
import akka.event.LoggingReceive
import akkaSolver.actors.InitialStateParser.GetInitialState
import akkaSolver.actors.Solver.{InitialStateFound, Start}

class Solver(makeExplorer: ActorRefFactory => ActorRef, makeOpenList: ActorRefFactory => ActorRef, makeClosedList: ActorRefFactory => ActorRef, makeNeighbourFinder: ActorRefFactory => ActorRef, makeInitialStateParser: ActorRefFactory => ActorRef) extends Actor {
  override def receive: Receive = idle

  def idle: Receive = LoggingReceive {

    case Start(initialState) => {
      val explorer: ActorRef = makeExplorer(context)
      val openList: ActorRef = makeOpenList(context)
      val closedList: ActorRef = makeClosedList(context)
      val neighbourFinder = makeNeighbourFinder(context)
      val initialStateParser = makeInitialStateParser(context)
      context.become(solving(explorer, openList, closedList, neighbourFinder, initialStateParser))
      initialStateParser ! GetInitialState(initialState)
    }
  }

  def solving(explorer: ActorRef, openList: ActorRef, closedList: ActorRef, neighbourFinder: ActorRef, initialStateParser: ActorRef): Receive = LoggingReceive {

    case Start => //do nothing - i'm busy
    case InitialStateFound(state) => {


    }

  }
}

object Solver {

  def props(explorerProps: Props, openListProps: Props, closedListProps: Props, neighbourFinderProps: Props, initialStateParserProps: Props): Props = {
    Props(new Solver(_.actorOf(explorerProps), _.actorOf(openListProps), _.actorOf(closedListProps), _.actorOf(neighbourFinderProps), _.actorOf(initialStateParserProps)))
  }

  implicit object StateOrdering extends Ordering[State] {
    def compare(a:State, b:State) = a.f compare b.f
  }

  case class State(state: String, g: Int, h: Int) {
    def f = g + h
  }

  case class Start(initialState: String)
  case class InitialStateFound(state: State)

}