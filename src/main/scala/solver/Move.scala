package solver {


trait Move {
  def distance: Int

  def change(state: State): State
}

case class Location(x: Int, y: Int)


case class Up(distance: Int, pieceIndex: Int) extends Move {
  override def change(state: State): State = {
    val oldLocation = state(pieceIndex)
    state updated(pieceIndex, Location(oldLocation.x, oldLocation.y + distance))
  }
}

case class Down(distance: Int, pieceIndex: Int) extends Move {
  override def change(state: State): State = {
    val oldLocation = state(pieceIndex)
    state updated(pieceIndex, Location(oldLocation.x, oldLocation.y - distance))
  }
}

case class Left(distance: Int, pieceIndex: Int) extends Move {
  override def change(state: State): State = {
    val oldLocation = state(pieceIndex)
    state updated(pieceIndex, Location(oldLocation.x - distance, oldLocation.y))
  }
}

case class Right(distance: Int, pieceIndex: Int) extends Move {
  override def change(state: State): State = {
    val oldLocation = state(pieceIndex)
    state updated(pieceIndex, Location(oldLocation.x + distance, oldLocation.y))
  }
}

}