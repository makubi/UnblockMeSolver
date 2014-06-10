package akkaSolver.actors

import akka.testkit._
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterEach, WordSpecLike}
import scala.concurrent.duration._
import akkaSolver.actors.Solver._
import akkaSolver.actors.OpenList._
import akkaSolver.actors.OpenList.GetStateWithLowestFCost
import akkaSolver.actors.OpenList.GetParentOfStateResponse
import akkaSolver.actors.OpenList.GetParentOfStateRequest
import akkaSolver.actors.OpenList.AddStateToOpenList
import scala.Some
import akkaSolver.actors.Solver.State
import akkaSolver.actors.OpenList.GetStateWithLowestFCost
import akkaSolver.actors.OpenList.GetParentOfStateResponse
import akkaSolver.actors.OpenList.GetParentOfStateRequest
import akkaSolver.actors.OpenList.AddStateToOpenList
import akkaSolver.actors.Solver.StateWithLowestFCost
import scala.Some
import akkaSolver.actors.OpenList.SolutionFoundProvidePathRequest

class OpenListInternalsTest extends TestKit(ActorSystem("TestKitUsageSpec",
  ConfigFactory.parseString(OpenListTest.config)))
with DefaultTimeout with ImplicitSender with WordSpecLike with BeforeAndAfterEach {

  var openListActorRef: TestActorRef[OpenList] = _
  var openListActor: OpenList = _

  override def beforeEach() {
    openListActorRef = TestActorRef[OpenList](OpenList.props())
    openListActor = openListActorRef.underlyingActor
  }

  override def afterEach() {
    system.stop(openListActorRef)
  }

  "An openlist" should {
    "add a state to the list" in {

      assert(openListActor.openList.size === 0)
      val state = State("111", 1, 111)
      val parent = State("110", 0, 110)
      openListActorRef ! AddStateToOpenList(state, parent)
      expectNoMsg(10 millis)

      assert(openListActor.openList.size === 1)
    }

    "add duplicate state only once" in {
      val state = State("111", 1, 111)
      val parent = State("110", 0, 110)
      openListActorRef ! AddStateToOpenList(state, parent)
      openListActorRef ! AddStateToOpenList(state, parent)
      expectNoMsg(10 millis)

      assert(openListActor.openList.size === 1)
    }

    "remove a state from the internal queue if GetNextStateToExploreIsReceived" in {
      val parent = State("110", 0, 110)
      openListActorRef ! AddStateToOpenList(State("111", 0, 111), parent)
      openListActorRef ! AddStateToOpenList(State("112", 0, 112), parent)
      openListActorRef ! AddStateToOpenList(State("113", 0, 113), parent)

      openListActorRef ! GetStateWithLowestFCost
      expectMsg(5 millis, StateWithLowestFCost(State("111", 0, 111)))

      assert(openListActor.openList.size === 2)

      val statesInQueue = openListActor.openList.toList.map(s => s.state)
      assert(statesInQueue === List("112", "113"))
    }

    "return the parent of a state" in {
      val state: State = State("111", 0, 111)
      val parent = State("110", 0, 110)

      openListActorRef ! AddStateToOpenList(state, parent)

      openListActorRef ! GetParentOfStateRequest(state.state)

      expectMsg(5 millis, GetParentOfStateResponse(Some(parent), state))
    }

    "update the parent of a child, if the state has lower g-cost" in {
      val state: State = State("111",20, 111)
      val parent = State("110", 10, 110)

      openListActorRef ! AddStateToOpenList(state, parent)

      val state2MoreEfficient = State("111", 18, 111)
      val parent2 = State("112", 10, 112)

      openListActorRef ! AddStateToOpenList(state2MoreEfficient, parent2)

      openListActorRef ! GetParentOfStateRequest("111")

      expectMsg(5 millis, GetParentOfStateResponse(Some(parent2), state2MoreEfficient))
    }

    "keep the parent of a child, if the state has higher g-cost" in {
      val state: State = State("111",20, 111)
      val parent = State("110", 10, 110)
      openListActorRef ! AddStateToOpenList(state, parent)

      val state2LessEfficient = State("111", 22, 111)
      val parent2 = State("112", 10, 112)
      openListActorRef ! AddStateToOpenList(state2LessEfficient, parent2)

      openListActorRef ! GetParentOfStateRequest("111")

      expectMsg(5 millis, GetParentOfStateResponse(Some(parent), state))
    }

    "construct a valid solution path" in {
      val parent = State("110", 0, 110)
      val state: State = State("111",10, 111)
      val state2: State = State("112",20, 112)

      openListActorRef ! AddStateToOpenList(state, parent)
      openListActorRef ! AddStateToOpenList(state2, state)

      openListActorRef ! SolutionFoundProvidePathRequest(parent.state, state2)

      expectMsg(5 millis, SolutionFoundProvidePathResponse(parent.state, List(parent.state, state.state, state2.state), Nil))
    }
  }
}

object OpenListTest {

  val config = """
    akka {
      loglevel = "DEBUG"
      actor.debug.receive=on
    }
               """


}