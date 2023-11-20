//GraphOperations
//Graph Creation, calculation of successors,calculating simscore

import NetGraphAlgebraDefs.{Action, NodeObject}
import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}

import scala.jdk.CollectionConverters.CollectionHasAsScala

object GraphOperations {
  // Declare the graph as a global variable


  def createGraph(nodes: List[NodeObject], edges: List[Action]): MutableValueGraph[NodeObject, Action] = {
    val graph: MutableValueGraph[NodeObject, Action] = ValueGraphBuilder.directed().build()
    // Add nodes to the graph
    nodes.foreach(graph.addNode)
    println("nodes")
    println(graph.nodes())

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
//    edges.foreach { action =>
//      val fromNode: NodeObject = nodes.find(_.id == action.fromNode.id).getOrElse(NodeObject(0, 0, 0, 0, 0, 0, 0, 0, 0))
//      val toNode:NodeObject = nodes.find(_.id == action.fromNode.id).getOrElse(NodeObject(0, 0, 0, 0, 0, 0, 0, 0, 0))
//      graph.putEdgeValue(fromNode, toNode, action)
//    }
    println("edges")
    println(graph.nodes())
    graph
  }

  def processGraph(nodes: List[NodeObject], nodeId: Int, graph: MutableValueGraph[NodeObject, Action]): Int = {
    // Create a corresponding NodeObject instance
    val specificNode: NodeObject = nodes.find(_.id == nodeId).getOrElse {
      throw new NoSuchElementException(s"No node found with ID $nodeId")
    }

    // Get successor nodes
    val successors: Set[NodeObject] = graph.successors(specificNode).asScala.toSet

    // Get incident edges
//    val incidentEdges = graph.incidentEdges(specificNode).asScala.toSet
    if (successors.isEmpty)
      return -2 //no successors
    // Print the results (optional)
    println(s"Successor nodes of node with ID $nodeId: $successors")
//    println(s"Incident edges of node with ID $nodeId: $incidentEdges")
    val allIds = successors.map(_.id).toList
//    println("all")
//    println(allIds)
    val jaccardIndices: Map[Int, Double] = allIds.map { id =>
      val jaccardIndex = calculateConfidenceScore(graph, id)
//      println(s"Jaccard Index for node $id: $jaccardIndex")
      id -> jaccardIndex
    }.toMap
//    println("all jaccard")
//    println(jaccardIndices)
    val bestSuccessor = jaccardIndices.toSeq.sortBy(-_._2).toMap
    println("successors in descending")
    println(bestSuccessor)
    println("value")
    println(bestSuccessor.values.head)
    if (bestSuccessor.values.head == -1)
      {
        -1
      }
    else
      bestSuccessor.keySet.head
  }


  def calculateConfidenceScore(graph1: MutableValueGraph[NodeObject, Action], nodeId: Int): Double = {
    val (originalNodes, originalEdges) = LoadGraph.load("https://kirtan441.s3.amazonaws.com/NetGraph_13-11-23-18-46-59.ngs")
    val originalGraph = GraphOperations.createGraph(originalNodes, originalEdges)
    val nodeInGraph1: NodeObject = graph1.nodes().asScala.find(_.id == nodeId).getOrElse {
      throw new NoSuchElementException(s"No node found with ID $nodeId in Graph 1")
    }

    val nodeInGraph2: NodeObject = originalGraph.nodes().asScala.find(_.id == nodeId).getOrElse {
      NodeObject(-1, -1, -1, -1, -1, -1, -1, -1, -1)
    }

    if (nodeInGraph2 == NodeObject(-1, -1, -1, -1, -1, -1, -1, -1, -1)) {
      // Stop the process and return -1
      -1
    }
    else {
      val attributes = Seq(
        (nodeInGraph1.children, nodeInGraph2.children),
        (nodeInGraph1.props, nodeInGraph2.props),
        (nodeInGraph1.currentDepth, nodeInGraph2.currentDepth),
        (nodeInGraph1.propValueRange, nodeInGraph2.propValueRange),
        (nodeInGraph1.maxDepth, nodeInGraph2.maxDepth),
        (nodeInGraph1.maxBranchingFactor, nodeInGraph2.maxBranchingFactor),
        (nodeInGraph1.maxProperties, nodeInGraph2.maxProperties),
        (nodeInGraph1.storedValue, nodeInGraph2.storedValue),
        (nodeInGraph1.valuableData, nodeInGraph2.valuableData)
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

