package akkaSolver.actors

import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor.{Props, ActorRef, ActorRefFactory, ActorSystem}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akkaSolver.actors.Solver.{SolutionFound, Start}
import scala.concurrent.duration._


class SolverIntegrationTestSpec
  extends TestKit(ActorSystem("SolverIntegrationTestKit",
    ConfigFactory.parseString(SolverIntegrationTestSpec.config)))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val initialState = "G,1,4,2,H|1,6,3,H|1,3,2,V|1,1,3,H|3,5,3,V|6,6,3,V|5,3,2,H|5,2,2,V"

  "An IntegrationTest Solver" should {
    "do something useful" in {
      val openListRef = system.actorOf(OpenList.props(), "OpenList")
      val closedListRef = system.actorOf(ClosedList.props(), "ClosedList")
      val neighbourFinderRef = system.actorOf(NeighbourFinder.props(), "NeighbourFinder")
      val initialStateParserRef = system.actorOf(InitialStateParser.props(), "InitialStateParser")

      val makeOpenlist: (ActorRefFactory) => ActorRef = (_: ActorRefFactory) => openListRef
      val makeClosedList: (ActorRefFactory) => ActorRef = (_: ActorRefFactory) => closedListRef
      val makeNeighbour: (ActorRefFactory) => ActorRef = (_: ActorRefFactory) => neighbourFinderRef
      val makeInitialStateParser: (ActorRefFactory) => ActorRef = (_: ActorRefFactory) => initialStateParserRef

      val solverActor = system.actorOf(Props(new Solver(
        makeOpenlist,
        makeClosedList,
        makeNeighbour,
        makeInitialStateParser)), "Solver")

      solverActor ! Start(initialState)

      expectMsgType[SolutionFound](1 seconds)
    }
  }
}

object SolverIntegrationTestSpec {
  // Define your test specific configuration here
  val config = """
    akka {
      loglevel = "DEBUG"
      actor.debug.receive=on
    }
               """
}