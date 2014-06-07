package akkaSolver.actors

import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akkaSolver.actors.OpenList.{GetNextStateToExplore, AddState}
import akkaSolver.actors.Solver.State
import scala.concurrent.duration._

class OpenListTest extends TestKit(ActorSystem("TestKitUsageSpec",
    ConfigFactory.parseString(OpenListTest.config)))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  "An openlist" should {

    "return the item with the lowest f value" in {
      val openListActor = system.actorOf(OpenList.props())

      openListActor ! AddState(State("111", 0, 111))
      openListActor ! AddState(State("112", 0, 112))
      openListActor ! AddState(State("110", 0, 110))

      openListActor ! GetNextStateToExplore

      expectMsg(10 millis, State("110", 0, 110))
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