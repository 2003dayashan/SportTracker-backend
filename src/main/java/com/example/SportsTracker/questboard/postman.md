# Quest Board API Endpoints - Postman Collection Guide

This document provides a complete guide to all Quest Board API endpoints, formatted for easy import into Postman or manual testing.

## Authentication Endpoints
All auth endpoints are under `/api/questboard/auth/`

### 1. Register User
- **Method**: POST
- **URL**: `http://localhost:8080/api/questboard/auth/register`
- **Headers**: 
  - Content-Type: application/json
- **Body** (raw, JSON):
  ```json
  {
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }
  ```
- **Expected Status**: 201 Created
- **Sample Response**:
  ```json
  {
    "id": "user123",
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER"
  }
  ```
- **Notes**: Sets HTTP-only cookie `quest_token` with JWT for authentication

### 2. Login User
- **Method**: POST
- **URL**: `http://localhost:8080/api/questboard/auth/login`
- **Headers**: 
  - Content-Type: application/json
- **Body** (raw, JSON):
  ```json
  {
    "email": "test@example.com",
    "password": "password123"
  }
  ```
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  {
    "id": "user123",
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER"
  }
  ```
- **Notes**: Sets HTTP-only cookie `quest_token` with JWT for authentication

### 3. Logout User
- **Method**: POST
- **URL**: `http://localhost:8080/api/questboard/auth/logout`
- **Headers**: None required
- **Body**: None
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  {
    "message": "Logged out"
  }
  ```
- **Notes**: Clears the `quest_token` cookie

### 4. Get Current User
- **Method**: GET
- **URL**: `http://localhost:8080/api/questboard/auth/me`
- **Headers**: None required (uses cookie)
- **Body**: None
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  {
    "id": "user123",
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER"
  }
  ```
- **Error Responses**: 401 Unauthorized if not authenticated
- **Notes**: Requires valid JWT token in `quest_token` cookie

## Quest Management Endpoints
All quest endpoints are under `/api/quests/`

### 5. Get All Quests (with filtering/pagination)
- **Method**: GET
- **URL`: `http://localhost:8080/api/quests`
- **Headers**: None required
- **Body**: None
- **Query Parameters**:
  - `page` (integer, default: 0) - Page number
  - `size` (integer, default: 10) - Page size
  - `sort` (string, default: "createdAt,desc") - Format: "property,direction"
  - `categoryId` (string, optional) - Filter by category ID
  - `status` (Quest.Status enum, optional) - OPEN, CLAIMED, COMPLETED, etc.
  - `keyword` (string, optional) - Search in title/description
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  {
    "content": [
      {
        "id": "quest123",
        "title": "Complete 5k Run",
        "description": "Run 5 kilometers in under 30 minutes",
        "status": "OPEN",
        "difficulty": "MEDIUM",
        "rewardXp": 100,
        "deadline": "2026-12-31T00:00:00Z",
        "categoryId": "cat1",
        "createdBy": "user123",
        "claimedBy": null,
        "imageUrl": null,
        "notDeleted": true
      }
    ],
    "pageable": {
      "sort": {"sorted": false, "unsorted": true, "empty": true},
      "pageNumber": 0,
      "pageSize": 10,
      "offset": 0,
      "unpaged": true,
      "paged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "size": 10,
    "number": 0,
    "sort": {"sorted": false, "unsorted": true, "empty": true},
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
  ```
- **Notes**: Public endpoint, no authentication required

### 6. Get Quest by ID
- **Method**: GET
- **URL**: `http://localhost:8080/api/quests/{id}` (replace {id} with actual quest ID)
- **Headers**: None required
- **Body**: None
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  {
    "id": "quest123",
    "title": "Complete 5k Run",
    "description": "Run 5 kilometers in under 30 minutes",
    "status": "OPEN",
    "difficulty": "MEDIUM",
    "rewardXp": 100,
    "deadline": "2026-12-31T00:00:00Z",
    "categoryId": "cat1",
    "createdBy": "user123",
    "claimedBy": null,
    "imageUrl": null,
    "notDeleted": true
  }
  ```
- **Error Responses**: 404 Not Found if quest doesn't exist
- **Notes**: Public endpoint, no authentication required

### 7. Create Quest
- **Method**: POST
- **URL**: `http://localhost:8080/api/quests`
- **Headers**: 
  - Content-Type: application/json
- **Body** (raw, JSON):
  ```json
  {
    "title": "Complete 10k Run",
    "description": "Run 10 kilometers in under 60 minutes",
    "difficulty": "HARD",
    "rewardXp": 200,
    "deadline": "2026-12-31",
    "categoryId": "cat1"
  }
  ```
- **Expected Status**: 201 Created
- **Sample Response**:
  ```json
  {
    "id": "quest456",
    "title": "Complete 10k Run",
    "description": "Run 10 kilometers in under 60 minutes",
    "status": "OPEN",
    "difficulty": "HARD",
    "rewardXp": 200,
    "deadline": "2026-12-31T00:00:00Z",
    "categoryId": "cat1",
    "createdBy": "user123",
    "claimedBy": null,
    "imageUrl": null,
    "notDeleted": true
  }
  ```
- **Error Responses**: 
  - 401 Unauthorized if not authenticated
  - 403 Forbidden if insufficient permissions (requires GUILD_MASTER or ADMIN role)
- **Notes**: Requires valid JWT token in `quest_token` cookie and GUILD_MASTER or ADMIN role

### 8. Update Quest
- **Method**: PUT
- **URL**: `http://localhost:8080/api/quests/{id}` (replace {id} with actual quest ID)
- **Headers**: 
  - Content-Type: application/json
- **Body** (raw, JSON):
  ```json
  {
    "title": "Updated Quest Title",
    "description": "Updated quest description",
    "difficulty": "MEDIUM",
    "rewardXp": 150,
    "deadline": "2026-12-31",
    "categoryId": "cat2"
  }
  ```
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  {
    "id": "quest123",
    "title": "Updated Quest Title",
    "description": "Updated quest description",
    "status": "OPEN",
    "difficulty": "MEDIUM",
    "rewardXp": 150,
    "deadline": "2026-12-31T00:00:00Z",
    "categoryId": "cat2",
    "createdBy": "user123",
    "claimedBy": null,
    "imageUrl": null,
    "notDeleted": true
  }
  ```
- **Error Responses**: 
  - 401 Unauthorized if not authenticated
  - 403 Forbidden if insufficient permissions (requires GUILD_MASTER or ADMIN role)
  - 404 Not Found if quest doesn't exist
- **Notes**: Requires valid JWT token in `quest_token` cookie and GUILD_MASTER or ADMIN role

### 9. Delete Quest
- **Method**: DELETE
- **URL**: `http://localhost:8080/api/quests/{id}` (replace {id} with actual quest ID)
- **Headers**: None required
- **Body**: None
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  {
    "deleted": true
  }
  ```
- **Error Responses**: 
  - 401 Unauthorized if not authenticated
  - 403 Forbidden if insufficient permissions (requires ADMIN role)
  - 404 Not Found if quest doesn't exist
- **Notes**: Requires valid JWT token in `quest_token` cookie and ADMIN role

### 10. Claim Quest
- **Method**: POST
- **URL**: `http://localhost:8080/api/quests/{id}/claim` (replace {id} with actual quest ID)
- **Headers**: None required (uses cookie)
- **Body**: None
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  {
    "id": "quest123",
    "title": "Complete 5k Run",
    "description": "Run 5 kilometers in under 30 minutes",
    "status": "CLAIMED",
    "difficulty": "MEDIUM",
    "rewardXp": 100,
    "deadline": "2026-12-31T00:00:00Z",
    "categoryId": "cat1",
    "createdBy": "user123",
    "claimedBy": "user123",
    "imageUrl": null,
    "notDeleted": true
  }
  ```
- **Error Responses**: 
  - 401 Unauthorized if not authenticated
  - 403 Forbidden if insufficient permissions (requires USER role)
  - 404 Not Found if quest doesn't exist
  - 400 Bad Request if quest cannot be claimed (already claimed, wrong status, etc.)
- **Notes**: Requires valid JWT token in `quest_token` cookie and USER role

### 11. Submit Proof for Quest
- **Method**: POST
- **URL**: `http://localhost:8080/api/quests/{id}/submit` (replace {id} with actual quest ID)
- **Headers**: 
  - Content-Type: multipart/form-data
- **Body** (form-data):
  - Key: `proofText`, Value: "I completed the 5k run in 28 minutes 45 seconds", Type: Text
  - Key: `proofFile`, Value: [select file], Type: File (optional)
- **Expected Status**: 201 Created
- **Sample Response**:
  ```json
  {
    "id": "submission123",
    "questId": "quest123",
    "userId": "user123",
    "proofText": "I completed the 5k run in 28 minutes 45 seconds",
    "proofFileUrl": "uploads/proof123.jpg",
    "status": "PENDING",
    "submittedAt": "2026-06-26T10:30:00Z"
  }
  ```
- **Error Responses**: 
  - 401 Unauthorized if not authenticated
  - 403 Forbidden if insufficient permissions (requires USER role)
  - 404 Not Found if quest doesn't exist
  - 400 Bad Request if proofText is missing
- **Notes**: Requires valid JWT token in `quest_token` cookie and USER role. Quest must be claimed by the current user.

### 12. Approve/Reject Submission
- **Method**: POST
- **URL**: `http://localhost:8080/api/quests/submissions/{id}/approve` (replace {id} with actual submission ID)
- **Headers**: None required (uses cookie)
- **Body**: None
- **Query Parameters**:
  - `approved` (boolean, required) - true to approve, false to reject
- **Expected Status**: 200 OK
- **Sample Response** (approved):
  ```json
  {
    "id": "submission123",
    "questId": "quest123",
    "userId": "user123",
    "proofText": "I completed the 5k run in 28 minutes 45 seconds",
    "proofFileUrl": "uploads/proof123.jpg",
    "status": "APPROVED",
    "submittedAt": "2026-06-26T10:30:00Z"
  }
  ```
- **Error Responses**: 
  - 401 Unauthorized if not authenticated
  - 403 Forbidden if insufficient permissions (requires GUILD_MASTER or ADMIN role)
  - 404 Not Found if submission doesn't exist
- **Notes**: Requires valid JWT token in `quest_token` cookie and GUILD_MASTER or ADMIN role

## Category Endpoints
All category endpoints are under `/api/categories/`

### 13. Get All Categories
- **Method**: GET
- **URL**: `http://localhost:8080/api/categories`
- **Headers**: None required
- **Body**: None
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  [
    {
      "id": "cat1",
      "name": "Fitness",
      "description": "Physical fitness and exercise challenges"
    },
    {
      "id": "cat2",
      "name": "Learning",
      "description": "Educational and skill development challenges"
    }
  ]
  ```
- **Notes**: Public endpoint, no authentication required

### 14. Create Category
- **Method**: POST
- **URL**: `http://localhost:8080/api/categories`
- **Headers**: 
  - Content-Type: application/json
- **Body** (raw, JSON):
  ```json
  {
    "name": "Travel",
    "description": "Travel and exploration challenges"
  }
  ```
- **Expected Status**: 200 OK
- **Sample Response**:
  ```json
  {
    "id": "cat3",
    "name": "Travel",
    "description": "Travel and exploration challenges"
  }
  ```
- **Error Responses**: 
  - 401 Unauthorized if not authenticated
  - 403 Forbidden if insufficient permissions (requires ADMIN role)
- **Notes**: Requires valid JWT token in `quest_token` cookie and ADMIN role

## File Endpoints
All file endpoints are under `/api/files/`

### 15. Upload File
- **Method**: POST
- **URL**: `http://localhost:8080/api/files/upload`
- **Headers**: 
  - Content-Type: multipart/form-data
- **Body** (form-data):
  - Key: `file`, Value: [select file], Type: File
- **Expected Status**: 200 OK
- **Sample Response**: "filename.jpg" (just the filename as plain text)
- **Notes**: Returns the filename which can