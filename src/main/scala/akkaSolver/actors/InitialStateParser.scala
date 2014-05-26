package akkaSolver.actors

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.helpers.{UnblockMePiece, Orientation, UnblockMePieceWithLocation}
import akkaSolver.actors.InitialStateParser.{InitialStateParseError, InitialState, GetInitialState}
import scala.util.{Success, Try, Failure}

class InitialStateParser extends Actor with ActorLogging {

  def receive: Actor.Receive = LoggingReceive {

    case GetInitialState(initialState) =>
      val result: Try[InitialState] = Try(parse(initialState))
      result match {
        case Success(response) => sender ! response
        case Failure(exception) => sender ! InitialStateParseError(exception.getMessage)
      }
  }

  def parse(initialState: String): InitialState = {
    val split: Vector[String] = initialState.split("[|]").toVector
    val tilesWithLocations = split.map(tile => UnblockMePieceWithLocation(tile))

    val extractedState = tilesWithLocations
      .map{ case (_, locationOnTheMovableAxis) => locationOnTheMovableAxis.toString }
      .mkString("")

    val unblockMePieces = tilesWithLocations.unzip._1

    InitialState(extractedState, unblockMePieces)
  }
}

object InitialStateParser {
  case class InitialState(stateString: String, pieces: Vector[UnblockMePiece])
  case class InitialStateParseError(error: String)
  case class GetInitialState(initialState: String)

  def props() = Props(new InitialStateParser)
}
