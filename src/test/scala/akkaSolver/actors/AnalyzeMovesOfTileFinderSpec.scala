package akkaSolver.actors

import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akkaSolver.helpers.{Orientation, UnblockMePiece}
import akkaSolver.actors.MoveAnalyzer.GetNewStatesOfPieceRequest
import scala.concurrent.duration._
import akkaSolver.actors.NeighbourFinder.GetNewStatesOfPieceResponse


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

  "The AnalyzeMovesOfTileActor" should {

    "find all moves to the right correctly" in {

      val analyzeMovesOfTileActor: ActorRef = system.actorOf(MoveAnalyzer.props())
      val state = Vector(1)
      val pieces = Vector(UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4))

      analyzeMovesOfTileActor ! GetNewStatesOfPieceRequest(0, state, pieces)

      expectMsg(15 millis, GetNewStatesOfPieceResponse(0, state, Vector(2,3,4,5)))
    }

    "find all moves to the left correctly" in {

      val analyzeMovesOfTileActor: ActorRef = system.actorOf(MoveAnalyzer.props())
      val state = Vector(5)
      val pieces = Vector(UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4))

      analyzeMovesOfTileActor ! GetNewStatesOfPieceRequest(0, state, pieces)

      expectMsg(15 millis, GetNewStatesOfPieceResponse(0, state, Vector(1,2,3,4)))
    }

    "find all upward moves correctly" in {

      val analyzeMovesOfTileActor: ActorRef = system.actorOf(MoveAnalyzer.props())
      val state = Vector(1)
      val pieces = Vector(UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 4))

      analyzeMovesOfTileActor ! GetNewStatesOfPieceRequest(0, state, pieces)

      expectMsg(15 millis, GetNewStatesOfPieceResponse(0, state, Vector(2,3,4,5)))
    }

    "find all downward moves correctly" in {

      val analyzeMovesOfTileActor: ActorRef = system.actorOf(MoveAnalyzer.props())
      val state = Vector(5)
      val pieces = Vector(UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 4))

      analyzeMovesOfTileActor ! GetNewStatesOfPieceRequest(0, state, pieces)

      expectMsg(15 millis, GetNewStatesOfPieceResponse(0, state, Vector(1,2,3,4)))
    }

  }

  "The AnalyzeMovesOfTile Helper" should {
    "remove the correct element from a list when calling remove at index" in {
      val withoutIndex = MoveAnalyzer.removeAtIndex(1, "1234".toVector)
      assert(withoutIndex.mkString === "134")
    }

    "calculate the pieceMatrix correctly" in {

      val state = Vector(1)
      val pieces = Vector(UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4))

      val expectedArray = Array(
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array('0', '0', ' ', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' '),
        Array(' ', ' ', ' ', ' ', ' ', ' ')
      )

      assert(MoveAnalyzer.createArrayOfPiecesWithState(pieces, state) === expectedArray)

    }
  }


}

