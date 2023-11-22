// GameEntities.scala
import GameServer.handleGameCompletion
import NetGraphAlgebraDefs.{Action, NodeObject}
import akka.actor.{Actor, ActorLogging}
import com.google.common.graph.MutableValueGraph

case class GetNode(nodeId: Int)
case class GetNodeResponse(message: String)

class EntityActor(nodes: List[NodeObject], perturbedGraph: MutableValueGraph[NodeObject, Action], originalGraph: MutableValueGraph[NodeObject, Action]) extends Actor with ActorLogging {

  def receive: Receive = {
    case GetNode(nodeId) =>
      val response = GraphOperations.processGraph(nodes, nodeId,perturbedGraph,originalGraph)
      log.info(s"Actor ${self.path.name} updated with response: $response")
      if (self.path.name == "thiefActor") {
        if (response == -2) {
          sender() ! GetNodeResponse(s"Police wins! Thief is on node (${GameServer.currentThiefNode}) with no successors.")
          handleGameCompletion(s"Police wins! Thief is on node (${GameServer.currentThiefNode}) with no successors.")
          //          GameServer.currentPoliceNode = None
        } else if (response == -1) {
          sender() ! GetNodeResponse(s"Police wins! Thief made an illegal move! ${GameServer.currentThiefNode}")
          handleGameCompletion(s"Police wins! Thief made an illegal move! ${GameServer.currentThiefNode}")
          //          GameServer.currentPoliceNode = None
        } else {
          GameServer.currentThiefNode = nodes.find(_.id == response)
          GameServer.currentThiefNode match {
            case Some(node) =>
              if (GameServer.currentPoliceNode.contains(node)) {
                sender() ! GetNodeResponse(s"Police wins! Police Caught Thief on node $response")
                handleGameCompletion(s"Police wins! Police Caught Thief on node $response")
                //                GameServer.currentPoliceNode = None
              } else if (node.valuableData) {
                sender() ! GetNodeResponse(s"Thief wins! Thief at valuable Node $response")
                handleGameCompletion(s"Thief wins! Thief at valuable Node $response")
                GameServer.currentPoliceNode = None
              } else {
                sender() ! GetNodeResponse(s"Thief is at node $response")
              }
          }
        }

      } else if (self.path.name == "policeActor") {
        if (response == -2) {
          sender() ! GetNodeResponse(s"Thief wins! Police is on a node (${GameServer.currentPoliceNode}) with no successors.")
          handleGameCompletion(s"Thief wins! Police is on a node (${GameServer.currentPoliceNode}) with no successors.")
          //          GameServer.currentThiefNode = None
        } else if (response == -1) {
          sender() ! GetNodeResponse(s"Thief wins! Police made an illegal move! ${GameServer.currentPoliceNode}")
          handleGameCompletion(s"Thief wins! Police made an illegal move! ${GameServer.currentPoliceNode}")
          //          GameServer.currentThiefNode = None
        } else {
          GameServer.currentPoliceNode = nodes.find(_.id == response)
          GameServer.currentPoliceNode match {
            case Some(node) =>
              if (GameServer.currentThiefNode.contains(node)) {
                sender() ! GetNodeResponse(s"Police wins! Thief was caught at node $response")
                handleGameCompletion(s"Police wins! Thief was caught at node $response")
                GameServer.currentThiefNode = None
              } else {
                sender() ! GetNodeResponse(s"Police is at node $response")
              }
          }
        }
      }

  }
}
