# Esport API Endpoints - Postman Guide

This document provides a comprehensive guide for testing the REST API endpoints in the esport module using Postman. It includes details on HTTP methods, routes, parameters, and sample JSON payloads.

---

## 1. Admin Dashboard (`/api/admin/dashboard`)

### Get Dashboard Statistics
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/admin/dashboard`
* **Description:** Retrieve overall statistics (counts of tournaments, teams, players, and matches).
* **Response Example:**
  ```json
  {
    "tournaments": 5,
    "teams": 20,
    "players": 100,
    "matches": 45
  }
  ```

---

## 2. Tournaments (`/api/tournaments`)

### Get All Tournaments
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/tournaments`
* **Query Params (Optional):**
  * `status`: e.g., `UPCOMING`, `ONGOING`, `COMPLETED`
  * `game`: e.g., `Valorant`
  * `page`: `0` (default)
  * `size`: `10` (default)
* **Description:** Retrieve a paginated list of tournaments.

### Create a Tournament
* **Method:** `POST`
* **URL:** `http://localhost:8080/api/tournaments`
* **Headers:** `Content-Type: application/json`
* **Body (raw JSON):**
  ```json
  {
    "name": "Valorant Champions 2026",
    "game": "Valorant",
    "format": "SINGLE_ELIMINATION",
    "status": "UPCOMING",
    "startDate": "2026-08-01T10:00:00",
    "endDate": "2026-08-15T18:00:00",
    "maxTeams": 16
  }
  ```

### Get a Specific Tournament
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/tournaments/{id}`
* **Path Variables:** Replace `{id}` with the tournament ID.

### Update a Tournament
* **Method:** `PUT`
* **URL:** `http://localhost:8080/api/tournaments/{id}`
* **Headers:** `Content-Type: application/json`
* **Body (raw JSON):** Use the same structure as the Create Tournament request, modifying the desired fields.

### Delete a Tournament
* **Method:** `DELETE`
* **URL:** `http://localhost:8080/api/tournaments/{id}`

### Search Tournaments
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/tournaments/search?q=Valorant`

---

## 3. Brackets (`/api/tournaments/{tournamentId}/bracket`)

### Get Tournament Bracket
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/tournaments/{tournamentId}/bracket`
* **Description:** Retrieve the match bracket for a specific tournament, ordered by scheduled time.

---

## 4. Standings (`/api/tournaments/{tournamentId}/standings`)

### Get Tournament Standings
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/tournaments/{tournamentId}/standings`
* **Description:** Retrieve the standings/leaderboard for a specific tournament. It calculates points, wins, losses based on completed matches.

---

## 5. Teams (`/api/teams`)

### Get All Teams
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/teams`

### Create a Team
* **Method:** `POST`
* **URL:** `http://localhost:8080/api/teams`
* **Headers:** `Content-Type: application/json`
* **Body (raw JSON):**
  ```json
  {
    "name": "Sentinels",
    "tournamentId": "64b1c2e8f1a2...",
    "logoUrl": "https://example.com/logo.png"
  }
  ```
  *(Note: Replace `tournamentId` with a real ID from your database)*

### Get a Specific Team
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/teams/{id}`

### Update a Team
* **Method:** `PUT`
* **URL:** `http://localhost:8080/api/teams/{id}`
* **Headers:** `Content-Type: application/json`
* **Body (raw JSON):** Same structure as Create Team.

### Delete a Team
* **Method:** `DELETE`
* **URL:** `http://localhost:8080/api/teams/{id}`

### Search Teams
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/teams/search?q=Sentinels`

---

## 6. Players (`/api/players`)

### Get All Players
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/players`
* **Query Params (Optional):**
  * `teamId`: Filters players belonging to a specific team.

### Create a Player
* **Method:** `POST`
* **URL:** `http://localhost:8080/api/players`
* **Headers:** `Content-Type: application/json`
* **Body (raw JSON):**
  ```json
  {
    "username": "TenZ",
    "teamId": "64b1c2f9e4b3...",
    "role": "Duelist"
  }
  ```

### Get a Specific Player
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/players/{id}`

### Update a Player
* **Method:** `PUT`
* **URL:** `http://localhost:8080/api/players/{id}`
* **Headers:** `Content-Type: application/json`
* **Body (raw JSON):** Same structure as Create Player.

### Delete a Player
* **Method:** `DELETE`
* **URL:** `http://localhost:8080/api/players/{id}`

### Search Players
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/players/search?q=TenZ`

---

## 7. Matches (`/api/matches`)

### Create a Match
* **Method:** `POST`
* **URL:** `http://localhost:8080/api/matches`
* **Headers:** `Content-Type: application/json`
* **Body (raw JSON):**
  ```json
  {
    "tournamentId": "64b1c2e8f1a2...",
    "teamAId": "64b1c2f9e4b3...",
    "teamBId": "64b1c312d5a1...",
    "status": "SCHEDULED",
    "scheduledAt": "2026-08-05T15:00:00"
  }
  ```

### Get a Specific Match
* **Method:** `GET`
* **URL:** `http://localhost:8080/api/matches/{id}`

### Update Match Score
* **Method:** `PUT`
* **URL:** `http://localhost:8080/api/matches/{id}/score`
* **Headers:** `Content-Type: application/json`
* **Body (raw JSON):**
  ```json
  {
    "scoreA": 2,
    "scoreB": 1
  }
  ```
  *(Note: Updating scores might internally trigger the MatchService to compute the winner or update match status to `COMPLETED` depending on your backend logic)*

---

## 8. MVC/View Endpoints

### Esport Home Page
* **Method:** `GET`
* **URL:** `http://localhost:8080/esports`
* **Description:** Returns the HTML view `esport/home` (Not an API endpoint returning JSON).
