package solver {

import Orientation._
import scala.annotation.tailrec


class UnblockMeSolver(initialState: Vector[(UnblockMePiece, Location)]) {

  import UnblockMeSolver._

  val (pieces, locations) = initialState.unzip
  val moves: Vector[Move] = getMoves


  /**
   * get all possible moves from this state
   * @return
   */
  def getMoves: Vector[Move] = {

    val moveFunctions: List[(Int, Int) => Move] = List(Up.apply, Right.apply, Down.apply, Left.apply)

    /**
     * helper functions, that explores the movement-possibilities of one piece into the given direction
     */
    def helper(pieceIndex: Int, moveFn: (Int, Int) => Move): List[Move] = {

      @tailrec
      def helper2(moves: List[Move], offset: Int): List[Move] = {
        val move = moveFn(offset, pieceIndex)

        if(isValid(move, locations, pieces)) helper2(move :: moves, offset + 1)
        else moves.reverse
      }

      helper2(Nil, 1)
    }

    val moves: List[Move] = (for (
      moveFn <- moveFunctions;
      pieceIndex <- 0.until(pieces.size);
      move <- helper(pieceIndex, moveFn)
    )
    yield move).toList

    moves.toVector
  }
}

object UnblockMeSolver {

  /**
   * Heler function that checks, if a move would lead to a valid solution
   */
  def isValid(move: Move, locationsToCheck: Vector[Location], piecesToCheck: Vector[UnblockMePiece]): Boolean = {
    val piece = piecesToCheck(move.pieceIndex)
    val newState: Vector[(UnblockMePiece, Location)] = piecesToCheck.zip(move.change(locationsToCheck))
    val newLocation: Location = newState(move.pieceIndex)._2

    if (piece.orientation != move.orientation) false
    else if (isPieceOutOfBounds(piece, newLocation)) false
    else if (arePiecesOverlapping(newState)) false
    else true
  }

  /**
   * checks, if a piece is out of bounds
   */
  def isPieceOutOfBounds(piece: UnblockMePiece, location: Location): Boolean = {
    val tiles: Vector[Location] = piece.calcLocationOfTiles(location)

    tiles.exists(l => l.x < 1 || l.x > 6 || l.y < 1 || l.y > 6)
  }

  /**
   * Checks, if there are overlapping tiles
   */
  def arePiecesOverlapping(newState: Vector[(UnblockMePiece, Location)]): Boolean = {
    val locationsOfAllTiles: Vector[Location] = for (((piece, topLeft), index) <- newState.zipWithIndex;
                                                     location <- piece.calcLocationOfTiles(topLeft))
    yield location
    
    locationsOfAllTiles.size != locationsOfAllTiles.toSet.size
  }

  /**
   *
   */
  def getCollectionExceptElementAt[T](index: Int, list: List[T]): List[T] = {
    list.take(index) ::: list.takeRight(list.size-index-1)
  }
}

}