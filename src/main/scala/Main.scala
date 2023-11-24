//Main
import org.apache.logging.log4j.LogManager
object Main {
  private val logger = LogManager.getLogger(getClass.getName)
  def main(args: Array[String]): Unit = {
    val (perturbedNodes, perturbedEdges) = LoadGraph.load(args(0))
    logger.info(s"Perturbed Nodes: $perturbedNodes")
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



