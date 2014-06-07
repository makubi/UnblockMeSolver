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
        UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 6),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 1),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 1),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 3),
        UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 6),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 3),
        UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 5)
      )

      expectMsg(50 millis, ParseInitialStateResponse(expectedState, expectedPieces))
    }

    "repsond to a malformed GetInitialState() message correctly" in {

      val actorRef = system.actorOf(InitialStateParser.props())
      actorRef ! ParseInitialStateRequest("BOMMSFRIKADELLE")

      expectMsg(50 millis, ParseInitialStateError("KAPOTT - failed to match List(BOMMSFRIKADELLE)"))
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
