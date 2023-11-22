//Main

object Main {
  def main(args: Array[String]): Unit = {
    val (perturbedNodes, perturbedEdges) = LoadGraph.load(args(0))
    // Create and populate the perturbed graph
    val PerturbedGraph = GraphOperations.createGraph(perturbedNodes, perturbedEdges)
    val (originalNodes, originalEdges) = LoadGraph.load(args(1))
    val originalGraph = GraphOperations.createGraph(originalNodes, originalEdges)
//    println(perturbedNodes)

////    val successors = GraphOperations.processGraph(nodes, nodeId)
////    val nodeIdToCompare = 11
//    val nodeInGraph1: NodeObject = graph.nodes().asScala.find(_.id == nodeId).getOrElse {
//      throw new NoSuchElementException(s"No node found with ID $nodeId in Graph 1")
//    }
//
//    val nodeInGraph2: NodeObject = graph2.nodes().asScala.find(_.id == nodeId).getOrElse {
//      throw new NoSuchElementException(s"No node found with ID $nodeId in Graph 2")
//    }

//    val nodeId = 11
//    val confidenceScore = GraphOperations.calculateConfidenceScore(PerturbedGraph, nodeId)
//    println(s"Confidence Score: $confidenceScore")

    // Start Akka HTTP server
    GameServer.startGameServer(perturbedNodes,PerturbedGraph,originalGraph)
  }
}



//  import NetGraphAlgebraDefs.{Action, NodeObject}
//  import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}
//  import scala.jdk.CollectionConverters.CollectionHasAsScala
//
//  object Main {
//    def main(args: Array[String]): Unit = {
//      val (nodes, edges) = LoadGraph.load("C:\\Users\\kirta\\Desktop\\load\\src\\main\\resources\\10_nodes.ngs")
//      println(nodes)
//      println(edges)
//      // Create a MutableValueGraph using Guava's ValueGraphBuilder
//      val graph: MutableValueGraph[NodeObject, Action] = ValueGraphBuilder.directed().build()
//
//      // Add nodes to the graph
//      nodes.foreach(graph.addNode)
//
//      // Add edges to the graph with additional properties
//      edges.foreach { action =>
//        graph.putEdgeValue(action.fromNode, action.toNode, action)
//      }
//
//
//      val specificNodeId: Int = 0
//
//      // Create a corresponding NodeObject instance
//      val specificNode: NodeObject = nodes.find(_.id == specificNodeId).getOrElse {
//        throw new NoSuchElementException(s"No node found with ID $specificNodeId")
//      }
//
//      // Get successor nodes
//      val successors: Set[NodeObject] = graph.successors(specificNode).asScala.toSet
//
//      // Get incident edges
//      val incidentEdges = graph.incidentEdges(specificNode).asScala.toSet
//      // Print the results
//      println(s"Successor nodes of node with ID $specificNodeId: $successors")
//      println(s"Incident edges of node with ID $specificNodeId: $incidentEdges")
//
//      val incidentEdge = graph.incidentEdges(specificNode).asScala.map { edge =>
//        val successor = if (edge.source() == specificNode) edge.target() else edge.source()
//        (graph.edgeValue(edge.source(), edge.target()), successor)
//      }.toSet
//
//      println(incidentEdge)
//
//      import akka.actor.ActorSystem
//      import akka.http.scaladsl.Http
//      import akka.http.scaladsl.server.Directives._
//      import akka.stream.ActorMaterializer
//      implicit val system: ActorSystem = ActorSystem("police-thief-game")
//      implicit val materializer: ActorMaterializer = ActorMaterializer()
//
//      // Your existing code for the graph and GetSuccessor
//
//      // Define the route
//      val route =
//        path("get-node" / IntNumber) { nodeId =>
//          get {
//            // Retrieve NodeObject for the specified nodeId
//            complete {
//              val nodeOption = nodes.find(_.id == nodeId)
//              nodeOption match {
//                case Some(node) => node.toString // or any response you want
//                case None => "Node not found"
//              }
//            }
//          }
//        }
//
//      // Start the server
//      val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
//
//      println(s"Server online at http://localhost:8080/")
//    }
//
//
//  }
//
//
