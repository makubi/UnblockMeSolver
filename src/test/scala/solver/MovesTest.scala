package solver

import org.scalatest.FunSuite

class MovesTest extends FunSuite {

  test("A vertical piece must only be moved left and right") {

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

  test("A horizontal piece must only be moved up and down") {

    val actualMoves = new UnblockMeSolver(Vector((UnblockMePiece(isGoalPiece = false, 2, orientation = Orientation.Vertical), Location(2, 3)))).moves

    val expectedMoves: Vector[Move] = Vector(
      Up(distance = 1, pieceIndex = 0),
      Up(distance = 2, pieceIndex = 0),
      Up(distance = 3, pieceIndex = 0),
      Down(distance = 1, pieceIndex = 0)
    )

    assert(actualMoves.filter(_.isInstanceOf[Up]) === expectedMoves.filter(_.isInstanceOf[Up]))
    assert(actualMoves.filter(_.isInstanceOf[Down]) === expectedMoves.filter(_.isInstanceOf[Down]))
  }
}

//               Vector(Down(1,0), Down(2,0), Down(3,0), Down(4,0))
// did not equal Vector(Up(1,0), Up(2,0), Up(3,0), Down(1,0))

