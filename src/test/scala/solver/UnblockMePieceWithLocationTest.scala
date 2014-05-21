package solver

import org.scalatest.FunSuite

class UnblockMePieceWithLocationTest extends FunSuite {


  test("An input string should be parsed correctly") {

    val (piece, location) = UnblockMePieceWithLocation("G,1,5,2,H")

    assert(piece.isGoalPiece)
    assert(piece.length == 2)
    assert(piece.orientation == Orientation.Horizontal)

    assert(location.x == 1)
    assert(location.y == 5)
  }

  test("A lowercase string should be parsed correctly") {
    val (piece, location) = UnblockMePieceWithLocation("g,1,5,2,h")

    assert(piece.isGoalPiece)
    assert(piece.length == 2)
    assert(piece.orientation == Orientation.Horizontal)

    assert(location.x == 1)
    assert(location.y == 5)
  }

  test("A non goal piece should be parsed correctly") {
    val (piece, location) = UnblockMePieceWithLocation("1,5,2,h")

    assert(!piece.isGoalPiece)
    assert(piece.length == 2)
    assert(piece.orientation == Orientation.Horizontal)

    assert(location.x == 1)
    assert(location.y == 5)
  }

  test("Malformed input should rise an IllegalArgumentException") {
    intercept[IllegalArgumentException] {
      UnblockMePieceWithLocation(",5,2,h")
    }
    intercept[IllegalArgumentException] {
      UnblockMePieceWithLocation("1,,2,h")
    }
    intercept[IllegalArgumentException] {
      UnblockMePieceWithLocation("1,4,,h")
    }
    intercept[IllegalArgumentException] {
      UnblockMePieceWithLocation("1,4,2,x")
    }
    intercept[IllegalArgumentException] {
      UnblockMePieceWithLocation("x,4,2,x")
    }
    intercept[IllegalArgumentException] {
      UnblockMePieceWithLocation("1,x,2,x")
    }
    intercept[IllegalArgumentException] {
      UnblockMePieceWithLocation("1,5,x,x")
    }
  }

  test("A horizontal piece with a given location should block the right amount of location") {
    val (piece, location) = UnblockMePieceWithLocation("1,5,2,h")

    assert(piece.calcLocations(location) === Vector(Location(1,5), Location(2,5)))
  }

  test("A vertical piece with a given location should block the right amount of location") {
    val (piece, location) = UnblockMePieceWithLocation("1,5,2,v")

    assert(piece.calcLocations(location) === Vector(Location(1,5), Location(1,4)))
  }
}
