//Main
import org.slf4j.LoggerFactory
object Main {
  private val logger = LoggerFactory.getLogger(getClass)
  def main(args: Array[String]): Unit = {
    val (perturbedNodes, perturbedEdges) = LoadGraph.load(args(0))
    logger.info(s"Perturbed Nodes: $perturbedNodes")
    // Create and populate the perturbed graph
    val PerturbedGraph = GraphOperations.createGraph(perturbedNodes, perturbedEdges)
    val (originalNodes, originalEdges) = LoadGraph.load(args(1))
    val originalGraph = GraphOperations.createGraph(originalNodes, originalEdges)
    // Start Akka HTTP server
    GameServer.startGameServer(perturbedNodes,PerturbedGraph,originalGraph)
  }
}



