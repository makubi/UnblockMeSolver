package akkaSolver.actors

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.actors.NeighbourFinder.GetNewStatesOfPieceResponse
import akkaSolver.helpers.{Orientation, UnblockMePiece}
import scala.collection.immutable.IndexedSeq


class MoveAnalyzer extends Actor with ActorLogging {

  import MoveAnalyzer._

  def receive = LoggingReceive {

    case GetNewStatesOfPieceRequest(pieceIndex, stateArray, pieces) => {
      val piece = pieces(pieceIndex)
      val state = stateArray(pieceIndex)
      val otherStates: Vector[Int] = MoveAnalyzer.removeAtIndex(pieceIndex, stateArray)
      val otherPieces: Vector[UnblockMePiece] = MoveAnalyzer.removeAtIndex(pieceIndex, pieces)

      val otherPiecesMatrix: Array[Array[Char]] = createArrayOfPiecesWithState(otherPieces, otherStates)

      //Go through all moves
      //is tile already blocked?
      //yes - allright
      //no  - continue

      val positiveStates = for {
        newState <- Range.inclusive(state + 1, 6 - piece.length +1)
      } yield newState

      val validPositiveStates: IndexedSeq[Int] = positiveStates.takeWhile(newState => {
        canPieceTakeThisState(otherPiecesMatrix, piece, newState)
      })

      val negativeStates = for {
        newState <- Range.inclusive(1, state - 1)
      } yield newState

      val validNegativeStates: IndexedSeq[Int] = negativeStates.takeWhile(newState => {
        canPieceTakeThisState(otherPiecesMatrix, piece, newState)
      })

      sender() ! GetNewStatesOfPieceResponse(pieceIndex, stateArray, validPositiveStates ++ validNegativeStates)




    }
  }

}

object MoveAnalyzer {

  def props() = Props(new MoveAnalyzer)

  case class GetNewStatesOfPieceRequest(pieceIndex: Int, stateArray: Vector[Int], pieces: Vector[UnblockMePiece])

  def removeAtIndex[A](index: Int, list: Vector[A]): Vector[A] = {
    val take = list.take(index)
    val right = list.takeRight(list.size - index - 1)

    take ++ right
  }

  private def fillPieceInMatrix(matrix: Array[Array[Char]], piece: UnblockMePiece, state: Int, pieceIndex: Int) {
    0.until(piece.length).foreach { offset => {
      val x = if (piece.orientation == Orientation.Horizontal) state - 1 + offset else piece.positionOnTheFixedAxis
      val y = if (piece.orientation == Orientation.Vertical) state - 1 + offset else piece.positionOnTheFixedAxis

      matrix(6 - y)(x) = pieceIndex.toString.head
    }}
  }

  private def canPieceTakeThisState(matrixOfOtherPieces: Array[Array[Char]], piece: UnblockMePiece, state: Int): Boolean = {
    val isBlocked: Boolean = 0.until(piece.length).exists(offset => {
      val x = if (piece.orientation == Orientation.Horizontal) state - 1 + offset else piece.positionOnTheFixedAxis
      val y = if (piece.orientation == Orientation.Vertical) state - 1 + offset else piece.positionOnTheFixedAxis

      matrixOfOtherPieces(6 - y - 1)(x) != ' '
    })

    !isBlocked
  }

  def createArrayOfPiecesWithState(pieces: Vector[UnblockMePiece], state: Vector[Int]): Array[Array[Char]] = {

    val matrix = Array.fill(6, 6)(' ')

    0.until(state.size).foreach(i => {
      val piece = pieces(i)
      val stateOfPiece = state(i)

      fillPieceInMatrix(matrix, piece, stateOfPiece, pieceIndex = i)

    })


    matrix
  }
}