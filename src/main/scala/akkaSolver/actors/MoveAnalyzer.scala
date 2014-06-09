package akkaSolver.actors

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.actors.NeighbourFinder.GetNewStatesOfPieceResponse
import akkaSolver.helpers.{Orientation, UnblockMePiece}
import scala.collection.immutable.IndexedSeq
import scala.collection.immutable.Range.Inclusive


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

      val downRightStates: IndexedSeq[Int] = calcDownRightStates(state, piece)
      val upLeftStates: IndexedSeq[Int] = calcUpLeftStates(state, piece)

      val validateStates: IndexedSeq[Int] => IndexedSeq[Int] = allStates => allStates.takeWhile(newState => {
        canPieceTakeThisState(otherPiecesMatrix, piece, newState)
      })

      val response: GetNewStatesOfPieceResponse = GetNewStatesOfPieceResponse(pieceIndex, stateArray, validateStates(downRightStates) ++ validateStates(upLeftStates))
      sender() ! response
    }
  }
}

object MoveAnalyzer {

  def props() = Props(new MoveAnalyzer)

  case class GetNewStatesOfPieceRequest(pieceIndex: Int, stateArray: Vector[Int], pieces: Vector[UnblockMePiece])

  def calcDownRightStates(stateOfPiece: Int, piece: UnblockMePiece): IndexedSeq[Int] = {

    val right: Inclusive = Range.inclusive(stateOfPiece + 1, 6 - piece.length + 1)
    val down: Inclusive = Range.inclusive(stateOfPiece - 1, piece.length, -1)

    val range =if (piece.orientation == Orientation.Horizontal) right else down
    range.toVector
  }

  def calcUpLeftStates(stateOfPiece: Int, piece: UnblockMePiece): IndexedSeq[Int] = {
    val left: Inclusive = Range.inclusive(stateOfPiece - 1, 1, -1)
    val up: Inclusive = Range.inclusive(stateOfPiece + 1, 6)

    val range: Inclusive = if (piece.orientation == Orientation.Horizontal) left else up
    range.toVector
  }

  def removeAtIndex[A](index: Int, list: Vector[A]): Vector[A] = {
    val take = list.take(index)
    val right = list.takeRight(list.size - index - 1)

    take ++ right
  }

  private def fillPieceInMatrix(matrix: Array[Array[Char]], piece: UnblockMePiece, state: Int) {
    0.until(piece.length).foreach { offset => {
      val x = if (piece.orientation == Orientation.Horizontal) state + offset else piece.positionOnTheFixedAxis
      val y = if (piece.orientation == Orientation.Vertical) state - offset else piece.positionOnTheFixedAxis

      val yLocInMatrix = 6 - y
      matrix(yLocInMatrix)(x - 1) = if(piece.isGoalPiece) 'G' else piece.pieceIndex.toString.head
    }}
  }

  private def canPieceTakeThisState(matrixOfOtherPieces: Array[Array[Char]], piece: UnblockMePiece, state: Int): Boolean = {
    val isBlocked: Boolean = 0.until(piece.length).exists(offset => {
      val x = if (piece.orientation == Orientation.Horizontal) state + offset else piece.positionOnTheFixedAxis
      val y = if (piece.orientation == Orientation.Vertical) state - offset else piece.positionOnTheFixedAxis

      val yLocInMatrix = 6 - y
      matrixOfOtherPieces(yLocInMatrix)(x-1) != ' '
    })

    !isBlocked
  }

  def createArrayOfPiecesWithState(pieces: Vector[UnblockMePiece], state: Vector[Int]): Array[Array[Char]] = {

    val matrix = Array.fill(6, 6)(' ')

    0.until(state.size).foreach(i => {
      val piece = pieces(i)
      val stateOfPiece = state(i)

      fillPieceInMatrix(matrix, piece, stateOfPiece)

    })


    matrix
  }
}