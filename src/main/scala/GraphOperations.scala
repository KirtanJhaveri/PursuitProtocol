//GraphOperations
//Graph Creation, calculation of successors,calculating simscore

import NetGraphAlgebraDefs.{Action, NodeObject}
import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}
import org.slf4j.LoggerFactory
import scala.jdk.CollectionConverters.CollectionHasAsScala

object GraphOperations {
  // Declare the graph as a global variable
  private val logger = LoggerFactory.getLogger(getClass)
  def createGraph(nodes: List[NodeObject], edges: List[Action]): MutableValueGraph[NodeObject, Action] = {
    val graph: MutableValueGraph[NodeObject, Action] = ValueGraphBuilder.directed().build()
    // Add nodes to the graph
    nodes.foreach(graph.addNode)
    println("nodes")
    println(graph.nodes())
    logger.info("nodes")
    logger.info(graph.nodes().toString)

    // Add edges to the graph with additional properties
    edges.foreach { action =>
      val nodeFromOption: Option[NodeObject] = nodes.find(_.id == action.fromNode.id)
      val nodeToOption: Option[NodeObject] = nodes.find(_.id == action.toNode.id)

      nodeFromOption.flatMap { nodeFrom =>
        nodeToOption.map { nodeTo =>
          graph.putEdgeValue(nodeFrom, nodeTo, action)
        }
      }
    }
    logger.info("edges")
    logger.info(graph.nodes().toString)
    graph
  }

  def processGraph(nodes: List[NodeObject], nodeId: Int, perturbedGraph: MutableValueGraph[NodeObject, Action], originalGraph: MutableValueGraph[NodeObject, Action]): Int = {
    // Create a corresponding NodeObject instance
    val specificNode: NodeObject = nodes.find(_.id == nodeId).getOrElse {
      throw new NoSuchElementException(s"No node found with ID $nodeId")
    }

    // Get successor nodes
    val successors: Set[NodeObject] = perturbedGraph.successors(specificNode).asScala.toSet

    // Get incident edges
//    val incidentEdges = graph.incidentEdges(specificNode).asScala.toSet
    if (successors.isEmpty)
      return -2 //no successors
    // Print the results (optional)
    logger.debug(s"Successor nodes of node with ID $nodeId: $successors")
//    println(s"Incident edges of node with ID $nodeId: $incidentEdges")
    val allIds = successors.map(_.id).toList
//    println("all")
//    println(allIds)
    val jaccardIndices: Map[Int, Double] = allIds.map { id =>
      val jaccardIndex = calculateConfidenceScore(perturbedGraph,originalGraph, id)
//      println(s"Jaccard Index for node $id: $jaccardIndex")
      id -> jaccardIndex
    }.toMap
//    println("all jaccard")
//    println(jaccardIndices)
    val bestSuccessor = jaccardIndices.toSeq.sortBy(-_._2).toMap
//    println("successors in descending")
//    println(bestSuccessor)
//    println("value")
//    println(bestSuccessor.values.head)
    if (bestSuccessor.values.head == -1)
      {
        -1
      }
    else
      bestSuccessor.keySet.head
  }


  def calculateConfidenceScore(perturbedGraph: MutableValueGraph[NodeObject, Action],originalGraph: MutableValueGraph[NodeObject, Action], nodeId: Int): Double = {

    val nodeInPerturbed: NodeObject = perturbedGraph.nodes().asScala.find(_.id == nodeId).getOrElse {
      throw new NoSuchElementException(s"No node found with ID $nodeId in Graph 1")
    }

    val nodeInOriginal: NodeObject = originalGraph.nodes().asScala.find(_.id == nodeId).getOrElse {
      NodeObject(-1, -1, -1, -1, -1, -1, -1, -1, -1)
    }

    if (nodeInOriginal == NodeObject(-1, -1, -1, -1, -1, -1, -1, -1, -1)) {
      // Stop the process and return -1
      -1
    }
    else {
      val attributes = Seq(
        (nodeInPerturbed.children, nodeInOriginal.children),
        (nodeInPerturbed.props, nodeInOriginal.props),
        (nodeInPerturbed.currentDepth, nodeInOriginal.currentDepth),
        (nodeInPerturbed.propValueRange, nodeInOriginal.propValueRange),
        (nodeInPerturbed.maxDepth, nodeInOriginal.maxDepth),
        (nodeInPerturbed.maxBranchingFactor, nodeInOriginal.maxBranchingFactor),
        (nodeInPerturbed.maxProperties, nodeInOriginal.maxProperties),
        (nodeInPerturbed.storedValue, nodeInOriginal.storedValue),
        (nodeInPerturbed.valuableData, nodeInOriginal.valuableData)
      )

      val jaccardIndices = attributes.map { case (attr1, attr2) =>
        val intersection = Set(attr1).intersect(Set(attr2))
        val union = Set(attr1).union(Set(attr2))
        val jaccardIndex = jaccardSimilarity(intersection, union)

        jaccardIndex
      }

      // Calculate the average Jaccard index for all attributes
      val averageJaccardIndex = jaccardIndices.sum / jaccardIndices.size.toDouble

      averageJaccardIndex
    }
  }

  private def jaccardSimilarity[T](set1: Set[T], set2: Set[T]): Double = {
    val intersectionSize = set1.intersect(set2).size.toDouble
    val unionSize = set1.union(set2).size.toDouble
    if (unionSize == 0) 0.0 else intersectionSize / unionSize
  }

}

// Print the results
//    println(s"Successor nodes of node with ID $specificNodeId: $successors")
//    println(s"Incident edges of node with ID $specificNodeId: $incidentEdges")
//
//    val incidentEdge = graph.incidentEdges(specificNode).asScala.map { edge =>
//      val successor = if (edge.source() == specificNode) edge.target() else edge.source()
//      (graph.edgeValue(edge.source(), edge.target()), successor)
//    }.toSet
//
//    println(incidentEdge)

