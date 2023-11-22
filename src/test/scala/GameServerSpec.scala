import NetGraphAlgebraDefs._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class GameServerSpec extends AnyWordSpecLike with Matchers with ScalatestRouteTest {

  val testRoutes: Route =
    path("get-node" / "thief") {
      get {
        complete("Thief node response")
      }
    } ~
      path("get-node" / "police") {
        get {
          complete("Police node response")
        }
      }

  "GameServer" should {
    def setupGameScenario(): (List[NodeObject], MutableValueGraph[NodeObject, Action]) = {
      val nodes = List(
        NodeObject(1, 0, 0, 0, 0, 0, 0, 0, 0),
        NodeObject(2, 0, 0, 0, 0, 0, 0, 0, 0),
        NodeObject(3, 0, 0, 0, 0, 0, 0, 0, 0)
      )
      val edges = List(
        Action(1, nodes(1), nodes(2), 1, 2, Some(10), 5.0)
      )

      val graph: MutableValueGraph[NodeObject, Action] = ValueGraphBuilder.directed().build()
      nodes.foreach(graph.addNode)
      edges.foreach { action =>
        val nodeFromOption: Option[NodeObject] = nodes.find(_.id == action.fromNode.id)
        val nodeToOption: Option[NodeObject] = nodes.find(_.id == action.toNode.id)

        nodeFromOption.flatMap { nodeFrom =>
          nodeToOption.map { nodeTo =>
            graph.putEdgeValue(nodeFrom, nodeTo, action)
          }
        }
      }

      (nodes, graph)
    }
    "initialize thief and police nodes correctly" in {
      Get("/get-node/thief") ~> testRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Thief node response"
      }

      Get("/get-node/police") ~> testRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Police node response"
      }
    }

    "simulate game completion correctly" in {
      val (nodes, graph) = setupGameScenario()
      val emptyGraph = GraphOperations.createGraph(List.empty[NodeObject], List.empty[Action])

      GameServer.startGameServer(nodes, graph,emptyGraph)
      GameServer.handleGameCompletion("Thief")

      GameServer.gameIsOver shouldBe true
      GameServer.currentThiefNode shouldBe None
      GameServer.currentPoliceNode shouldBe None
    }
    }
}
