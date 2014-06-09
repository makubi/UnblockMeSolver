package akkaSolver.helpers

import akkaSolver.helpers.Orientation.Orientation


case class UnblockMePiece(isGoalPiece: Boolean, length: Int, orientation: Orientation, positionOnTheFixedAxis: Int, pieceIndex: Int = 0) {

  val offsetFn: (Int) => Location = offset => {
    if (orientation == Orientation.Vertical) Location(0, length - offset - 1)
    else Location(offset, 0)
  }

  val tiles = 0.until(length).map(offsetFn).toSet

  /**
   * Calculates the locations of all tiles of this piece
   */
  def calcLocationOfTiles(topLeftLocation: Location): Set[Location] = {
    val loc: Location = topLeftLocation

    tiles.map(t => Location(t.x + loc.x, loc.y - t.y))

  }
}
