package akkaSolver.actors

import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._
import akkaSolver.actors.InitialStateParser.{ParseInitialStateError, ParseInitialStateResponse, ParseInitialStateRequest}
import akkaSolver.helpers.{Orientation, UnblockMePiece}

class ParseInitialStateResponseParserSpec extends TestKit(ActorSystem("TestKitUsageSpec",
  ConfigFactory.parseString(ParseInitialStateResponseParserSpec.config)))
with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {


  val initialState = "G,1,4,2,H|1,6,3,H|1,3,2,V|1,1,3,H|3,5,3,V|6,6,3,V|5,3,2,H|5,2,2,V"

  "The InitialStateParser" should {

    "parse a valid GetInitialState() message correctly" in {

      val initialStateParserActor = system.actorOf(InitialStateParser.props())
      initialStateParserActor ! ParseInitialStateRequest(initialState)

      val expectedState: String = "11315652"
      val expectedPieces: Vector[UnblockMePiece] = Vector(
        UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4, pieceIndex = 0),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 6, pieceIndex = 1),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 1, pieceIndex = 2),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 1, pieceIndex = 3),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 3, pieceIndex = 4),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 6, pieceIndex = 5),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 3, pieceIndex = 6),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 5, pieceIndex = 7)
      )

      expectMsg(50 millis, ParseInitialStateResponse(expectedState, expectedPieces))
    }

    "repsond to a malformed GetInitialState() message correctly" in {

      val actorRef = system.actorOf(InitialStateParser.props())
      actorRef ! ParseInitialStateRequest("BOMMSFRIKADELLE")

      expectMsg(50 millis, ParseInitialStateError("unknown format"))
    }

    "parse a RushHour Jam correctly" in {
      val input = """Jam-1
                    |6
                    |1 2 h 2
                    |0 1 v 3
                    |0 0 h 2
                    |3 1 v 3
                    |2 5 h 3
                    |0 4 v 2
                    |4 4 h 2
                    |5 0 v 3""".stripMargin

      val expectedState: String = "25153256"
      val expectedPieces: Vector[UnblockMePiece] = Vector(
        UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4, pieceIndex = 0),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 1, pieceIndex = 1),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 6, pieceIndex = 2),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 4, pieceIndex = 3),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 1, pieceIndex = 4),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 1, pieceIndex = 5),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 2, pieceIndex = 6),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 6, pieceIndex = 7)
      )

      val initialStateParserActor = system.actorOf(InitialStateParser.props())
      initialStateParserActor ! ParseInitialStateRequest(input)

      expectMsg(50 millis, ParseInitialStateResponse(expectedState, expectedPieces))
    }
  }
}

object ParseInitialStateResponseParserSpec {

  val config = """
    akka {
      loglevel = "DEBUG"
      actor.debug.receive=on
    }
               """

}
