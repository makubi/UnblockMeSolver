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