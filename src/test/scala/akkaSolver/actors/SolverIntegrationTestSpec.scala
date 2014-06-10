package akkaSolver.actors

import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor._
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akkaSolver.actors.Solver.{SolutionFound, Start}
import scala.concurrent.duration._
import java.util.logging.Logger
import akkaSolver.actors.Solver.SolutionFound
import akkaSolver.actors.Solver.Start


class SolverIntegrationTestSpec
  extends TestKit(ActorSystem("SolverIntegrationTestKit",
    ConfigFactory.parseString(SolverIntegrationTestSpec.config)))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val makeOpenlist: (ActorRefFactory) => ActorRef = _.actorOf(OpenList.props(), "OpenList")
  val makeClosedList: (ActorRefFactory) => ActorRef = _.actorOf(ClosedList.props(), "ClosedList")
  val makeNeighbour: (ActorRefFactory) => ActorRef = _.actorOf(NeighbourFinder.props(), "NeighbourFinder")
  val makeInitialStateParser: (ActorRefFactory) => ActorRef = _.actorOf(InitialStateParser.props(), "InitialStateParser")

  val solverActor = system.actorOf(Props(new Solver(
    makeOpenlist,
    makeClosedList,
    makeNeighbour,
    makeInitialStateParser)), "Solver")


  "An IntegrationTest Solver" should {
    "find the solution for puzzle #1 (the easiest)" in {

      val initialState = "G,1,4,2,H|1,6,3,H|1,3,2,V|1,1,3,H|3,5,3,V|6,6,3,V|5,3,2,H|5,2,2,V"

      solverActor ! Start(initialState)

      val solution: SolutionFound = expectMsgType[SolutionFound](10 seconds)
      println(solution)
    }

    "find the solution for puzzle #22 (Beginner but not _that_ easy)" in {

      val initialState = "G,2,4,2,H|1,6,3,v|1,3,2,h|2,6,2,h|3,3,2,v|4,6,2,h|4,5,2,v|4,3,2,v|5,4,2,v|5,2,2,h|6,6,3,v"

      solverActor ! Start(initialState)

      val solution: SolutionFound = expectMsgType[SolutionFound](10 seconds)
      println(solution)
    }
  }
}

object SolverIntegrationTestSpec {
  // Define your test specific configuration here
  val config = """
    akka {
      loglevel = "INFO"
      actor.debug.receive=off
    }
               """
}