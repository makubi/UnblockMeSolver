package akkaSolver

import org.scalatest.WordSpec
import scala.io.Source

class RushHourJamParser extends WordSpec {

  //http://www.cs.princeton.edu/courses/archive/fall04/cos402/assignments/rushhour/

  "The input of one jam" should {
    "be parsed correctly" in {
      val input = """Jam-1
                    |6
                    |1 2 h 2
                    |0 1 v 3
                    |0 0 h 2
                    |3 1 v 3
                    |2 5 h 3
                    |0 4 v 2
                    |4 4 h 2
                    |5 0 v 3""".stripMargin


    }
  }

  "The input of the file" should {
    "be parsed correctly" in {
      val source = Source.fromURL(getClass.getResource("/rushHourJams.txt")).mkString

      val jamStrings: Array[String] = source.split("[.]").filter(_.trim != "")

      assert (40 === jamStrings.length)
    }
  }



}
