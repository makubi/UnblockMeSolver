package solver {

import Orientation._
import scala.annotation.tailrec


case class UnblockMePiece(isGoalPiece: Boolean, length: Int, orientation: Orientation) {

  def calcLocationOfTiles(topLeftLocation: Location): Vector[Location] = {
    val loc: Location = topLeftLocation

    val offsetFn: (Int) => Location = offset => {
      if (orientation == Orientation.Vertical) Location(loc.x, loc.y - offset)
      else Location(loc.x + offset, loc.y)
    }

    0.until(length).map(offsetFn).toVector
  }
}

class UnblockMeSolver(initialState: Vector[(UnblockMePiece, Location)]) {

  import UnblockMeSolver._

  val (pieces, locations) = initialState.unzip

  val moves: Vector[Move] = getMoves


  def getMoves: Vector[Move] = {

    val moveFunctions: List[(Int, Int) => Move] = List(Up.apply, Right.apply, Down.apply, Left.apply)

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
  def isValid(move: Move, locationsToCheck: Vector[Location], piecesToCheck: Vector[UnblockMePiece]): Boolean = {
    val piece = piecesToCheck(move.pieceIndex)
    val newState: Vector[(UnblockMePiece, Location)] = piecesToCheck.zip(move.change(locationsToCheck))
    val newLocation: Location = move.change(locationsToCheck(move.pieceIndex))

    if (piece.orientation != move.orientation) false
    else if (isPieceOutOfBounds(piece, newLocation)) false
    else if (areTwoTilesAtTheSameLocation(newState)) false
    else true
  }
  
  def isPieceOutOfBounds(piece: UnblockMePiece, location: Location): Boolean = {
    val tiles: Vector[Location] = piece.calcLocationOfTiles(location)

    tiles.exists(l => l.x < 1 || l.x > 6 || l.y < 1 || l.y > 6)
  }

  def areTwoTilesAtTheSameLocation(newState: Vector[(UnblockMePiece, Location)]): Boolean = {
    val locationsOfAllTiles: Vector[Location] = for (((piece, topLeft), index) <- newState.zipWithIndex;
                                                     location <- piece.calcLocationOfTiles(topLeft))
    yield location

    val grouped: Map[Location, Vector[Location]] = locationsOfAllTiles.groupBy(l => l)
    val numberOfGroupsWithDuplicateEntries: Int = grouped.count(_._2.size > 1)
    numberOfGroupsWithDuplicateEntries > 0
  }

  def getCollectionExceptElementAt[T](index: Int, list: List[T]): List[T] = {
    list.take(index) ::: list.takeRight(list.size-index-1)
  }
}

object UnblockMePieceWithLocation {

  def apply(input: String): (UnblockMePiece, Location) = parse(input)

  private def parse(input: String): (UnblockMePiece, Location) = {
    //"G,1,5,2H" --> GoalPiece, x=1, y=5, length=2; Orientation=Horizontal

    val values: List[String] = input.toUpperCase.split(",").toList
    val (isGoalPiece, pieceDetails) = values match {
      case "G" :: xs => (true, xs)
      case xs: List[String] => (false, xs)
    }

    val output = pieceDetails match {
      case x :: y :: length :: orientation :: Nil => {
        val o: Orientation = if (orientation == "V") Orientation.Vertical else if (orientation == "H") Orientation.Horizontal else throw new IllegalArgumentException("Orientation must be H/V")

        (
          UnblockMePiece(isGoalPiece, length.toInt, o),
          Location(x.toInt, y.toInt)
          )
      }
      case _ => throw new Exception("KAPOTT")
    }

    output
  }
}

}