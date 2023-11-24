import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import org.apache.logging.log4j.LogManager
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import spray.json._

case class GetResponse(message: String)

trait JsonSupport extends DefaultJsonProtocol {
  implicit val getNodeResponseFormat: RootJsonFormat[GetResponse] = jsonFormat1(GetResponse)
}

object AutomatedClient extends JsonSupport {
  private val logger = LogManager.getLogger(getClass.getName)
  def play(): String = {
    implicit val system: ActorSystem = ActorSystem("AutomatedClient")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    // Helper method to send GET requests to the server
    def sendGetRequest(endpoint: String): Future[GetResponse] = {
      val request = HttpRequest(uri = endpoint)
      val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
      responseFuture.flatMap { response =>
        response.status match {
          case StatusCodes.OK =>
            Unmarshal(response.entity).to[String].map { entityString =>
              if (entityString.startsWith("Random node assigned")) {
                val message = entityString.substring(entityString.indexOf(":") + 2)
                GetResponse(message)
              } else {
                val parsedJson = JsonParser(entityString)
                parsedJson.convertTo[GetResponse]
              }
            }
          case _ =>
            Future.successful(GetResponse("Error occurred"))
        }
      }
    }

    // Play the game until a win condition is reached
    def playGame(): String = {
      val thiefEndpoint = "http://0.0.0.0:8080/get-node/thief"
      val policeEndpoint = "http://0.0.0.0:8080/get-node/police"
      val resetGameEndpoint = "http://0.0.0.0:8080/reset/game"

      var gameIsOver = false
      var isThiefTurn = true // Start with thief's turn

      while (!gameIsOver) {
        val responseFuture = if (isThiefTurn) sendGetRequest(thiefEndpoint) else sendGetRequest(policeEndpoint)
        val response = Await.result(responseFuture, 5.seconds)

        logger.info(s"${if (isThiefTurn) "Thief" else "Police"}: ${response.message}")

        if (response.message.startsWith("Thief wins!") || response.message.startsWith("Police wins!") || response.message.startsWith("Game is already over! Thief wins!")) {
          gameIsOver = true
          val winner = if (response.message.startsWith("Thief wins!") || response.message.startsWith("Game is already over! Thief wins!")) "Thief" else "Police"
          println(s"$winner wins!")
          // Reset the game
          val resetResponse = Await.result(sendResetRequest(resetGameEndpoint), 5.seconds)
          if (resetResponse == Done) {
            println("Game being reset!!")
            isThiefTurn = true // Start with thief's turn again
          } else {
            println("Failed to reset the game. Exiting...")
            return "Error: Game Reset Failed"
          }
          return winner
        }

        isThiefTurn = !isThiefTurn // Switch turns
      }
      "No winner"
    }

    def sendResetRequest(endpoint: String): Future[Done] = {
      val request = HttpRequest(uri = endpoint)
      val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
      responseFuture.flatMap { response =>
        response.status match {
          case StatusCodes.OK =>
            Future.successful(Done)
          case _ =>
            logger.error("Reset request failed")
            Future.failed(new Exception("Reset request failed"))
        }
      }
    }

    // Play the game twice

      val winner = playGame()

    // Terminate the ActorSystem
    system.terminate()
    winner
  }
}
