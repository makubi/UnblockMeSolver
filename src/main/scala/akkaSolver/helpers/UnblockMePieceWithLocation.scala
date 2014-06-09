package akkaSolver.helpers

import akkaSolver.helpers.Orientation.Orientation


object UnblockMePieceWithLocation {

  /**
   * Converts an inputstring seamlessly into a (@UnblockMePiece, @Int)-Tuple. The number of the tuple indicates the position on the movable axis --> the state of the Piece
   */
  def apply(input: String, index: Int): (UnblockMePiece, Int) = parse(input, index)

  private def parse(input: String, index: Int): (UnblockMePiece, Int) = {
    //"G,1,5,2,H" --> GoalPiece, x=1, y=5, length=2; Orientation=Horizontal

    val values: List[String] = input.toUpperCase.split(",").toList
    val (isGoalPiece, pieceDetails) = values match {
      case "G" :: xs => (true, xs)
      case xs: List[String] => (false, xs)
    }

    val output = pieceDetails match {
      case x :: y :: length :: orientation :: Nil => {
        val o: Orientation = if (orientation == "V") Orientation.Vertical else if (orientation == "H") Orientation.Horizontal else throw new IllegalArgumentException("Orientation must be H/V")
        val positionOnTheFixedAxis = if(o == Orientation.Horizontal) y.toInt else x.toInt
        val positionOnTheMovableAxis = if(o == Orientation.Vertical) y.toInt else x.toInt

        (
          UnblockMePiece(isGoalPiece, length.toInt, o, positionOnTheFixedAxis, index),
          positionOnTheMovableAxis
          )
      }
      case x => throw new Exception(s"KAPOTT - failed to match $x")
    }

    output
  }
}
