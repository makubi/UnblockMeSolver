package akkaSolver.actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.actors.Solver.{StateOrderingForPriorityQueue, State}
import akkaSolver.actors.OpenList.{AddState, GetStateWithLowestFCost, DoneAddingNeighbours}


class OpenList() extends Actor with ActorLogging {

  val openList =  scala.collection.mutable.PriorityQueue.empty[State](StateOrderingForPriorityQueue)

  override def receive: Actor.Receive = LoggingReceive {

    case AddState(state: State) =>
      if(openList.exists(s => s.state == state.state)) {
        log.debug(s"$state already in queue")
      } else {
        openList.enqueue(state)
      }


    case GetStateWithLowestFCost =>
      sender() ! Solver.StateWithLowestFCost(getStateWithLowestFCost)

    case DoneAddingNeighbours(solver) => solver ! Solver.StateWithLowestFCost(getStateWithLowestFCost)
    case _ =>
  }

  def getStateWithLowestFCost = openList.dequeue()
}

object OpenList {

  def props() = Props(new OpenList)

  case class GetStateWithLowestFCost()
  case class AddState(state: State)
  case class AddStates(states: List[State])


  case class DoneAddingNeighbours(solver: ActorRef)

}

