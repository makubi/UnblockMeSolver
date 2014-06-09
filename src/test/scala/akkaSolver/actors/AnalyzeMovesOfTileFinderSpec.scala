package akkaSolver.actors

import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akkaSolver.helpers.{Orientation, UnblockMePiece}
import akkaSolver.actors.MoveAnalyzer.GetNewStatesOfPieceRequest
import scala.concurrent.duration._
import akkaSolver.actors.NeighbourFinder.{FindNeighbours, GetNewStatesOfPieceResponse}
import akkaSolver.actors.Solver.{NeighboursFound, State, InitialState}


object AnalyzeMovesOfTileFinderSpec {

  val config = """
    akka {
      loglevel = "DEBUG"
      actor.debug.receive=on
    }
               """

}

class AnalyzeMovesOfTileFinderSpec extends TestKit(ActorSystem("TestKitUsageSpec",
  ConfigFactory.parseString(AnalyzeMovesOfTileFinderSpec.config)))
with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  "The MoveAnalyzer" should {

    "find all moves to the right correctly" in {

      val analyzeMovesOfTileActor: ActorRef = system.actorOf(MoveAnalyzer.props())
      val state = Vector(1)
      val pieces = Vector(UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4, pieceIndex = 0))

      analyzeMovesOfTileActor ! GetNewStatesOfPieceRequest(0, state, pieces)

      expectMsg(15 millis, GetNewStatesOfPieceResponse(0, state, Vector(2,3,4,5)))
    }

    "find all moves to the left correctly" in {

      val analyzeMovesOfTileActor: ActorRef = system.actorOf(MoveAnalyzer.props())
      val state = Vector(5)
      val pieces = Vector(UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4, pieceIndex = 0))

      analyzeMovesOfTileActor ! GetNewStatesOfPieceRequest(0, state, pieces)

      expectMsg(15 millis, GetNewStatesOfPieceResponse(0, state, Vector(4,3,2,1)))
    }

    "find all upward moves correctly" in {

      val analyzeMovesOfTileActor: ActorRef = system.actorOf(MoveAnalyzer.props())
      val state = Vector(1)
      val pieces = Vector(UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 4, pieceIndex = 0))

      analyzeMovesOfTileActor ! GetNewStatesOfPieceRequest(0, state, pieces)

      expectMsg(15 millis, GetNewStatesOfPieceResponse(0, state, Vector(2,3,4,5,6)))
    }

    "find all downward moves correctly" in {

      val analyzeMovesOfTileActor: ActorRef = system.actorOf(MoveAnalyzer.props())
      val state = Vector(6)
      val pieces = Vector(UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 4, pieceIndex = 0))

      analyzeMovesOfTileActor ! GetNewStatesOfPieceRequest(0, state, pieces)

      expectMsg(15 millis, GetNewStatesOfPieceResponse(0, state, Vector(5,4,3,2)))
    }

    "handle this integration test correctly" in {
      val moveAnalyzer = system.actorOf(MoveAnalyzer.props())

      val stateString: String = "11315652"
      val state: Vector[Int] = stateString.map(_.asDigit).toVector

      val pieces: Vector[UnblockMePiece] = Vector(
        UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4, pieceIndex = 0),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 6, pieceIndex = 1),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 1, pieceIndex = 2),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 1, pieceIndex = 3),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 3, pieceIndex = 4),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 6, pieceIndex = 5),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 3, pieceIndex = 6),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 5, pieceIndex = 7)
      )
      moveAnalyzer ! GetNewStatesOfPieceRequest(4, state, pieces)

      expectMsg(15 millis, GetNewStatesOfPieceResponse(4, state, Vector(4)))
    }
  }

  "The AnalyzeMovesOfTile Helper" should {
    "remove the correct element from a list when calling remove at index" in {
      val withoutIndex = MoveAnalyzer.removeAtIndex(1, "1234".toVector)
      assert(withoutIndex.mkString === "134")
    }

    "calculate the pieceMatrix correctly for a horizontal piece" in {

      val state = Vector(1)
      val pieces = Vector(UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4, pieceIndex = 0))

      val expectedArray = Array(
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array('G', 'G', ' ', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' ')
      )

      assert(MoveAnalyzer.createArrayOfPiecesWithState(pieces, state) === expectedArray)

    }

    "calculate the pieceMatrix correctly for a vertical piece" in {

      val state = Vector(5)
      val pieces = Vector(UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 3, pieceIndex = 0))

      val expectedArray = Array(
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array(' ', ' ', '0', ' ', ' ', ' '),
        Array(' ', ' ', '0', ' ', ' ', ' '),
        Array(' ', ' ', '0', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' ')
      )

      assert(MoveAnalyzer.createArrayOfPiecesWithState(pieces, state) === expectedArray)

    }

    "calculate the pieceMatrix correctly for a the sample grid" in {

      val state = Vector(1, 1, 3, 1, 5, 6, 5, 2)
      val pieces: Vector[UnblockMePiece] = Vector(
        UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4, pieceIndex = 0),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 6, pieceIndex = 1),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 1, pieceIndex = 2),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 1, pieceIndex = 3),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 3, pieceIndex = 4),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 6, pieceIndex = 5),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 3, pieceIndex = 6),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 5, pieceIndex = 7)
      )

      val expectedArray = Array(
        Array('1', '1', '1', ' ', ' ', '5'),
        Array(' ', ' ', '4', ' ', ' ', '5'),
        Array('G', 'G', '4', ' ', ' ', '5'),
        Array('2', ' ', '4', ' ', '6', '6'),
        Array('2', ' ', ' ', ' ', '7', ' '),
        Array('3', '3', '3', ' ', '7', ' ')
      )

      assert(MoveAnalyzer.createArrayOfPiecesWithState(pieces, state) === expectedArray)

    }
  }


}

