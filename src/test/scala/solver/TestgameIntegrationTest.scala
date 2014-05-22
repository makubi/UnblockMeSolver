package solver

import org.scalatest.{BeforeAndAfter, FunSuite}

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

    assert(expectedMoves === moves)

    //               Vector(Left(1,6), Down(1,4), Right(1,1), Right(2,1), Right(1,3))
    // did not equal Vector(Down(1,4), Left(1,6), Right(1,1), Right(2,1), Right(1,3))



  }
}

class SolverTest extends FunSuite with BeforeAndAfter {

  test("areTwoTilesAtTheSameLocation should return false if there are no overlapping tiles") {
    assert(!UnblockMeSolver.areTwoTilesAtTheSameLocation(Vector(UnblockMePieceWithLocation("G,1,4,2,H"), UnblockMePieceWithLocation("1,3,2,V"))))
  }

  test("areTwoTilesAtTheSameLocation should return true if there are overlapping tiles") {
    assert(UnblockMeSolver.areTwoTilesAtTheSameLocation(Vector(UnblockMePieceWithLocation("G,1,4,2,H"), UnblockMePieceWithLocation("1,4,2,V"))))
  }

  test("A tile, that is blocked by another tile must not move in the direction of the blocked tile (even if there is enough space behind the blocker)") {
    val initialState = Vector(UnblockMePieceWithLocation("1,3,6,H"), UnblockMePieceWithLocation("5,2,2,V"))

    val solver = new UnblockMeSolver(initialState)
    val moves = solver.moves

    assert(Vector.empty[Move] === moves)
  }

  test("getCollectionExceptElementAt") {

    val list = List(0,1,2,3,4,5)

    assert(UnblockMeSolver.getCollectionExceptElementAt(2, list) === List(0,1,3,4,5))

  }


}