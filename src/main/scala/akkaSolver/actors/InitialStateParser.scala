package akkaSolver.actors

import akka.actor.{Props, ActorLogging, Actor}
import akka.event.LoggingReceive
import akkaSolver.helpers.{Orientation, UnblockMePiece, UnblockMePieceWithLocation}
import akkaSolver.actors.InitialStateParser.ParseInitialStateResponse
import scala.util.{Success, Try, Failure}
import InitialStateParser._
import scala.util.matching.Regex
import akkaSolver.helpers.Orientation.Orientation
import akkaSolver.helpers.Orientation

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

    if (initialState.contains('|')) parseAkkaSolverStateString(initialState)
    else if (initialState.contains("Jam-")) parseRushHourInputString(initialState)
    else throw new IllegalArgumentException("unknown format")
  }

}

object InitialStateParser {
  case class ParseInitialStateRequest(input: String)
  case class ParseInitialStateResponse(stateString: String, pieces: Vector[UnblockMePiece])
  case class ParseInitialStateError(error: String)

  def props() = Props(new InitialStateParser)

  def parseRushHourInputString(initialState: String): ParseInitialStateResponse = {
    /*
Jam-1
6
1 2 h 2
0 1 v 3
0 0 h 2
3 1 v 3
2 5 h 3
0 4 v 2
4 4 h 2
5 0 v 3
     */

    case class ParseResult(name: String, puzzleSize: Int, pieces: List[String])
    val parseResult: ParseResult = initialState.split("\n").toList match {
      case name :: puzzleSize :: tiles => ParseResult(name, puzzleSize.toInt, tiles)
    }

    case class ParsedResult(piece: UnblockMePiece, stateOfPiece: Int)

    val parsedResult: List[ParsedResult] = parseResult.pieces.zipWithIndex.map {
      case (piece: String, index: Int) =>
        val pieceDetails = piece.split(" ")
        val x = pieceDetails(0).toInt + 1
        val y = pieceDetails(1).toInt + 1
        val orientation = if (pieceDetails(2).equalsIgnoreCase("v")) Orientation.Vertical else Orientation.Horizontal
        val length = pieceDetails(3).toInt
        val isGoalPiece = index == 0
        val positionOnFixedAxis = if (orientation == Orientation.Vertical) x else y
        val positionOnMovableAxis = if (orientation == Orientation.Vertical) y else x
        ParsedResult(UnblockMePiece(isGoalPiece, length, orientation, positionOnFixedAxis, index), positionOnMovableAxis)
    }

    ParseInitialStateResponse(parsedResult.map(r => r.stateOfPiece).mkString, parsedResult.map(r => r.piece).toVector)
  }

  def parseAkkaSolverStateString(initialState: String): ParseInitialStateResponse = {
    val split: Vector[(String, Int)] = initialState.split("[|]").toVector.zipWithIndex
    val tilesWithLocations = split.map{ case (tile, index) => UnblockMePieceWithLocation(tile, index) }

    val extractedState = tilesWithLocations
      .map{ case (_, locationOnTheMovableAxis) => locationOnTheMovableAxis.toString }
      .mkString("")

    val unblockMePieces: Vector[UnblockMePiece] = tilesWithLocations.unzip._1

    ParseInitialStateResponse(extractedState, unblockMePieces)
  }
}
