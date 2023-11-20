//import NetGraphAlgebraDefs.{Action, NetGraphComponent, NodeObject}
//import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
//import akka.http.scaladsl.server.Directives._
//import akka.pattern.ask
//import akka.util.Timeout
//import com.google.common.graph.MutableValueGraph
//import spray.json.{DefaultJsonProtocol, RootJsonFormat}
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.duration._
//import scala.util.Random
//
//// Define case classes for messages and responses
//case class GetNode(nodeId: Int)
//
//case class GetNodeResponse(nodes: Set[String]) extends NetGraphComponent
//
//
//// Define a generic Entity Actor
//class EntityActor(nodes: List[NodeObject], graph: MutableValueGraph[NodeObject, Action]) extends Actor with ActorLogging {
//  def receive: Receive = {
//    case GetNode(nodeId) =>
//      println(nodeId)
//      val response = GraphOperations.processGraph(nodes, nodeId, graph)
//      log.info(s"Actor ${self.path.name} updated with response: $response")
//      sender() ! GetNodeResponse(response.map(_.toString).toSet)
//  }
//}
//
//object GameServer extends SprayJsonSupport with DefaultJsonProtocol {
//  // Enable JSON marshalling for the messages
//  implicit val getNodeResponseFormat: RootJsonFormat[GetNodeResponse] = jsonFormat1(GetNodeResponse)
//
//  def startGameServer(nodes: List[NodeObject], graph: MutableValueGraph[NodeObject, Action]): Unit = {
//    implicit val system: ActorSystem = ActorSystem("police-thief-game")
//    implicit val timeout: Timeout = Timeout(5.seconds) // Specify a timeout
//    var isThiefFirstRequest = true
//    var isPoliceFirstRequest = true
//    // Create actor instances for the thief and police
//    val thiefActor = system.actorOf(Props(new EntityActor(nodes, graph)), "thiefActor")
//    val policeActor = system.actorOf(Props(new EntityActor(nodes, graph)), "policeActor")
//
//    // Define the route
//    val route =
//      path("get-node" / "thief" / IntNumber) { nodeId =>
//        get {
//          // Forward the request to the ThiefActor and return the response
//          complete((thiefActor ? GetNode(nodeId)).mapTo[GetNodeResponse])
//        }
//      } ~
//        path("get-node" / "police" / IntNumber) { nodeId =>
//          get {
//            // Forward the request to the PoliceActor and return the response
//            complete((policeActor ? GetNode(nodeId)).mapTo[GetNodeResponse])
//          }
//        } ~
//        path("get-node" / "thief") {
//          get {
//            if (isThiefFirstRequest) {
//              val randomNodeId = Random.shuffle(nodes.map(_.id)).headOption.getOrElse(0)
//              isThiefFirstRequest = false
//              val response = s"Random node selected: $randomNodeId"
//              complete((thiefActor ? GetNode(randomNodeId)).mapTo[GetNodeResponse].map(_.copy(nodes = Set(response))))
//            } else {
//              complete("Cannot provide a random node ID for non-first requests.")
//            }
//          }
//        } ~
//        path("get-node" / "police") {
//          get {
//            if (isPoliceFirstRequest) {
//              val randomNodeId = Random.shuffle(nodes.map(_.id)).headOption.getOrElse(0)
//              isPoliceFirstRequest = false
//              val response = s"Random node selected: $randomNodeId"
//              complete((thiefActor ? GetNode(randomNodeId)).mapTo[GetNodeResponse].map(_.copy(nodes = Set(response))))
//            } else {
//              complete("Cannot provide a random node ID for non-first requests.")
//            }
//          }
//        }
//
//
//    // Start the server
//    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
//
//    println(s"Server online at http://localhost:8080/")
//  }
//}
