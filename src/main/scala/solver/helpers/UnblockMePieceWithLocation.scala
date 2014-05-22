package solver.helpers

import solver._
import solver.Location
import solver.UnblockMePiece
import solver.Orientation.Orientation

object UnblockMePieceWithLocation {

  /**
   * Converts an inputstring seamlessly into a (@UnblockMePiece, @Location)-Tuple
   */
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