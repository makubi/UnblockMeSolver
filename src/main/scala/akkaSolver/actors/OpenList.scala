package akkaSolver.actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.actors.Solver._
import akkaSolver.actors.OpenList._
import scala.collection.mutable
import scala.annotation.tailrec
import akkaSolver.actors.OpenList.GetStateWithLowestFCost
import scala.Some
import akkaSolver.actors.OpenList.SolutionFoundProvidePathRequest
import akkaSolver.actors.OpenList.DoneAddingNeighbours
import akkaSolver.actors.Solver.State
import akkaSolver.actors.OpenList.GetParentOfStateResponse
import akkaSolver.actors.OpenList.GetParentOfStateRequest
import akkaSolver.actors.Solver.StateWithLowestFCost
import akkaSolver.actors.OpenList.AddStateToOpenList

class OpenList() extends Actor with ActorLogging {

  var openList = mutable.PriorityQueue.empty[State](StateOrderingForPriorityQueue)
  val childToParentRelation: mutable.Map[String, Option[String]] = scala.collection.mutable.Map.empty[String, Option[String]]
  val stateDictionary = scala.collection.mutable.Map.empty[String, State]

  override def receive: Actor.Receive = LoggingReceive {

    case AddStateToOpenList(newState: State, parentState: State) =>
      if (openList.exists(s => s.state == newState.state)) {

        //check, if the g-costs of the existing newState are higher than the g-costs of the current-newState
        val oldStateOpt = openList.find(s => s.state == newState.state)

        //new state is more efficient -- use the new one instead
        if (oldStateOpt.isDefined && newState.g < oldStateOpt.get.g) {

          //Update Lookup
          stateDictionary(newState.state) = newState

          //Update Lookup of parent
          stateDictionary(parentState.state) = parentState

          //Update relation
          childToParentRelation(newState.state) = Some(parentState.state)

          //remove oldState (the inefficient one) from queue
          openList = updateItemInQueue(newState, oldStateOpt.get)
        }
      } else {
        openList.enqueue(newState)
        stateDictionary(newState.state) = newState
        stateDictionary(parentState.state) = parentState
        childToParentRelation(newState.state) = Some(parentState.state)
      }


    case GetParentOfStateRequest(stateString) =>
      val parentOption = childToParentRelation.getOrElse(stateString, None).map(x => stateDictionary(x))
      sender() ! GetParentOfStateResponse(parentOption, stateDictionary(stateString))


    case SolutionFoundProvidePathRequest(initialStateString, finalState) =>
      val path: List[String] = calcSolutionPath(initialStateString, finalState.state, childToParentRelation.toMap)
      sender ! SolutionFoundProvidePathResponse(initialStateString, path, Nil)


    case GetStateWithLowestFCost =>
      val stateWithLowestFCost: StateWithLowestFCost = Solver.StateWithLowestFCost(getStateWithLowestFCost)
      sender() ! stateWithLowestFCost


    case DoneAddingNeighbours(solver) => solver ! Solver.StateWithLowestFCost(getStateWithLowestFCost)
  }

  def updateItemInQueue(newState: State, oldState: State): mutable.PriorityQueue[State] = {
    val newQueue = openList.filter(s => s == oldState)
    newQueue.enqueue(newState)

    newQueue
  }

  def getStateWithLowestFCost = openList.dequeue()
}

object OpenList {

  def props() = Props(new OpenList)

  def calcSolutionPath(initialState: String, finalState: String, childToParentRelation: Map[String, Option[String]]): List[String] = {

    @tailrec
    def helper(state: String, path: List[String]): List[String] = {
      if (state == initialState) state :: path
      else {
        val parentOpt = childToParentRelation(state)
        parentOpt match {
          case None => path
          case Some(parent) => helper(parent, state :: path)
        }
      }
    }

    helper(finalState, Nil)
  }


  case class GetStateWithLowestFCost()

  case class AddStateToOpenList(state: State, parentState: State)

  case class AddStates(states: List[State])

  case class DoneAddingNeighbours(solver: ActorRef)

  case class GetParentOfStateRequest(stateString: String)

  case class GetParentOfStateResponse(parent: Option[State], state: State)

  case class SolutionFoundProvidePathRequest(initialStateString: String, finalState: State)

}

