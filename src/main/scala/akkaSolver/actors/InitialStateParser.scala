package akkaSolver.actors

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.helpers.{UnblockMePiece, Orientation, UnblockMePieceWithLocation}
import akkaSolver.actors.InitialStateParser.{ParseInitialStateError, ParseInitialStateResponse, ParseInitialStateRequest}
import scala.util.{Success, Try, Failure}

class InitialStateParser extends Actor with ActorLogging {

  def receive: Actor.Receive = LoggingReceive {

    case ParseInitialStateRequest(initialState) =>
      val result: Try[ParseInitialStateResponse] = Try(parse(initialState))
      result match {
        case Success(response) => sender ! response
        case Failure(exception) => sender ! ParseInitialStateError(exception.getMessage)
      }
  }

  def parse(initialState: String): ParseInitialStateResponse = {
    val split: Vector[String] = initialState.split("[|]").toVector
    val tilesWithLocations = split.map(tile => UnblockMePieceWithLocation(tile))

    val extractedState = tilesWithLocations
      .map{ case (_, locationOnTheMovableAxis) => locationOnTheMovableAxis.toString }
      .mkString("")

    val unblockMePieces = tilesWithLocations.unzip._1

    ParseInitialStateResponse(extractedState, unblockMePieces)
  }
}

object InitialStateParser {
  case class ParseInitialStateRequest(input: String)
  case class ParseInitialStateResponse(stateString: String, pieces: Vector[UnblockMePiece])
  case class ParseInitialStateError(error: String)

  def props() = Props(new InitialStateParser)
}
