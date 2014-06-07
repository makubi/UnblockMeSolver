package akkaSolver.actors

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.actors.Solver.{InitialState, State}
import akkaSolver.actors.NeighbourFinder.{InitializeWithState, FindNeighbours}
import akkaSolver.actors.InitialStateParser.ParseInitialStateResponse
import akkaSolver.helpers.UnblockMePiece


class NeighbourFinder extends Actor with ActorLogging {

  override def receive: Actor.Receive = uninitialized

  val uninitialized: Actor.Receive = LoggingReceive {
    case FindNeighbours(parentState: State) =>
      log.error("Cannot find neighbours in unitialized state, dude")

    case InitializeWithState(stateString, pieces) =>
      context.become(initialized(stateString, pieces))

      sender() ! InitialState(calcStateByStateString(stateString, pieces, currentMovementCosts = 0))

  }

  def initialized(stateString: String, pieces: Vector[UnblockMePiece]) = LoggingReceive {

    case FindNeighbours(parentState: State) => log.info("Try to find neighbours")
  }


  def calcStateByStateString(stateString: String, pieces: Vector[UnblockMePiece], currentMovementCosts: Int): State = {
    //FIXME: calculate the heuristic
    State(state = stateString, g = currentMovementCosts, h = 10)
  }

}

object NeighbourFinder {

  def props() = Props(new NeighbourFinder)

  case class InitializeWithState(stateString: String, pieces: Vector[UnblockMePiece])
  case class FindNeighbours(parentState: State)
}
