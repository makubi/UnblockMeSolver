package solver

import org.scalatest.FunSuite
import solver.helpers.UnblockMePieceWithLocation

class MovesTest extends FunSuite {

  test("A horizontal piece must only be moved left and right") {

    val actualMoves = new UnblockMeSolver(Vector((UnblockMePiece(isGoalPiece = false, 2, orientation = Orientation.Horizontal), Location(2,1)))).moves

    val expectedMoves: Vector[Move] = Vector(
      Left(distance = 1, pieceIndex = 0),
      Right(distance = 1, pieceIndex = 0),
      Right(distance = 2, pieceIndex = 0),
      Right(distance = 3, pieceIndex = 0)
    )

    assert(actualMoves.filter(_.isInstanceOf[Left]) === expectedMoves.filter(_.isInstanceOf[Left]))
    assert(actualMoves.filter(_.isInstanceOf[Right]) === expectedMoves.filter(_.isInstanceOf[Right]))
  }

  test("A vertical piece must only be moved up and down") {

    val actualMoves = new UnblockMeSolver(Vector(UnblockMePieceWithLocation("2,3,2,V"))).moves

    val expectedMoves: Vector[Move] = Vector(
      Up(distance = 1, pieceIndex = 0),
      Up(distance = 2, pieceIndex = 0),
      Up(distance = 3, pieceIndex = 0),
      Down(distance = 1, pieceIndex = 0)
    )

    assert(actualMoves.filter(_.isInstanceOf[Up]) === expectedMoves.filter(_.isInstanceOf[Up]))
    assert(actualMoves.filter(_.isInstanceOf[Down]) === expectedMoves.filter(_.isInstanceOf[Down]))
  }

  test("A vertical piece at the bottom must only be moved upwards") {

    val actualMoves = new UnblockMeSolver(Vector(UnblockMePieceWithLocation("5,2,2,V"))).moves

    val expectedMoves: Vector[Move] = Vector(
      Up(distance = 1, pieceIndex = 0),
      Up(distance = 2, pieceIndex = 0),
      Up(distance = 3, pieceIndex = 0),
      Up(distance = 4, pieceIndex = 0)
    )

    assert(actualMoves === expectedMoves)
  }

  test("Up-Move must update the state of the piece accordingly") {
    val initialState:State = Vector(Location(3,3), Location(5,5))

    val expectedState = Vector(Location(3,5), Location(5,5))

    assert(Up(2, 0).change(initialState) === expectedState)
  }

  test("Down-Move must update the state of the piece accordingly") {
    val initialState:State = Vector(Location(3,3), Location(5,5))

    val expectedState = Vector(Location(3,1), Location(5,5))

    assert(Down(2, 0).change(initialState) === expectedState)
  }

  test("Left-Move must update the state of the piece accordingly") {
    val initialState:State = Vector(Location(3,3), Location(5,5))

    val expectedState = Vector(Location(1,3), Location(5,5))

    assert(Left(2, 0).change(initialState) === expectedState)
  }

  test("Right-Move must update the state of the piece accordingly") {
    val initialState:State = Vector(Location(3,3), Location(5,5))

    val expectedState = Vector(Location(5,3), Location(5,5))

    assert(Right(2, 0).change(initialState) === expectedState)
  }

  test("toString") {
    assert("0:U1" === Up(distance = 1, pieceIndex = 0).toString)

  }
}

//               Vector(Down(1,0), Down(2,0), Down(3,0), Down(4,0))
// did not equal Vector(Up(1,0), Up(2,0), Up(3,0), Down(1,0))

