package akkaSolver.actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.actors.Solver.{SolutionFoundProvidePathResponse, State}
import akkaSolver.actors.OpenList.{AddState, DoneAddingNeighbours}
import scala.collection.mutable
import scala.annotation.tailrec
import ClosedList._

class ClosedList extends Actor with ActorLogging {

  val closedList = scala.collection.mutable.Set.empty[String]
  val closedListDict = scala.collection.mutable.Map.empty[String, State]
  val childToParentRelation: mutable.Map[String, Option[String]] = scala.collection.mutable.Map.empty[String, Option[String]]

  override def receive: Actor.Receive = LoggingReceive {

    case AddToOpenListIfNotOnClosedList(parentState, states, openList) =>

      val solver = sender()
      states.foreach {
        state => {
          childToParentRelation(state.state) = Some(parentState.state)

          if (closedList.contains(state.state)) {
            //check, if state.g is lower than the previously stored g
            val oldState = closedListDict.get(state.state)
            if(oldState.isDefined && state.g < oldState.get.g) {
              childToParentRelation(state.state) = Some(parentState.state)
            }
          } else {
            openList ! AddState(state)
          }
        }
      }

      log.info(s"closedList has ${closedList.size} entries")
      openList ! DoneAddingNeighbours(solver)

    case AddState(state) =>
      closedList.add(state.state)
      closedListDict(state.state) = state
      childToParentRelation(state.state) = None

    case SolutionFoundProvidePathRequest(initialState, finalState: State) =>
      sender ! SolutionFoundProvidePathResponse(initialState, calcSolutionPath(finalState, childToParentRelation.toMap), Nil)
  }
}

object ClosedList {

  def props() = Props(new ClosedList)

  case class AddToOpenListIfNotOnClosedList(parentState: State, states: List[State], openList: ActorRef)

  case class SolutionFoundProvidePathRequest(initialState: String, finalState: State)

  def calcSolutionPath(finalState: State, childToParentRelation: Map[String, Option[String]]) = {

    @tailrec
    def helper(state: String, path: List[String]): List[String] = {
      val parentOpt = childToParentRelation(state)
      parentOpt match {
        case None => path
        case Some(parent) => helper(parent, state :: path )
      }
    }

    helper(finalState.state, Nil)
  }


}

