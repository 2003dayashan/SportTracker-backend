# Questboard API - Postman Testing Guide

This document provides instructions on how to test the Questboard API endpoints using Postman or any other API client.

## Base URL
Local Development: `http://localhost:8080` (Direct to backend) or `http://localhost:5173/api` (Via frontend Vite proxy)
API Prefix for Questboard: `/api/quests`

## Authentication
Most questboard operations (except GET requests for listing quests and leaderboards) require the user to be authenticated.
Authentication is handled via the Core Module. First, make sure to login to receive the session cookie or JWT token.

### 1. Login (Core Auth)
- **Endpoint:** `POST /api/auth/signin`
- **Body (JSON):**
```json
{
  "email": "user@example.com",
  "password": "yourpassword"
}
```
*Note: Ensure your Postman is set up to capture and send cookies automatically if using session-based auth. Subsequent requests will include the `JSESSIONID` or JWT token.*

### 2. Get Current User / Verify Auth
- **Endpoint:** `GET /api/auth/me`
- **Response:** JSON object containing `id`, `username`, `email`, and `roles` array (e.g. `["ROLE_USER"]`, `["ROLE_ADMIN"]`, `["ROLE_GUILD_MASTER"]`).

### 3. Logout (Core Auth)
- **Endpoint:** `POST /api/auth/signout`
- **Response:** Clears the session.

---

## Quest Endpoints

### 1. Get All Quests (Public)
- **Endpoint:** `GET /api/quests`
- **Response:** Array of `Quest` objects.

### 2. Get Quests by Service (Public)
- **Endpoint:** `GET /api/quests/service/{serviceType}`
- **Path Variable:** `serviceType` (e.g., `ESPORT`, `SPORT`)
- **Response:** Array of `Quest` objects filtered by service type.

### 3. Create a New Quest (Authenticated - Admin / Guild Master)
- **Endpoint:** `POST /api/quests`
- **Body (JSON):**
```json
{
  "title": "Win 3 Matches",
  "description": "Win three competitive matches in a row.",
  "serviceType": "ESPORT",
  "points": 100
}
```

### 4. Delete a Quest (Authenticated - Admin / Guild Master)
- **Endpoint:** `DELETE /api/quests/{id}`
- **Path Variable:** `id` (Quest ID)

### 5. Claim a Quest (Authenticated - Any User)
- **Endpoint:** `POST /api/quests/{id}/claim`
- **Path Variable:** `id` (Quest ID)
- **Response:** `QuestSubmission` object with status `CLAIMED`.

### 6. Submit a Quest (Authenticated - Any User)
- **Endpoint:** `POST /api/quests/{id}/submit`
- **Path Variable:** `id` (Quest ID)
- **Response:** `QuestSubmission` object with status `SUBMITTED`.

### 7. Get Pending Submissions (Authenticated - Admin / Guild Master)
- **Endpoint:** `GET /api/quests/submissions/pending`
- **Response:** Array of `QuestSubmission` objects with status `SUBMITTED`.

### 8. Approve a Submission (Authenticated - Admin / Guild Master)
- **Endpoint:** `PUT /api/quests/submissions/{submissionId}/approve`
- **Path Variable:** `submissionId`
- **Response:** `QuestSubmission` object with status `APPROVED`.

### 9. Get My Progress (Authenticated - Any User)
- **Endpoint:** `GET /api/quests/my-progress`
- **Response:** JSON object containing the user's total points and an array of their submissions.

### 10. Get Leaderboard (Public)
- **Endpoint:** `GET /api/quests/leaderboard`
- **Response:** List of users ranked by their points.
