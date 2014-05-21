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

  val (pieces, locations) = initialState.unzip

  val moves: Vector[Move] = getMoves

  def getMoves = {

    val allMoves =
      (for ((piece, index) <- pieces.zipWithIndex;
            location = locations(index);
            offset <- 1 to (6 - location.y))
      yield Up(offset, index)) ++
        (for ((piece, index) <- pieces.zipWithIndex;
              location = locations(index);
              offset <- 1 to (6 - location.y - piece.length))
        yield Down(offset, index)) ++
        (for ((piece, index) <- pieces.zipWithIndex;
              location = locations(index);
              offset <- 1 to (location.x - 1))
        yield Left(offset, index)) ++
        (for ((piece, index) <- pieces.zipWithIndex;
              location = locations(index);
              offset <- 1 to (6 - piece.length - location.x + 1))
        yield Right(offset, index))

    allMoves.filter(isValid)
  }

  def isValid(move: Move): Boolean = {
    val piece = pieces(move.pieceIndex)

    if (piece.orientation != move.orientation)
      false

    //Check, if updated location of the piece is already blocked

    true
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

    pieceDetails match {
      case x :: y :: length :: orientation :: Nil => {
        (
          UnblockMePiece(isGoalPiece, length.toInt, if (orientation == "V") Orientation.Vertical else if (orientation == "H") Orientation.Horizontal else throw new IllegalArgumentException("Orientation must be H/V")),
          Location(x.toInt, y.toInt)
          )
      }
      case _ => throw new Exception("KAPOTT")
    }
  }
}

}