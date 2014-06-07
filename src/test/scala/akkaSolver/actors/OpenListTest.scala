package akkaSolver.actors

import akka.testkit._
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import scala.concurrent.duration._
import akkaSolver.actors.Solver.{StateWithLowestFCost, State}
import akkaSolver.actors.OpenList.AddState
import akkaSolver.actors.OpenList.GetStateWithLowestFCost

class OpenListInternalsTest extends TestKit(ActorSystem("TestKitUsageSpec",
  ConfigFactory.parseString(OpenListTest.config)))
with DefaultTimeout with ImplicitSender with WordSpecLike with BeforeAndAfterAll {

  var openListActorRef: TestActorRef[OpenList] = _
  var openListActor: OpenList = _

  override def beforeAll() {
    openListActorRef = TestActorRef[OpenList](OpenList.props(), "OpenList")
    openListActor = openListActorRef.underlyingActor
  }

  "An openlist" should {
    "add a state to the list" in {

      assert(openListActor.openList.size === 0)

      openListActorRef ! AddState(State("111", 0, 111))
      expectNoMsg(10 millis)

      assert(openListActor.openList.size === 1)
    }

    "add duplicate state only once" in {
      openListActorRef ! AddState(State("111", 0, 111))
      openListActorRef ! AddState(State("111", 0, 111))
      expectNoMsg(10 millis)

      assert(openListActor.openList.size === 1)
    }

    "remove a state from the internal queue if GetNextStateToExploreIsReceived" in {
      openListActorRef ! AddState(State("111", 0, 111))
      openListActorRef ! AddState(State("112", 0, 112))
      openListActorRef ! AddState(State("110", 0, 110))

      openListActorRef ! GetStateWithLowestFCost

      assert(openListActor.openList.size === 2)
      expectMsg(5 millis, StateWithLowestFCost(State("110", 0, 110)))

      val statesInQueue = openListActor.openList.toList.map(s => s.state)
      assert(statesInQueue === List("111", "112"))
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