package akkaSolver.actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.actors.Solver.State
import akkaSolver.actors.OpenList.{AddStateToOpenList, DoneAddingNeighbours}
import scala.annotation.tailrec
import ClosedList._

class ClosedList extends Actor with ActorLogging {

  val closedList = scala.collection.mutable.Set.empty[String]

  override def receive: Actor.Receive = LoggingReceive {

    case AddToOpenListIfNotOnClosedList(parentState, states, openList) =>

      val solver = sender()
      states.foreach {
        state => {

          if (!closedList.contains(state.state)) {
            openList ! AddStateToOpenList(state, parentState)
          }
        }
      }

      log.debug(s"closedList has ${closedList.size} entries")
      openList ! DoneAddingNeighbours(solver)

    case AddStateToClosedList(state) =>
      closedList.add(state.state)
  }
}

object ClosedList {

  def props() = Props(new ClosedList)

  case class AddStateToClosedList(state: State)

  case class AddToOpenListIfNotOnClosedList(parentState: State, states: List[State], openList: ActorRef)
}

