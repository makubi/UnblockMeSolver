package solver

import solver.Orientation._

case class UnblockMePiece(isGoalPiece: Boolean, length: Int, orientation: Orientation) {

  /**
   * Calculates the locations of all tiles of this piece
   */
  def calcLocationOfTiles(topLeftLocation: Location): Vector[Location] = {
    val loc: Location = topLeftLocation

    val offsetFn: (Int) => Location = offset => {
      if (orientation == Orientation.Vertical) Location(loc.x, loc.y - offset)
      else Location(loc.x + offset, loc.y)
    }

    0.until(length).map(offsetFn).toVector
  }
}
