# Quest Board — Claude Memory File

## What Is This Project?

A guild-style task management system where users (adventurers) browse, claim, and complete quests. Guild Masters post quests and approve completions. Users earn XP and level up over time.

---

## Roles

| Role | Permissions |
|------|-------------|
| `USER` | Browse, claim, submit quests. View own profile and XP. |
| `GUILD_MASTER` | Everything USER can + create/edit quests + approve/reject submissions. |
| `ADMIN` | Full control — delete quests, manage users, change roles. |

---

## User Journeys

### Adventurer (USER)
1. Signs up → logs in → lands on the quest board
2. Browses quests — filters by category, difficulty, status
3. Clicks a quest → reads details → clicks **"Claim Quest"**
4. Completes the task in real life or on the platform
5. Returns → clicks **"Submit Proof"** → uploads file + writes description
6. Waits for Guild Master approval → receives XP on profile

### Guild Master
1. Logs in with `GUILD_MASTER` role
2. Creates a new quest — title, description, difficulty, XP reward, deadline, category
3. Views pending submissions on the admin panel
4. Reviews proof → **approves** or **rejects** → XP flows to the user on approval

---

## Quest Status Flow

```
OPEN → CLAIMED → SUBMITTED → COMPLETED
                           → REJECTED
CLAIMED (past deadline, nightly job) → EXPIRED
```

- `OPEN` — quest is available to claim
- `CLAIMED` — an adventurer has claimed it
- `SUBMITTED` — adventurer submitted proof, awaiting review
- `COMPLETED` — Guild Master approved; XP awarded
- `REJECTED` — Guild Master rejected the submission
- `EXPIRED` — `@Scheduled` nightly job sets this if a `CLAIMED` quest passes its deadline

---

## MongoDB Collections

### `users`
| Field | Type | Notes |
|-------|------|-------|
| `id` | ObjectId | Primary key |
| `username` | String | Unique |
| `email` | String | Unique |
| `passwordHash` | String | Hashed password |
| `role` | Enum | `USER`, `GUILD_MASTER`, `ADMIN` |
| `xp` | Number | Total XP earned |
| `level` | Number | Derived from XP |
| `createdAt` | Date | Account creation timestamp |

### `quests`
| Field | Type | Notes |
|-------|------|-------|
| `id` | ObjectId | Primary key |
| `title` | String | Quest title |
| `description` | String | Full quest details |
| `status` | Enum | `OPEN`, `CLAIMED`, `SUBMITTED`, `COMPLETED`, `REJECTED`, `EXPIRED` |
| `difficulty` | Enum | e.g. `EASY`, `MEDIUM`, `HARD` |
| `rewardXp` | Number | XP granted on completion |
| `deadline` | Date | Expiry date for claimed quests |
| `categoryId` | ObjectId | Ref → `categories` |
| `createdBy` | ObjectId | Ref → `users` (Guild Master) |
| `claimedBy` | ObjectId | Ref → `users` (Adventurer) |
| `imageUrl` | String | Optional quest image |
| `deletedAt` | Date | Soft delete timestamp |

### `categories`
| Field | Type | Notes |
|-------|------|-------|
| `id` | ObjectId | Primary key |
| `name` | String | Category name |
| `icon` | String | Icon identifier |
| `color` | String | Hex color code |
| `description` | String | Category description |

### `quest_submissions`
| Field | Type | Notes |
|-------|------|-------|
| `id` | ObjectId | Primary key |
| `questId` | ObjectId | Ref → `quests` |
| `userId` | ObjectId | Ref → `users` |
| `proofText` | String | Written proof description |
| `proofFileUrl` | String | Uploaded file URL |
| `status` | Enum | `PENDING`, `APPROVED`, `REJECTED` |
| `submittedAt` | Date | Submission timestamp |

---

## Key Business Rules

- A quest can only be claimed by **one user** at a time
- XP is awarded **only** when a Guild Master approves a submission
- Nightly `@Scheduled` job scans `CLAIMED` quests past `deadline` → sets to `EXPIRED`
- Soft deletes on quests via `deletedAt` field (Admin only)
- Level is computed from cumulative `xp` on the `users` collection

---

## Commands

> Fill these in once the project is scaffolded.

```bash
# Run the app
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build
./mvnw clean package
```

---

## Environment Variables

```env
MONGODB_URI=mongodb://localhost:27017/questboard
JWT_SECRET=your_jwt_secret_here
FILE_UPLOAD_PATH=./uploads
```

---

## Tech Stack

- **Backend:** Java + Spring Boot
- **Database:** MongoDB
- **Auth:** JWT (role-based)
- **Scheduler:** Spring `@Scheduled` (nightly expiry job)
- **File uploads:** Multipart form data

---

## Notes for Claude

- Always check the user's `role` before allowing create/edit/delete operations
- Quest status transitions must follow the defined flow — no skipping states
- XP logic lives in the submission approval handler
- The nightly job only targets `CLAIMED` quests — not `SUBMITTED` ones
- Use soft deletes (`deletedAt`) for quests, never hard delete
