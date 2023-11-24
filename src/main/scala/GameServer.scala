//GameServer.scala
import NetGraphAlgebraDefs.{Action, NodeObject}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.google.common.graph.MutableValueGraph
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import scala.util.Random



object GameServer extends SprayJsonSupport with DefaultJsonProtocol {
  private val logger = LoggerFactory.getLogger(getClass)
  // Enable JSON marshalling for the messages
  implicit val getNodeResponseFormat: RootJsonFormat[GetNodeResponse] = jsonFormat1(GetNodeResponse)
  var currentThiefNode: Option[NodeObject] = None
  var currentPoliceNode: Option[NodeObject] = None
  var gameIsOver: Boolean = false

  def startGameServer(nodes: List[NodeObject], perturbedGraph: MutableValueGraph[NodeObject, Action],originalGraph: MutableValueGraph[NodeObject, Action]): Unit = {
    implicit val system: ActorSystem = ActorSystem("police-thief-game")
    implicit val timeout: Timeout = Timeout(5.seconds)

    // Store the current nodes for thief and police


    // Create actor instances for the thief and police
    val thiefActor = system.actorOf(Props(new EntityActor(nodes, perturbedGraph, originalGraph)), "thiefActor")
    val policeActor = system.actorOf(Props(new EntityActor(nodes, perturbedGraph, originalGraph)), "policeActor")

    // Define the route
    val route =
      path("get-node" / "thief") {
        get {
          if(!gameIsOver) {
            currentThiefNode match {
              case Some(thiefNode) =>
                // continue the game
                complete((thiefActor ? GetNode(thiefNode.id)).mapTo[GetNodeResponse])
              case None =>
                // initialization logic
                val randomNodeId = Random.shuffle(nodes.map(_.id)).headOption.getOrElse(0)
                currentThiefNode = nodes.find(_.id == randomNodeId)
                complete(s"Random node assigned: ${currentThiefNode.map(_.id).getOrElse(0)}")
            }
          }else
            complete(s"Game is already over! ${if (currentThiefNode.isDefined) "Thief" else "Police"} wins! Please reset the game by sending a request to /reset/game!!")
        }
      } ~
      path("get-node" / "police") {
        get {
          if(!gameIsOver){
            currentPoliceNode match {
              case Some(policeNode) =>
                // continue the game
                complete((policeActor ? GetNode(policeNode.id)).mapTo[GetNodeResponse])
              case None =>
                // initialization logic
                val randomNodeId = Random.shuffle(nodes.map(_.id)).headOption.getOrElse(0)
                currentPoliceNode = nodes.find(_.id == randomNodeId)
                complete(s"Random node assigned: ${currentPoliceNode.map(_.id).getOrElse(0)}")
            }
          }else
            complete(s"Game is already over! ${if (currentPoliceNode.isDefined) "Police" else "Thief"} wins! Please reset the game by sending a request to /reset/game!!")
        }
      } ~
      path("reset" / "game") {
        get {
          if (gameIsOver) {
            resetGame()
            complete("Game has been reset both players will have to start again")
          } else
            complete(s"Game is not over!! Please play your next move!! ")
        }
      } ~
        path("autoClient") {
          get {
            val winner = AutomatedClient.play()
            complete(s"The winner is $winner !! Game has been reset!!")
          }
        }
    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)

    println(s"Server online at http://localhost:8080/")
    logger.info(s"Server online at http://localhost:8080/")
  }

  def handleGameCompletion(winner: String): Unit = {
    gameIsOver = true
    // Perform any additional actions upon game completion
    logger.info(s"Game over! $winner")
  }
  private def resetGame(): Unit = {
    gameIsOver = false
    currentThiefNode = None
    currentPoliceNode = None
    logger.info("Game has been reset. Both players will have to start again.")
  }
}
