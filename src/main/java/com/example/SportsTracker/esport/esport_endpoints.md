# Esport API Endpoints

This document outlines the REST API endpoints available in the esport module.

## Admin Dashboard (`/api/admin/dashboard`)
* `GET /api/admin/dashboard` - Retrieve overall statistics (counts of tournaments, teams, players, and matches).

## Tournaments (`/api/tournaments`)
* `GET /api/tournaments` - Retrieve a paginated list of tournaments. Accepts optional query parameters: `status`, `game`, `page` (default 0), `size` (default 10).
* `POST /api/tournaments` - Create a new tournament.
* `GET /api/tournaments/{id}` - Retrieve a specific tournament by its ID.
* `PUT /api/tournaments/{id}` - Update an existing tournament by its ID.
* `DELETE /api/tournaments/{id}` - Delete a tournament by its ID.
* `GET /api/tournaments/search?q={query}` - Search for tournaments based on a query string.

## Brackets (`/api/tournaments/{tournamentId}/bracket`)
* `GET /api/tournaments/{tournamentId}/bracket` - Retrieve the match bracket for a specific tournament, ordered by scheduled time.

## Standings (`/api/tournaments/{tournamentId}/standings`)
* `GET /api/tournaments/{tournamentId}/standings` - Retrieve the standings/leaderboard for a specific tournament.

## Teams (`/api/teams`)
* `GET /api/teams` - Retrieve a list of all teams.
* `POST /api/teams` - Create a new team.
* `GET /api/teams/{id}` - Retrieve a specific team by its ID.
* `PUT /api/teams/{id}` - Update an existing team by its ID.
* `DELETE /api/teams/{id}` - Delete a team by its ID.
* `GET /api/teams/search?q={query}` - Search for teams based on a query string.

## Players (`/api/players`)
* `GET /api/players` - Retrieve a list of all players. Accepts an optional query parameter `teamId` to filter by team.
* `POST /api/players` - Create a new player.
* `GET /api/players/{id}` - Retrieve a specific player by their ID.
* `PUT /api/players/{id}` - Update an existing player by their ID.
* `DELETE /api/players/{id}` - Delete a player by their ID.
* `GET /api/players/search?q={query}` - Search for players based on a query string.

## Matches (`/api/matches`)
* `POST /api/matches` - Create a new match.
* `GET /api/matches/{id}` - Retrieve a specific match by its ID.
* `PUT /api/matches/{id}/score` - Update the score of a specific match.

## MVC/View Endpoints
* `GET /esports` - Returns the `esport/home` view.
