# VideoGameTest - Gatling Performance Test

## Prerequisites
- Java 11 or later
- Maven
- IntelliJ IDEA
- Scala Plugin

## Project Structure
- VideoGameTest.scala
- Custom feeder for generating game data
- Gatling HTML reports

## How to Run

Open terminal and execute:

```bash
./mvnw clean gatling:test \
-Dgatling.simulationClass=computerdatabase.VideoGameTest
```

## Test Scenarios

1. Create a new video game using dynamically generated JSON data.
2. Delete a video game and verify HTTP 403 response.
3. Execute ramp user load simulation.
4. Execute fixed duration load simulation.

## Load Profile

- Ramp 5 users during 10 seconds.
- Execute 1 user per second for 20 seconds.

## Reports

Generated reports are located under:

```
target/gatling
```

Open the HTML report:

```bash
open target/gatling/<report-folder>/index.html
```

## Note

The public Video Game DB currently returns HTTP 403 for anonymous create requests. The simulation still demonstrates POST implementation, custom feeders, delete validation, and load profile configuration.