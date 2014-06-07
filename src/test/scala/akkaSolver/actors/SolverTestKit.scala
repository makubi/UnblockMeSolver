package akkaSolver.actors

import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpecLike
import org.scalatest.Matchers

import com.typesafe.config.ConfigFactory

import akka.actor._
import akka.testkit._
import scala.concurrent.duration._
import akkaSolver.actors.Solver.Start
import akkaSolver.actors.InitialStateParser.ParseInitialStateRequest


class SolverTestKit
  extends TestKit(ActorSystem("SolverTestKit",
    ConfigFactory.parseString(SolverTestSpec.config)))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {


  val initialState = "G,1,4,2,H|1,6,3,H|1,3,2,V|1,1,3,H|3,5,3,V|6,6,3,V|5,3,2,H|5,2,2,V"

  "A Solver" should {

    "send the GetInitialState() message to the neighbourFinder" in {

      val openListRef = system.actorOf(OpenList.props())
      val closedListRef = system.actorOf(ClosedList.props())
      val neighbourFinderRef = system.actorOf(NeighbourFinder.props())
      val initialStateParserProbe = TestProbe()

      val makeOpenlist: (ActorRefFactory) => ActorRef = (_: ActorRefFactory) => openListRef
      val makeClosedList: (ActorRefFactory) => ActorRef = (_: ActorRefFactory) => closedListRef
      val makeNeighbour: (ActorRefFactory) => ActorRef = (_: ActorRefFactory) => neighbourFinderRef
      val makeInitialStateParser: (ActorRefFactory) => ActorRef = (_: ActorRefFactory) => initialStateParserProbe.ref

      //TODO: Test, if children get spawned properly
      val actorRef = system.actorOf(Props(new Solver(
        makeOpenlist,
        makeClosedList,
        makeNeighbour,
        makeInitialStateParser)))

      actorRef ! Start(initialState)
      initialStateParserProbe.expectMsg(50 millis, ParseInitialStateRequest(initialState))
    }
  }
}


object SolverTestSpec {
  // Define your test specific configuration here
  val config = """
    akka {
      loglevel = "DEBUG"
      actor.debug.receive=on
    }
               """
}
