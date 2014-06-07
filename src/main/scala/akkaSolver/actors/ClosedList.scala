package akkaSolver.actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.actors.Solver.{AddState, State}
import akkaSolver.actors.ClosedList.AddToOpenListIfNotOnClosedList
import akkaSolver.actors.OpenList.DoneAddingNeighbours

class ClosedList extends Actor with ActorLogging {

  val closedList = scala.collection.mutable.Set.empty[State]

  override def receive: Actor.Receive = LoggingReceive {

    case AddToOpenListIfNotOnClosedList(states, openList) =>

      val explorer = sender()
      states.foreach {
        state => {
          if (closedList.contains(state)) {
            //do nothing
          } else {
            openList ! AddState(state)
          }
        }
      }

      openList ! DoneAddingNeighbours(explorer)


    case _ =>
  }
}

object ClosedList {

  def props() = Props(new ClosedList)

  case class AddToOpenListIfNotOnClosedList(states: List[State], openList: ActorRef)

}

