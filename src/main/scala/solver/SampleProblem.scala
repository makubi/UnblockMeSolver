package solver

import solver.helpers.UnblockMePieceWithLocation
import scala.concurrent.duration._

object SampleProblem {

  def main(args: Array[String]) {

    println("Sleeping for 10s")
    Thread.sleep(10000)

    val startMillis: Long = System.currentTimeMillis()
    println(s"Starting... (on $startMillis)")



    val initialState: Vector[(UnblockMePiece, Location)] = Vector(
      UnblockMePieceWithLocation("G,1,4,2,H"),
      UnblockMePieceWithLocation("1,6,3,H"),
      UnblockMePieceWithLocation("1,3,2,V"),
      UnblockMePieceWithLocation("1,1,3,H"),
      UnblockMePieceWithLocation("3,5,3,V"),
      UnblockMePieceWithLocation("6,6,3,V"),
      UnblockMePieceWithLocation("5,3,2,H"),
      UnblockMePieceWithLocation("5,2,2,V")
    )

    val solver = new UnblockMeSolver(initialState)

    println(solver.solution(Location(5,4)).take(1).toList)

    val endMillis = System.currentTimeMillis()

    val diff = endMillis.-(startMillis)
    val duration = diff.millis

    println(s"Finished... (on $startMillis)")

    println(s"Duration: ${duration.toSeconds}s")

    println("Sleeping for 5mins")
    Thread.sleep(5*60*1000)
  }
}

object HardestProblem {

  def main(args: Array[String]) {

    println("Sleeping for 10s")
    Thread.sleep(10000)

    val startMillis: Long = System.currentTimeMillis()
    println(s"Starting... (on $startMillis)")



    val initialState: Vector[(UnblockMePiece, Location)] = Vector(
      UnblockMePieceWithLocation("G,4,4,2,H"),
      UnblockMePieceWithLocation("1,6,3,v"),
      UnblockMePieceWithLocation("1,3,3,h"),
      UnblockMePieceWithLocation("1,1,2,h"),
      UnblockMePieceWithLocation("2,6,2,h"),
      UnblockMePieceWithLocation("2,5,2,v"),
      UnblockMePieceWithLocation("3,5,2,v"),
      UnblockMePieceWithLocation("3,2,2,v"),
      UnblockMePieceWithLocation("4,3,2,v"),
      UnblockMePieceWithLocation("4,1,2,h"),
      UnblockMePieceWithLocation("5,6,2,v"),
      UnblockMePieceWithLocation("5,2,2,h"),
      UnblockMePieceWithLocation("6,6,3,v")
    )

    val solver = new UnblockMeSolver(initialState)

    println(solver.solution(Location(5,4)).take(1).toList)

    val endMillis = System.currentTimeMillis()

    val diff = endMillis.-(startMillis)
    val duration = diff.millis

    println(s"Finished... (on $startMillis)")

    println(s"Duration: ${duration.toSeconds}s")

    println("Sleeping for 5mins")
    Thread.sleep(5*60*1000)
  }
}