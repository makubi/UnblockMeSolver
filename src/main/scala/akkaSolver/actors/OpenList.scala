package akkaSolver.actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.actors.Solver.State
import akkaSolver.actors.OpenList.DoneAddingNeighbours


class OpenList extends Actor with ActorLogging {

  val openList =  scala.collection.mutable.PriorityQueue.empty[State]

  override def receive: Actor.Receive = LoggingReceive {

    case DoneAddingNeighbours(solver) =>
      val stateWithLowestFCost: State = openList.dequeue()
      solver ! Solver.StateWithLowestFCost(stateWithLowestFCost)
    case _ =>
  }
}

object OpenList {

  def props() = Props(new OpenList)

  case class GetNextStateToExplore()
  case class AddStates(states: List[State])

  case class DoneAddingNeighbours(solver: ActorRef)

}

