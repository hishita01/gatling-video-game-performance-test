# Video Game Performance Testing using Gatling

## Project Overview
This project was developed using Gatling, Scala, and Maven to perform performance testing on the Video Game Database API.

The assignment requirements were:

- Create a new game
- Delete a game and verify HTTP 403 response
- Use custom feeders to generate test data
- Implement load simulations
- Add before and after hooks
- Generate performance reports

---

## Files Included

### VideoGameTest.scala
This implementation uses the public Video Game DB API. The API blocks write operations, so creating a game returns HTTP 403. This implementation demonstrates the behavior of the public API.

### VideoGameTestLocal.scala
This implementation uses a locally hosted VideoGameDB application. It successfully creates new games and verifies that delete requests return HTTP 403.

---

## Features Implemented

- Create a new game using a POST request.
- Delete a game and verify HTTP 403.
- Custom feeder for generating dynamic game data.
- Ramp user load simulation.
- Fixed duration load simulation.
- Before and after hooks for custom messages.
- HTML report generation.

---

## Custom Feeder

A custom feeder was created to generate:

- Unique Game ID
- Game Name
- Release Date
- Review Score
- Category
- Rating

This ensures that every request uses different data.

---

## Load Simulation

### Ramp Users
```scala
rampUsers(5).during(10.seconds)
```

Gradually increases users over 10 seconds.

### Fixed Duration Load
```scala
constantUsersPerSec(1).during(20.seconds)
```

Runs one user per second for 20 seconds.

---

# Challenges Faced and Solutions

## 1. Scala class option was not visible in IntelliJ.

### Solution
- Installed the Scala plugin.
- Reloaded the Maven project.
- Added the Scala SDK.

---

## 2. GitHub permission error (403).

### Solution
- Updated Git credentials.
- Re-authenticated GitHub.
- Fixed repository permissions.

---

## 3. Public API returned HTTP 403 when creating a game.

### Solution
The public Video Game DB API does not allow anonymous users to create games.

To satisfy the assignment requirement, I downloaded and ran the open-source VideoGameDB application locally and used:

```
http://localhost:8080
```

for successful game creation.

---

## 4. Local application returned HTTP 500 errors.

### Cause
Multiple requests were inserting games with the same ID.

### Solution
Implemented a custom feeder that generates unique game IDs for every request.

---

## 5. Gradle project failed to start.

### Cause
The project was incompatible with Java 25.

### Solution
Installed Java 11 and ran the application using Java 11.

---

# How to Run the Project

## Start the Local Video Game DB Application

```bash
cd ~/Desktop/VideoGameDB
./gradlew bootRun
```

---

## Run Public API Simulation

```bash
./mvnw gatling:test \
-Dgatling.simulationClass=computerdatabase.VideoGameTest
```

---

## Run Local API Simulation

```bash
./mvnw gatling:test \
-Dgatling.simulationClass=computerdatabase.VideoGameTestLocal
```

---

# Performance Report

After execution, the Gatling report is generated under:

```
target/gatling/
```

Open the generated `index.html` file in a browser to view:

- Request count
- Response times
- Percentiles
- Success and failure statistics
- Graphs and charts

---

# Conclusion

This project helped me understand:

- Performance testing using Gatling.
- API testing using HTTP requests.
- Dynamic data generation using feeders.
- Load simulation techniques.
- Debugging real-world API and environment issues.
- Generating and analyzing performance reports.

Both implementations were successfully executed and generated performance reports with zero failures.