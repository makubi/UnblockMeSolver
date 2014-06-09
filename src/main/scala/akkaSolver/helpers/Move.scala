package akkaSolver.helpers

object Helper {
  type State = Vector[Int]
}

import akkaSolver.helpers.Orientation.Orientation


trait Move {
  def distance: Int
  def pieceIndex: Int
  def orientation: Orientation

  override def toString = s"$pieceIndex:${this.getClass.getSimpleName.head}$distance"
}

case class Up(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Vertical
}

case class Down(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Vertical
}

case class Left(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Horizontal
}

case class Right(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Horizontal
}