import solver.helpers.UnblockMePieceWithLocation
import solver.{UnblockMePiece, Location, UnblockMeSolver}
object testWorksheet {

  val initialState: Vector[(UnblockMePiece, Location)] = Vector(
    UnblockMePieceWithLocation("G,1,4,2,H"),
    UnblockMePieceWithLocation("1,6,3,H"),
    UnblockMePieceWithLocation("1,3,2,V"),
    UnblockMePieceWithLocation("1,1,3,H"),
    UnblockMePieceWithLocation("3,5,3,V"),
    UnblockMePieceWithLocation("6,6,3,V"),
    UnblockMePieceWithLocation("5,3,2,H"),
    UnblockMePieceWithLocation("5,2,2,V")
  )
  val solver = new UnblockMeSolver(initialState)
  solver.moves
}

