package solver {

import solver.Orientation.Orientation


trait Move {
  def distance: Int
  def pieceIndex: Int
  def orientation: Orientation

  def change(state: State): State
}

case class Up(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Vertical
  override def change(state: State): State = {
    val oldLocation = state(pieceIndex)
    state updated(pieceIndex, Location(oldLocation.x, oldLocation.y + distance))
  }
}

case class Down(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Vertical
  override def change(state: State): State = {
    val oldLocation = state(pieceIndex)
    state updated(pieceIndex, Location(oldLocation.x, oldLocation.y - distance))
  }
}

case class Left(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Horizontal
  override def change(state: State): State = {
    val oldLocation = state(pieceIndex)
    state updated(pieceIndex, Location(oldLocation.x - distance, oldLocation.y))
  }
}

case class Right(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Horizontal
  override def change(state: State): State = {
    val oldLocation = state(pieceIndex)
    state updated(pieceIndex, Location(oldLocation.x + distance, oldLocation.y))
  }
}

}