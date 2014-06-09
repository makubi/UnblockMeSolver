package akkaSolver.actors

import akka.actor._
import akka.event.LoggingReceive
import akkaSolver.actors.Solver._
import akkaSolver.actors.OpenList.{AddState, GetStateWithLowestFCost}
import akkaSolver.actors.NeighbourFinder.{InitializeWithState, FindNeighbours}
import akkaSolver.actors.Solver.StateWithLowestFCost
import akkaSolver.actors.Solver.Start
import akkaSolver.actors.InitialStateParser.{ParseInitialStateResponse, ParseInitialStateRequest}
import akkaSolver.actors.ClosedList.AddToOpenListIfNotOnClosedList

class Solver(makeOpenList: ActorRefFactory => ActorRef, makeClosedList: ActorRefFactory => ActorRef, makeNeighbourFinder: ActorRefFactory => ActorRef, makeInitialStateParser: ActorRefFactory => ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = idle

  private var solutionRequester: ActorRef = _

  def idle: Receive = LoggingReceive {

    case Start(input) => {
      val openList: ActorRef = makeOpenList(context)
      val closedList: ActorRef = makeClosedList(context)
      val neighbourFinder = makeNeighbourFinder(context)
      val initialStateParser = makeInitialStateParser(context)
      solutionRequester = sender()
      context.become(solving(openList, closedList, neighbourFinder, initialStateParser))
      initialStateParser ! ParseInitialStateRequest(input)
    }
  }

  def solving(openList: ActorRef, closedList: ActorRef, neighbourFinder: ActorRef, initialStateParser: ActorRef): Receive = LoggingReceive {

    case Start(initialState) => log.info(s"Dude, I'm busy. Cannot process $initialState right now.") //do nothing - i'm busy
    case parseInitialStateResponse @ ParseInitialStateResponse(stateString, pieces) => {

      neighbourFinder ! InitializeWithState(stateString, pieces)
      
      //Move found state to openlist
      //openList ! AddState(stateString)
    }

    case InitialState(state) =>
      openList ! AddState(state)
      openList ! GetStateWithLowestFCost

    //Openlist schickt immer dann eine Nachricht, wenn
    case StateWithLowestFCost(state) => {
      closedList ! AddState(state)
      neighbourFinder ! FindNeighbours(state)
    }

    case NeighboursFound(neighbourStates, parent) =>

      val solutionOption = neighbourStates.find(state => isSolved(state))

      if(solutionOption.isDefined) {
        solutionRequester ! SolutionFound(solutionOption.get)
        context.children.foreach(c => c ! PoisonPill)
        context.unbecome()
      }

      closedList ! AddToOpenListIfNotOnClosedList(neighbourStates, openList)

      openList ! GetStateWithLowestFCost
  }

  def isSolved(state: State): Boolean = false
}

object Solver {

  def props(openListProps: Props, closedListProps: Props, neighbourFinderProps: Props, initialStateParserProps: Props): Props = {
    Props(new Solver(_.actorOf(openListProps, "OpenList"), _.actorOf(closedListProps, "ClosedList"), _.actorOf(neighbourFinderProps, "NeighbourFinder"), _.actorOf(initialStateParserProps, "InitialStateParser")))
  }

  case class State(state: String, g: Int, h: Int) {
    def f = g + h
  }

  object StateOrderingForPriorityQueue extends Ordering[State] {
    def compare(a:State, b:State) = -(a.f compare b.f)
  }

  case class InitialState(state: State)
  case class Start(input: String)
  case class StateWithLowestFCost(state: State)
  case class NeighboursFound(neighbourStates: List[State], parentState: State)
  case class SolutionFound(state: State)

}