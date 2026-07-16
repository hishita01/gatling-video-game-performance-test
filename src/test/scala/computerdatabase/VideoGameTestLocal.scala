package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration._
import scala.util.Random

class VideoGameTestLocal extends Simulation {

  /*
   * Generates a different numeric ID for every game.
   * This prevents database primary-key errors.
   */
  private val idCounter = new AtomicInteger(1000)

  /*
   * Custom feeder that creates fresh JSON data
   * for every virtual user.
   */
  private val gameFeeder: Iterator[Map[String, Any]] =
    Iterator.continually {

      val gameId = idCounter.incrementAndGet()
      val randomScore = Random.between(60, 101)

      Map(
        "gameId" -> gameId,
        "gameName" -> s"Gatling Test Game $gameId",
        "releaseDate" -> "2026-07-16",
        "reviewScore" -> randomScore,
        "category" -> "Adventure",
        "rating" -> "Mature"
      )
    }

  /*
   * One scenario containing both required actions:
   *
   * 1. Create a new game successfully on the local writable API.
   * 2. Attempt a delete on the public API and verify HTTP 403.
   */
  private val videoGameScenario =
    scenario("Create and Delete Video Game")
      .feed(gameFeeder)

      // Create a new game
      .exec(
        http("Create a new video game")
          .post("http://localhost:8080/app/videogames")
          .header("Accept", "application/json")
          .header("Content-Type", "application/json")
          .body(
            StringBody(
              """{
                |  "id": #{gameId},
                |  "name": "#{gameName}",
                |  "releaseDate": "#{releaseDate}",
                |  "reviewScore": #{reviewScore},
                |  "category": "#{category}",
                |  "rating": "#{rating}"
                |}""".stripMargin
            )
          )
          .asJson
          .check(status.is(200))
      )

      .pause(1.second)

      // Delete request must return 403 Forbidden
      .exec(
        http("Delete game and verify HTTP 403")
          .delete("https://www.videogamedb.uk/api/videogame/#{gameId}")
          .check(status.is(403))
      )

  /*
   * Runs once before the simulation starts.
   */
  before {
    println("======================================================")
    println("Starting the Video Game DB Gatling performance test.")
    println("The scenario will create a game and verify delete returns 403.")
    println("======================================================")
  }

  /*
   * Runs once after the simulation finishes.
   */
  after {
    println("======================================================")
    println("The Video Game DB Gatling performance test is complete.")
    println("Open the generated HTML report under target/gatling.")
    println("======================================================")
  }

  /*
   * Required load profiles:
   *
   * Phase 1: Ramp 5 users over 10 seconds.
   * Phase 2: Inject 1 user per second for 20 seconds.
   */
  setUp(
    videoGameScenario.inject(
      rampUsers(5).during(10.seconds),
      constantUsersPerSec(1).during(20.seconds)
    )
  )
}