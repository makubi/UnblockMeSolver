package solver {

import scala.annotation.tailrec


class UnblockMeSolver(initialState: Vector[(UnblockMePiece, Location)]) {

  import UnblockMeSolver._

  val (pieces, locations) = initialState.unzip

  def moves = getAllPossibleMoves(locations)

  val availableMoveDirections: List[(Int, Int) => Move] = List(Up.apply, Right.apply, Down.apply, Left.apply)

  /**
   * get all possible moves from this state
   * @return
   */
  def getAllPossibleMoves(state: State): Vector[Move] = {


    /**
     * helper functions, that explores the movement-possibilities of one piece into the given direction
     */
    def getValidMovesForPiece(pieceIndex: Int, moveFn: (Int, Int) => Move): List[Move] = {

      @tailrec
      def getValidMovesForPieceHelper(moves: List[Move], offset: Int): List[Move] = {
        val move = moveFn(offset, pieceIndex)
        if(move.orientation != pieces(pieceIndex).orientation) Nil
        else if(isValid(move, state, pieces)) getValidMovesForPieceHelper(move :: moves, offset + 1)
        else moves.reverse
      }

      getValidMovesForPieceHelper(Nil, 1)
    }

    val moves: List[Move] = (for (
      moveFn <- availableMoveDirections;
      pieceIndex <- 0.until(pieces.size);
      move <- getValidMovesForPiece(pieceIndex, moveFn)
    )
    yield move).toList

    moves.toVector
  }

  class Path(val history: List[Move], val endState: State) {

    def extend(move: Move) = new Path(move :: history, move.change(endState))
    override def toString = s"${history.reverse mkString " "} --> $endState"
  }

  val initialPath = new Path(Nil, locations)
  def from(paths: Set[Path], explored: Set[State]): Stream[Set[Path]] =
    if (paths.isEmpty) Stream.empty
    else {
      val more = for {
        path <- paths
        next <- getAllPossibleMoves(path.endState) map path.extend
        if !(explored contains next.endState)
      } yield next

      paths #:: from(more, explored ++ (more map (_.endState)))
    }

  val pathSets = from(Set(initialPath), Set(locations))

  def solution(targetLocation: Location): Stream[Path] = {

    val indexOfTargetPiece = pieces.indexWhere(_.isGoalPiece)

    for {
      pathSet <- pathSets
      path <- pathSet
      if path.endState(indexOfTargetPiece) == targetLocation
    } yield path
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
    val tiles: Set[Location] = piece.calcLocationOfTiles(location)

    tiles.exists(l => l.x < 1 || l.x > 6 || l.y < 1 || l.y > 6)
  }

  /**
   * Checks, if there are overlapping tiles
   */
  def arePiecesOverlapping(newState: Vector[(UnblockMePiece, Location)]): Boolean = {
    val locationsOfAllTiles: Vector[Location] = for ((piece, topLeft) <- newState;
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