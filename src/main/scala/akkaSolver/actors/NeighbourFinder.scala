package akkaSolver.actors

import akka.actor._
import akka.event.LoggingReceive
import akkaSolver.actors.Solver._
import scala.collection.immutable.IndexedSeq
import akkaSolver.actors.Solver.State
import akkaSolver.actors.NeighbourFinder.FindNeighbours
import akkaSolver.helpers.UnblockMePiece
import akkaSolver.actors.NeighbourFinder.GetNewStatesOfPieceResponse
import akkaSolver.actors.Solver.InitialState
import akkaSolver.actors.NeighbourFinder.InitializeWithState
import akkaSolver.actors.MoveAnalyzer.GetNewStatesOfPieceRequest


class NeighbourFinder extends Actor with ActorLogging {

  override def receive: Actor.Receive = uninitialized

  val moveAnalyzerActor: ActorRef = context.actorOf(MoveAnalyzer.props(), "MoveAnalyzer")

  val uninitialized: Actor.Receive = LoggingReceive {
    case FindNeighbours(parentState: State) =>
      log.error("Cannot find neighbours in unitialized state, dude")

    case InitializeWithState(stateString, pieces) =>

      context.become(initialized(pieces))

      sender() ! InitialState(calcStateByStateString(stateString, pieces, currentMovementCosts = 0))
  }

  def initialized(pieces: Vector[UnblockMePiece]) = LoggingReceive {

    case msg@FindNeighbours(parentState: State) => {
      val stateArray: Array[Int] = parentState.state.toCharArray.map(c => c.asDigit)

      val answerReceiver = sender()

      //Create helper actor to gather the movements of _all_ tiles
      val helperActor: ActorRef = context.actorOf(Props(new Actor {
        private var receivedMovesOfTiles = Map.empty[Int, Seq[Int]]
        val expectedNumberOfTiles = parentState.state.length

        override def receive: Actor.Receive = LoggingReceive {
          case FindNeighbours(parentState: State) =>
            val stateArray: Vector[Int] = parentState.state.toCharArray.map(_.asDigit).toVector
            for (piece <- pieces) {
              moveAnalyzerActor ! GetNewStatesOfPieceRequest(piece.pieceIndex, stateArray, pieces)
            }

          case GetNewStatesOfPieceResponse(pieceIndex, _, newStatesOfPiece) => {
            receivedMovesOfTiles = receivedMovesOfTiles.updated(pieceIndex, newStatesOfPiece)
            checkIfDone()
          }
        }

        def checkIfDone() {

          receivedMovesOfTiles match {
            case xs if xs.size == expectedNumberOfTiles =>

              val state = stateArray.toVector
              val newStates: Iterable[State] = for (pieceIndex <- xs.keys;
                                                    newStateOfThatPiece <- xs(pieceIndex);
                                                    newState = state.updated(pieceIndex, newStateOfThatPiece))
              yield calcStateByStateString(newState.mkString, pieces, parentState.g + 1)

              answerReceiver ! NeighboursFound(newStates.toList, parentState)
              context.stop(self)
            case _ =>
          }
        }
      }), s"MoveGathererHelper-${parentState.state}")

      helperActor ! msg
    }
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

  case class GetNewStatesOfPieceResponse(pieceIndex: Int, stateArray: Vector[Int], newStatesOfPiece: IndexedSeq[Int])

}
