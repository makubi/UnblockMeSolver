package solver

import org.scalatest.{BeforeAndAfter, FunSuite}
import solver.helpers.UnblockMePieceWithLocation


class SolverTest extends FunSuite with BeforeAndAfter {

  test("areTwoTilesAtTheSameLocation should return false if there are no overlapping tiles") {
    assert(!UnblockMeSolver.arePiecesOverlapping(Vector(UnblockMePieceWithLocation("G,1,4,2,H"), UnblockMePieceWithLocation("1,3,2,V"))))
  }

  test("areTwoTilesAtTheSameLocation should return true if there are overlapping tiles") {
    assert(UnblockMeSolver.arePiecesOverlapping(Vector(UnblockMePieceWithLocation("G,1,4,2,H"), UnblockMePieceWithLocation("1,4,2,V"))))
  }

  test("A tile, that is blocked by another tile must not move in the direction of the blocked tile (even if there is enough space behind the blocker)") {
    val initialState = Vector(UnblockMePieceWithLocation("1,3,6,H"), UnblockMePieceWithLocation("5,2,2,V"))

    val solver = new UnblockMeSolver(initialState)
    val moves = solver.moves

    assert(Vector.empty[Move] === moves)
  }

  test("getCollectionExceptElementAt") {

    val list = List(0, 1, 2, 3, 4, 5)

    assert(UnblockMeSolver.getCollectionExceptElementAt(2, list) === List(0, 1, 3, 4, 5))


  }
}
