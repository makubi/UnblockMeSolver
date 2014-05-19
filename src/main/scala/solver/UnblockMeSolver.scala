
package solver {

import Orientation._


case class UnblockMePiece(isGoalPiece: Boolean, length: Int, orientation: Orientation)

class UnblockMeSolver(initialState: Vector[(UnblockMePiece, Location)]) {

  val (pieces, locations) = initialState.unzip

  val moves: Vector[Move] =
    (for ((piece, index) <- pieces.zipWithIndex;
         location = locations(index);
         offset <- 1 to (6 - location.y)
         if piece.orientation == Orientation.Vertical)
    yield Up(offset, index) ) ++
    (for ((piece, index) <- pieces.zipWithIndex;
          location = locations(index);
          offset <- 1 to (6 - location.y - piece.length )
          if piece.orientation == Orientation.Vertical)
    yield Down(offset, index) ) ++
    (for ((piece, index) <- pieces.zipWithIndex;
          location = locations(index);
          offset <- 1 to (location.x - 1)
          if piece.orientation == Orientation.Horizontal)
    yield Left(offset, index) ) ++
    (for ((piece, index) <- pieces.zipWithIndex;
          location = locations(index);
          offset <- 1 to (6 - piece.length - location.x + 1)
          if piece.orientation == Orientation.Horizontal)
    yield Right(offset, index) )
}

}