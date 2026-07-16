package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class VideoGameTest extends Simulation {

  /*
   * Shared HTTP configuration for the public Video Game DB API.
   */
  private val httpProtocol = http
    .baseUrl("https://www.videogamedb.uk")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Video Game Performance Test")

  /*
   * Custom feeder.
   *
   * A new set of game data is generated whenever a virtual user
   * starts the create-game scenario.
   */
  private val gameFeeder: Iterator[Map[String, Any]] =
    Iterator.continually {
      val uniqueNumber =
        s"${System.currentTimeMillis()}-${Random.nextInt(100000)}"

      Map(
        "gameName" -> s"Gatling Test Game $uniqueNumber",
        "releaseDate" -> "2026-07-16",
        "reviewScore" -> Random.between(70, 101),
        "category" -> "Adventure",
        "rating" -> "Teen"
      )
    }

  /*
   * Scenario 1: Send a request to create a new game.
   *
   * The public API currently returns 403 for write operations.
   * Therefore, this test accepts 200, 201, or 403 so that the
   * simulation records the current public API behavior.
   */
  private val createGameScenario =
    scenario("Create New Video Game")
      .feed(gameFeeder)
      .exec(
        http("Create a new video game")
          .post("/api/videogame")
          .body(
            StringBody(
              """{
                |  "category": "#{category}",
                |  "name": "#{gameName}",
                |  "rating": "#{rating}",
                |  "releaseDate": "#{releaseDate}",
                |  "reviewScore": #{reviewScore}
                |}""".stripMargin
            )
          )
          .asJson
          .check(status.in(200, 201, 403))
          .check(status.saveAs("createStatus"))
          .check(bodyString.saveAs("createResponse"))
      )
      .exec { session =>
        val status =
          session("createStatus").asOption[Int].getOrElse(-1)

        val response =
          session("createResponse")
            .asOption[String]
            .getOrElse("No response body returned")

        println("--------------------------------------------------")
        println(s"Create-game response status: $status")
        println(s"Create-game response body: $response")
        println("--------------------------------------------------")

        session
      }

  /*
   * Scenario 2: Attempt to delete a game.
   *
   * The expected response is HTTP 403 Forbidden.
   */
  private val deleteGameScenario =
    scenario("Delete Video Game Without Authorization")
      .exec(
        http("Delete game and verify HTTP 403")
          .delete("/api/videogame/1")
          .check(status.is(403))
      )

  /*
   * Runs once before the test begins.
   */
  before {
    println("======================================================")
    println("Starting the Video Game DB Gatling performance test.")
    println("The test uses a custom feeder to generate game data.")
    println("It also verifies that deletion returns HTTP 403.")
    println("======================================================")
  }

  /*
   * Runs once after the test finishes.
   */
  after {
    println("======================================================")
    println("The Video Game DB Gatling performance test is complete.")
    println("Open the generated HTML report under target/gatling.")
    println("======================================================")
  }

  /*
   * Load profile:
   *
   * 1. Ramp 5 users over 10 seconds for game creation.
   * 2. Run 1 delete user per second for 20 seconds.
   */
  setUp(
    createGameScenario.inject(
      rampUsers(5).during(10.seconds)
    ),
    deleteGameScenario.inject(
      constantUsersPerSec(1).during(20.seconds)
    )
  ).protocols(httpProtocol)
}