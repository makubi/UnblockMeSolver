package akkaSolver

import org.scalatest.WordSpec
import akkaSolver.actors.Solver.{StateOrderingForPriorityQueue, State}

class StateOrderingForPriorityQueueTest extends WordSpec {

  "A list of states" should {
    "be ordered correctly based on their f-values" in {

      val state111: State = State("111", 0, 111)
      val state110: State = State("110", 0, 110)

      val list: List[State] = List(state111, state110)

      val sorted: List[State] = list.sorted(StateOrderingForPriorityQueue)
      val expected = List(state111, state110)
      assert(sorted === expected)
    }
  }
}
