# 🏆 SportTracker Backend API

Welcome to the **SportTracker Backend** repository. This is a high-performance Spring Boot API that powers the tournament management, live match tracking, and quest boards for Esports, Football, and World Cup leagues. It is fully integrated with MongoDB and provides robust JWT and Session-based security.

---

## 🚀 Tech Stack & Prerequisites
* **Java:** version 21+ (Compatible with JDK 25)
* **Framework:** Spring Boot 3.5.15
* **Database:** MongoDB (using Spring Data MongoDB)
* **Build Tool:** Maven 3.9+
* **Session Store:** MongoDB-backed HTTP Sessions
* **Dependencies:** Lombok, Spring Security, JSON Web Token (JWT), OpenCSV, Springdoc OpenAPI/Swagger UI

---

## ⚙️ Configuration & Settings (`application.properties`)

The application is configured via the `src/main/resources/application.properties` file. Below are the key environment configurations you need to set:

```properties
spring.application.name=SportsTracker
server.port=8080

# 🍃 MongoDB Connection
# Set your MongoDB Atlas URI or local connection string
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/?appName=Cluster0
spring.data.mongodb.database=sport_tracker_db

# 🛡️ Spring Session (MongoDB Backed)
spring.session.store-type=mongodb
spring.session.mongodb.collection-name=sessions
spring.session.timeout=3600

# 📧 Mail Server Configuration (SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# 🔑 JWT Security Configuration
jwt.secret=yourSuperSecretKeyThatIsLongEnoughForHS256AlgorithmUsually256Bits32CharsOrMore
jwt.expiration=86400000

# ⚽ Football API Configuration
football.api.token=your-football-api-org-token
app.frontend.url=http://localhost:5173
```

---

## 📦 Build & Running Guide

Follow these steps to compile, package, and run the backend server.

### 1. Compile the Project
Since this project compiles under modern Java versions (including JDK 25), annotation processing is restricted/disabled from the classpath by default. The `maven-compiler-plugin` has been updated with explicit Lombok processing paths. To compile the project:
```bash
# Using Maven Wrapper (Windows PowerShell)
.\mvnw.cmd clean compile

# Using Maven Wrapper (Bash/Linux/macOS)
./mvnw clean compile
```

### 2. Package the Application (Create JAR)
To package the Spring Boot application into a single executable JAR file (skipping test suites for quick building):
```bash
.\mvnw.cmd clean package -DskipTests
```
This generates the packaged JAR in the `target/` directory:
`target/SportsTracker-0.0.1-SNAPSHOT.jar`

### 3. Run the JAR Directly
To run the server in production/standalone mode:
```bash
java -jar target/SportsTracker-0.0.1-SNAPSHOT.jar
```

### 4. Direct Maven Dev Mode Run
Alternatively, you can boot the server in developer mode with live-reload:
```bash
.\mvnw.cmd spring-boot:run
```

Once started, Tomcat will launch on **port `8080`**.
* **Swagger API Documentation:** `http://localhost:8080/swagger-ui/index.html`
* **Raw OpenAPI JSON Specs:** `http://localhost:8080/v3/api-docs`

---

## 🛡️ Security & Role-Based Access Control
The application supports three roles:
* `ROLE_USER`: Standard registered user. Can view data, claim/submit quests, and register their fan profile.
* `ROLE_ADMIN`: Full administrative access. Can create/sync leagues, manage fixtures, and configure tournaments.
* `ROLE_GUILD_MASTER`: Oversees the quest system. Can approve or reject quest submissions.

Security is applied in a hybrid model:
1. **JWT Auth** via the `quest_token` header/cookie.
2. **Session-based Auth** (stored in MongoDB) for standard endpoints.

---

## 📖 Step-by-Step API Documentation

Below is a detailed guide for each API endpoint grouped by module.

### 1. Authentication & Profile (`/api/auth`)

#### 🔹 User Sign Up
Registers a new account.
* **Endpoint:** `POST /api/auth/signup`
* **Access:** Anonymous (Public)
* **Request Body (`SignupRequest`):**
  ```json
  {
    "username": "johndoe",
    "email": "johndoe@example.com",
    "password": "SecretPassword123"
  }
  ```
* **Response (201 Created):**
  ```json
  {
    "id": "60d5ec493b8c6a2b84e1b74a",
    "username": "johndoe",
    "email": "johndoe@example.com",
    "roles": ["ROLE_USER"]
  }
  ```

#### 🔹 User Sign In
Authenticates and initializes a session.
* **Endpoint:** `POST /api/auth/signin`
* **Access:** Anonymous (Public)
* **Request Body (`SigninRequest`):**
  ```json
  {
    "email": "johndoe@example.com",
    "password": "SecretPassword123"
  }
  ```
* **Response (200 OK):**
  ```json
  {
    "id": "60d5ec493b8c6a2b84e1b74a",
    "username": "johndoe",
    "email": "johndoe@example.com",
    "roles": ["ROLE_USER"]
  }
  ```

#### 🔹 User Sign Out
Invalidates the current session.
* **Endpoint:** `POST /api/auth/signout`
* **Access:** Public (Clears current session)
* **Response (200 OK):** Empty body

#### 🔹 Get Current User Details
Retrieves details of the logged-in user.
* **Endpoint:** `GET /api/auth/me`
* **Access:** Authenticated Users
* **Response (200 OK):**
  ```json
  {
    "id": "60d5ec493b8c6a2b84e1b74a",
    "username": "johndoe",
    "email": "johndoe@example.com",
    "roles": ["ROLE_USER"]
  }
  ```

#### 🔹 Update User Profile
Updates the username or email of the current logged-in user.
* **Endpoint:** `PUT /api/auth/me`
* **Access:** Authenticated Users
* **Request Body:**
  ```json
  {
    "username": "john_new_name",
    "email": "johnnew@example.com"
  }
  ```
* **Response (200 OK):** Updated user details

#### 🔹 Forgot Password
Sends a password reset token to the specified email.
* **Endpoint:** `POST /api/auth/forgot-password`
* **Access:** Anonymous (Public)
* **Request Body:**
  ```json
  {
    "email": "johndoe@example.com"
  }
  ```
* **Response (200 OK):**
  ```text
  "If that email exists, a reset link has been sent."
  ```

#### 🔹 Reset Password
Resets the password using a valid email token.
* **Endpoint:** `POST /api/auth/reset-password`
* **Access:** Anonymous (Public)
* **Request Body:**
  ```json
  {
    "token": "4a822b31-e127-4630-9b34-d3deab3db21a",
    "password": "MyNewPassword123"
  }
  ```
* **Response (200 OK):**
  ```text
  "Password reset successful!"
  ```

---

### 2. Quest Board Module (`/api/quests`)

#### 🔹 Get All Quests
* **Endpoint:** `GET /api/quests`
* **Access:** Public
* **Response (200 OK):** Array of Quest objects

#### 🔹 Create a Quest
* **Endpoint:** `POST /api/quests`
* **Access:** Authenticated (Guild Master / Admin)
* **Request Body (`Quest`):**
  ```json
  {
    "title": "Predict World Cup Winner",
    "description": "Submit your prediction for the championship team.",
    "serviceType": "SPORT",
    "points": 150
  }
  ```
* **Response (200 OK):** Created Quest object

#### 🔹 Delete a Quest
* **Endpoint:** `DELETE /api/quests/{id}`
* **Access:** Authenticated (Guild Master / Admin)

#### 🔹 Get Quests by Service Type
Filter quests by ESPORT or SPORT.
* **Endpoint:** `GET /api/quests/service/{serviceType}`
* **Path Variables:** `serviceType` (either `ESPORT` or `SPORT`)
* **Access:** Public

#### 🔹 Claim a Quest
Standard user claims a quest to start it.
* **Endpoint:** `POST /api/quests/{id}/claim`
* **Access:** Authenticated User
* **Response (200 OK):** `QuestSubmission` object with status `CLAIMED`.

#### 🔹 Submit Quest Proof
Submits completed proof for a claimed quest.
* **Endpoint:** `POST /api/quests/{id}/submit`
* **Access:** Authenticated User
* **Response (200 OK):** `QuestSubmission` object with status `SUBMITTED`.

#### 🔹 Get Pending Quest Submissions
* **Endpoint:** `GET /api/quests/submissions/pending`
* **Access:** Authenticated (Guild Master / Admin)
* **Response (200 OK):** Array of pending `QuestSubmission` objects.

#### 🔹 Approve/Reject Quest Submission
Approves a submission and awards points to the user.
* **Endpoint:** `PUT /api/quests/submissions/{submissionId}/approve`
* **Access:** Authenticated (Guild Master / Admin)
* **Response (200 OK):** Approved `QuestSubmission` with status `APPROVED`.

#### 🔹 Get My Progress
* **Endpoint:** `GET /api/quests/my-progress`
* **Access:** Authenticated User
* **Response (200 OK):** Map of claimed quests and current points.

#### 🔹 Leaderboard
* **Endpoint:** `GET /api/quests/leaderboard`
* **Access:** Public
* **Response (200 OK):** Ranked list of users and their total points.

---

### 3. Esports Tournaments Module (`/api/tournaments`, `/api/teams`, `/api/players`, `/api/matches`)

#### 🔹 Create Tournament
* **Endpoint:** `POST /api/tournaments`
* **Access:** Authenticated (Admin)
* **Request Body (`TournamentRequest`):**
  ```json
  {
    "name": "Valorant Champions 2026",
    "game": "Valorant",
    "format": "SINGLE_ELIMINATION",
    "status": "UPCOMING",
    "startDate": "2026-08-01T12:00:00Z",
    "endDate": "2026-08-15T18:00:00Z",
    "maxTeams": 16
  }
  ```

#### 🔹 Get All Tournaments
* **Endpoint:** `GET /api/tournaments`
* **Query Params (Optional):**
  * `status`: `UPCOMING`, `ONGOING`, `COMPLETED`
  * `game`: (e.g. `Valorant`)
  * `page`: page index (default: `0`)
  * `size`: list size (default: `10`)
* **Response (200 OK):** Paginated `PageTournament` object.

#### 🔹 Search Tournaments
* **Endpoint:** `GET /api/tournaments/search`
* **Query Params:** `q` (e.g. `Champions`)
* **Response (200 OK):** Array of matching Tournaments.

#### 🔹 Create Team
* **Endpoint:** `POST /api/teams`
* **Access:** Authenticated User
* **Request Body (`TeamRequest`):**
  ```json
  {
    "name": "Sentinels",
    "tournamentId": "60d5ec493b8c6a2b84e1b74f",
    "logoUrl": "http://example.com/logo.png"
  }
  ```

#### 🔹 Search Teams
* **Endpoint:** `GET /api/teams/search`
* **Query Params:** `q` (team name query)

#### 🔹 Register/Create Player
* **Endpoint:** `POST /api/players`
* **Access:** Authenticated User
* **Request Body (`PlayerRequest`):**
  ```json
  {
    "username": "TenZ",
    "teamId": "60d5ec493b8c6a2b84e1b75a",
    "role": "Duelist"
  }
  ```

#### 🔹 Create Match
* **Endpoint:** `POST /api/matches`
* **Access:** Authenticated (Admin)
* **Request Body (`Match`):**
  ```json
  {
    "tournamentId": "60d5ec493b8c6a2b84e1b74f",
    "teamAId": "teamAId123",
    "teamBId": "teamBId456",
    "status": "SCHEDULED",
    "scheduledAt": "2026-08-02T14:00:00Z"
  }
  ```

#### 🔹 Update Match Score
* **Endpoint:** `PUT /api/matches/{id}/score`
* **Access:** Authenticated (Admin)
* **Request Body:**
  ```json
  {
    "scoreA": 13,
    "scoreB": 9
  }
  ```

#### 🔹 Get Standings
* **Endpoint:** `GET /api/tournaments/{tournamentId}/standings`
* **Response (200 OK):** Ranked Team win-loss statistics.

#### 🔹 Get Tournament Bracket
Generates or fetches bracket matches.
* **Endpoint:** `GET /api/tournaments/{tournamentId}/bracket`

---

### 4. Football & World Cup Module (`/api/football`)

#### 🔹 Sync Football League Data
Syncs teams and stats from the external api.
* **Endpoint:** `POST /api/football/leagues/sync/{code}`
* **Access:** Authenticated (Admin)

#### 🔹 Get Football Leagues
* **Endpoint:** `GET /api/football/leagues`

#### 🔹 Get Football Standings
* **Endpoint:** `GET /api/football/standings/{leagueId}`
* **Response (200 OK):** Standings list sorted by position.

#### 🔹 Export Football Standings to CSV
* **Endpoint:** `GET /api/football/standings/{leagueId}/export`
* **Query Parameters:** `format` (default: `csv`)
* **Response:** A downloadable CSV file stream containing standard standings columns.

#### 🔹 Get Fan Profile
* **Endpoint:** `GET /api/football/fan-profile/me`
* **Access:** Authenticated User

#### 🔹 Update/Save Fan Profile
* **Endpoint:** `PUT /api/football/fan-profile/me`
* **Request Body:**
  ```json
  {
    "displayName": "GunnerFans",
    "countryCode": "GB"
  }
  ```

#### 🔹 Sync World Cup Data
Manually triggers a sync of World Cup groups, standings, and matches.
* **Endpoint:** `POST /api/football/worldcup/sync`
* **Access:** Authenticated (Admin)

#### 🔹 Get World Cup Standings
* **Endpoint:** `GET /api/football/worldcup/standings`
* **Response:** Array of `WorldCupStanding` objects.

#### 🔹 Get World Cup Matches
* **Endpoint:** `GET /api/football/worldcup/matches`

#### 🔹 Get Live World Cup Matches
* **Endpoint:** `GET /api/football/worldcup/matches/live`

#### 🔹 Get Team Stats
* **Endpoint:** `GET /api/football/worldcup/team-stats`
* **Query Params:** `countryCode` (e.g. `ARG`, `FRA`)

#### 🔹 Get Colored Figure Avatar
Generates a custom football avatar kit based on primary and secondary colors.
* **Endpoint:** `GET /api/football/figure/colored`
* **Query Params:**
  * `primary`: HEX color code (e.g., `FF0000`)
  * `secondary`: HEX color code (e.g., `0000FF`)
* **Response (200 OK):** Image stream (`image/png`)

---

### 5. Admin Dashboard (`/api/admin`)

#### 🔹 Get Admin Statistics
Fetches general counts of core entities (users, teams, fixtures, quests).
* **Endpoint:** `GET /api/admin/dashboard`
* **Access:** Authenticated (Admin)
* **Response (200 OK):**
  ```json
  {
    "users": 154,
    "tournaments": 12,
    "teams": 48,
    "quests": 25,
    "submissions": 89
  }
  ```

---

## 🛑 Exception Handling & Error Formats
All errors are intercepted globally. Common HTTP responses are formatted as:
```json
{
  "timestamp": "2026-07-01T21:46:20.4482318",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/v3/api-docs"
}
```
* **401 Unauthorized:** Credentials missing or expired.
* **403 Forbidden:** Authenticated user lacks the necessary role (e.g. trying to access admin endpoints).
* **404 Not Found:** Entity not found in database.
* **409 Conflict:** Resource (e.g., email or username) already exists.
* **400 Bad Request:** Validation constraints failed on request fields.