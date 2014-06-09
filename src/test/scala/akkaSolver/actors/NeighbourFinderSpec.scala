package akkaSolver.actors

import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpecLike}
import akkaSolver.helpers.{Orientation, UnblockMePiece}
import akkaSolver.actors.NeighbourFinder.{InitializeWithState, FindNeighbours}
import akkaSolver.actors.Solver.{NeighboursFound, InitialState, State}
import scala.concurrent.duration._


class NeighbourFinderSpec extends TestKit(ActorSystem("TestKitUsageSpec",
  ConfigFactory.parseString(NeighbourFinderSpec.config)))
with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers {


  val stateString: String = "11315652"
  val pieces: Vector[UnblockMePiece] = Vector(
    UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4),
    UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 6),
    UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 1),
    UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 1),
    UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 3),
    UnblockMePiece(isGoalPiece = false, length = 3, orientation = Orientation.Vertical, positionOnTheFixedAxis = 6),
    UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 3),
    UnblockMePiece(isGoalPiece = false, length = 2, orientation = Orientation.Vertical, positionOnTheFixedAxis = 5)
  )

  "The NeighbourFinder" should {

    "not work properly when not initialized" in {
      val neighbourFinderActor = system.actorOf(NeighbourFinder.props())

      neighbourFinderActor ! FindNeighbours(State(state = stateString, g = 0, h = 0))

      expectNoMsg(20 millis)
    }

    "respond with a InitialState-Message if initialized correctly" in {
      val neighbourFinderActor = system.actorOf(NeighbourFinder.props())

      neighbourFinderActor ! InitializeWithState(stateString, pieces)

      val expectedMessage = InitialState(State(state = stateString, g = 0, h = 10))

      expectMsg(20 millis, expectedMessage)
    }

    "find all moves of one (and only one) vertical piece" in {
      val neighbourFinderActor = system.actorOf(NeighbourFinder.props())

      val piece: UnblockMePiece = UnblockMePiece(isGoalPiece = true, length = 2, orientation = Orientation.Horizontal, positionOnTheFixedAxis = 4)
      neighbourFinderActor ! InitializeWithState("3", Vector(piece))

      //Ignore InitialState Message
      expectMsgType[InitialState](20 millis)

      val state = State("3", 0, 2)
      neighbourFinderActor ! FindNeighbours(state)

      expectMsg(20 millis, NeighboursFound(List("4", "5", "1", "2").map(State(_, 1, 10)), state))
    }
  }
}

object NeighbourFinderSpec {

  val config = """
    akka {
      loglevel = "DEBUG"
      actor.debug.receive=on
    }
               """

}