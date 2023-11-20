import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import NetGraphAlgebraDefs._
import org.scalatest.BeforeAndAfterAll

class GraphOperationsSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  val node1 = NodeObject(1, 0, 0, 0, 0, 0, 0, 0, 0)
  val node2 = NodeObject(2, 0, 0, 0, 0, 0, 0, 0, 0)
  val nodes = List(node1, node2)

  val action = Action(1, node1, node2, 1, 2, Some(10), 5.0)
  val edges = List(action)

  "createGraph" should "correctly create an empty graph when provided with no nodes or edges" in {
    val emptyGraph = GraphOperations.createGraph(List.empty[NodeObject], List.empty[Action])
    emptyGraph.nodes().isEmpty shouldBe true
    emptyGraph.edges().isEmpty shouldBe true
  }

  it should "properly create a graph with given nodes and edges" in {
    val graph = GraphOperations.createGraph(nodes, edges)
    graph.nodes().size() shouldBe 2
    graph.edges().size() shouldBe 1
  }

  "processGraph" should "return the correct successor node ID for a given node" in {
    val graph = GraphOperations.createGraph(nodes, edges)
    val successorNodeId = GraphOperations.processGraph(nodes, 1, graph)
    successorNodeId shouldBe 2
  }
}
