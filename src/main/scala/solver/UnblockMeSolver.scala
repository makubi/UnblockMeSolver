package solver {

import Orientation._


case class UnblockMePiece(isGoalPiece: Boolean, length: Int, orientation: Orientation) {

  def calcLocations(topLeftLocation: Location): Vector[Location] = {
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

  def getMoves = {

    val allMoves =
      (for ((piece, index) <- pieces.zipWithIndex;
            location = locations(index);
            offset <- 1 to 6
        if(location.y + offset) <= 6
      )
      yield Up(offset, index)) ++
        (for ((piece, index) <- pieces.zipWithIndex;
              location = locations(index);
              offset <- 1 to 6
          if (location.y - piece.length) - offset + 1 > 0
          )
        yield Down(offset, index)) ++
        (for ((piece, index) <- pieces.zipWithIndex;
              location = locations(index);
              offset <- 1 to 6
          if location.x - offset > 0
        )
        yield Left(offset, index)) ++
        (for ((piece, index) <- pieces.zipWithIndex;
              location = locations(index);
              offset <- 1 to 6
         if location.x + piece.length + offset -1 <= 6
        )
        yield Right(offset, index))


    allMoves.filter(m => isValid(m, locations, pieces))
  }
}

object UnblockMeSolver {
  def isValid(move: Move, locationsToCheck: Vector[Location], piecesToCheck: Vector[UnblockMePiece]): Boolean = {
    val piece = piecesToCheck(move.pieceIndex)
    val newState: Vector[(UnblockMePiece, Location)] = piecesToCheck.zip(move.change(locationsToCheck))

    if (piece.orientation != move.orientation) false
    else if(areTwoTilesAtTheSameLocation(newState)) false
    else true

    //Check, if updated location of the piece is already blocked

  }

  def areTwoTilesAtTheSameLocation(newState: Vector[(UnblockMePiece, Location)]): Boolean = {
    val locationsOfAllTiles: Vector[Location] = for (((piece, topLeft), index) <- newState.zipWithIndex;
                                                     location <- piece.calcLocations(topLeft))
    yield location

    val grouped: Map[Location, Vector[Location]] = locationsOfAllTiles.groupBy(l => l)
    val numberOfGroupsWithDuplicateEntries: Int = grouped.count(_._2.size > 1)
    numberOfGroupsWithDuplicateEntries > 0
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