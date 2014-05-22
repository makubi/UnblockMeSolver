package solver

import org.scalatest.{BeforeAndAfter, FunSuite}
import solver.helpers.UnblockMePieceWithLocation

class TestgameIntegrationTest extends FunSuite with BeforeAndAfter {

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

  test ("foobar") {

    val moves = solver.moves

    val expectedMoves: Vector[Move] = Vector(Right(1, 1), Right(2, 1), Right(1, 3), Down(1, 4), Left(1, 6))

    assert(expectedMoves.toSet === moves.toSet)
  }

  test("Set equality") {

    val vec1 = Vector(Right(1, 1), Right(2, 1), Right(1, 3), Down(1, 4), Left(1, 6))
    val vec2 = vec1.reverse

    assert(vec1.toSet === vec2.toSet)
  }
}