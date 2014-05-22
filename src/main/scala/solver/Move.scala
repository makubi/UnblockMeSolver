package solver {

import solver.Orientation.Orientation


trait Move {
  def distance: Int
  def pieceIndex: Int
  def orientation: Orientation
  def change(state: State): State = {
    val oldLocation = state(pieceIndex)
    state updated(pieceIndex, changeLocation(oldLocation))
  }
  def changeLocation(location: Location): Location

  override def toString = s"$pieceIndex:${this.getClass.getSimpleName.head}$distance"
}

case class Up(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Vertical
  override def changeLocation(oldLocation: Location): Location = {
    Location(oldLocation.x, oldLocation.y + distance)
  }
}

case class Down(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Vertical
  override def changeLocation(oldLocation: Location): Location = {
    Location(oldLocation.x, oldLocation.y - distance)
  }
}

case class Left(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Horizontal
  override def changeLocation(oldLocation: Location): Location = {
    Location(oldLocation.x - distance, oldLocation.y)
  }
}

case class Right(distance: Int, pieceIndex: Int) extends Move {
  val orientation = Orientation.Horizontal
  override def changeLocation(oldLocation: Location): Location = {
    Location(oldLocation.x + distance, oldLocation.y)
  }
}

}