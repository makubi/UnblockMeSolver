package solver

import org.scalatest.{BeforeAndAfter, FunSuite}

class TestgameIntegrationTest extends FunSuite with BeforeAndAfter {

  val initialState: Vector[(UnblockMePiece, Location)] = Vector(
    UnblockMePieceWithLocation("G,1,5,2,H"),
    UnblockMePieceWithLocation("1,6,3,H"),
    UnblockMePieceWithLocation("1,3,2,V"),
    UnblockMePieceWithLocation("1,1,3,H"),
    UnblockMePieceWithLocation("3,5,3,V"),
    UnblockMePieceWithLocation("6,6,3,V"),
    UnblockMePieceWithLocation("5,3,2,H"),
    UnblockMePieceWithLocation("5,2,2,V")
  )
  
  val solver = new UnblockMeSolver(initialState)

  test ("foobar") {

    val moves = solver.moves

    assert(moves.size === 5)
  }
}