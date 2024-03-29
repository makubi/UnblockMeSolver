package akkaSolver.actors

import akka.actor._
import akka.event.LoggingReceive
import akkaSolver.actors.Solver._
import akkaSolver.actors.OpenList.SolutionFoundProvidePathRequest
import akkaSolver.actors.NeighbourFinder.{InitializeWithState, FindNeighbours}
import akkaSolver.actors.Solver.StateWithLowestFCost
import akkaSolver.actors.Solver.Start
import akkaSolver.actors.InitialStateParser.{ParseInitialStateResponse, ParseInitialStateRequest}
import akkaSolver.actors.ClosedList.{AddStateToClosedList, AddToOpenListIfNotOnClosedList}
import akkaSolver.helpers.{Move, UnblockMePiece}

class Solver(makeOpenList: ActorRefFactory => ActorRef, makeClosedList: ActorRefFactory => ActorRef, makeNeighbourFinder: ActorRefFactory => ActorRef, makeInitialStateParser: ActorRefFactory => ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = idle

  private var solutionRequester: ActorRef = _
  private var pieces: Vector[UnblockMePiece] = _
  private var initialState: String = _
  private var goalPiece: UnblockMePiece = _

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

    case Start(state) => log.info(s"Dude, I'm busy. Cannot process $state right now.") //do nothing - i'm busy

    case parseInitialStateResponse@ParseInitialStateResponse(stateString, newPieces) => {

      this.pieces = newPieces
      this.initialState = stateString
      val goalPieceOpt: Option[UnblockMePiece] = pieces.find(_.isGoalPiece)
      if (goalPieceOpt.isEmpty) {
        solutionRequester ! NoGoalPieceGiven
        context.children.foreach(c => c ! PoisonPill)
        context.unbecome()
      } else {
        this.goalPiece = goalPieceOpt.get

        neighbourFinder ! InitializeWithState(stateString, pieces)
      }
    }


    case InitialState(state) =>
      //No need to add the first state to the openList and get it off again
      self ! StateWithLowestFCost(state)


    case StateWithLowestFCost(state) => {
      closedList ! AddStateToClosedList(state)
      neighbourFinder ! FindNeighbours(state)
    }


    case NeighboursFound(neighbourStates, parent) =>

      val solutionOption = neighbourStates.find(state => isSolved(state))

      closedList ! AddToOpenListIfNotOnClosedList(parent, neighbourStates, openList)

      if (solutionOption.isDefined) {
        context.children.toList.filterNot(child => child == openList).foreach(context.stop)
        openList ! SolutionFoundProvidePathRequest(initialState, solutionOption.get)
      }


    case SolutionFoundProvidePathResponse(initialStateString, path, moves) => {
      context.children.foreach(c => context.stop(c))
      context.unbecome()
      solutionRequester ! SolutionFound(initialStateString, path, moves)
    }
  }

  def isSolved(state: State): Boolean = state.state(goalPiece.pieceIndex) == '5'
}

object Solver {

  def props(openListProps: Props, closedListProps: Props, neighbourFinderProps: Props, initialStateParserProps: Props): Props = {
    val makeOpenList: (ActorRefFactory) => ActorRef = _.actorOf(openListProps, "OpenList")
    val makeClosedList: (ActorRefFactory) => ActorRef = _.actorOf(closedListProps, "ClosedList")
    val makeNeighbourFinder: (ActorRefFactory) => ActorRef = _.actorOf(neighbourFinderProps, "NeighbourFinder")
    val makeInitialStateParser: (ActorRefFactory) => ActorRef = _.actorOf(initialStateParserProps, "InitialStateParser")

    Props(new Solver(makeOpenList, makeClosedList, makeNeighbourFinder, makeInitialStateParser))
  }

  case class State(state: String, g: Int, h: Int) {
    def f = g + h
  }

  object StateOrderingForPriorityQueue extends Ordering[State] {
    def compare(a: State, b: State) = -(a.f compare b.f)
  }

  case class InitialState(state: State)

  case class Start(input: String)

  case class StateWithLowestFCost(state: State)

  case class NeighboursFound(neighbourStates: List[State], parentState: State)

  case class SolutionFound(initialState: String, path: List[String], moves: List[Move])

  case class SolutionFoundProvidePathResponse(initialState: String, path: List[String], moves: List[Move])

  case object NoGoalPieceGiven

}