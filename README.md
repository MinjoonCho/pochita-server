# Pochita Spring Backend

Spring Boot backend for the Pochita app.

## Stack

- Java 17
- Spring Boot 3.3
- Spring Web
- Spring Data JPA
- Spring Validation
- H2 database for local development
- PostgreSQL for persistent production deployments

## What It Covers

- Email/password signup and login
- Google demo login
- User profile fetch/update
- Group creation, password-protected join, invite-code regeneration
- Session start/finish and user session history
- User, university, group, and category rankings
- Stats payloads for today, total, weekly, and category breakdown

## API Surface

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/google-demo`
- `GET /api/users/{userId}`
- `PATCH /api/users/{userId}`
- `POST /api/groups`
- `POST /api/groups/join`
- `GET /api/groups`
- `GET /api/groups/{groupId}`
- `GET /api/groups/{groupId}/members`
- `POST /api/groups/{groupId}/invite-code`
- `POST /api/sessions/start`
- `POST /api/sessions/{sessionId}/finish`
- `GET /api/sessions/users/{userId}`
- `GET /api/stats/users/{userId}`
- `GET /api/rankings/universities`
- `GET /api/rankings/groups`
- `GET /api/rankings/users`
- `GET /api/rankings/categories`

## Project Layout

- Application entry: [src/main/java/com/pochita/server/PochitaServerApplication.java](/Users/minjooncho/SandBox/pochita-server/src/main/java/com/pochita/server/PochitaServerApplication.java)
- Controllers: [src/main/java/com/pochita/server/controller](/Users/minjooncho/SandBox/pochita-server/src/main/java/com/pochita/server/controller)
- Services: [src/main/java/com/pochita/server/service](/Users/minjooncho/SandBox/pochita-server/src/main/java/com/pochita/server/service)
- Entities and repositories: [src/main/java/com/pochita/server/domain](/Users/minjooncho/SandBox/pochita-server/src/main/java/com/pochita/server/domain), [src/main/java/com/pochita/server/repository](/Users/minjooncho/SandBox/pochita-server/src/main/java/com/pochita/server/repository)
- App config: [src/main/resources/application.yml](/Users/minjooncho/SandBox/pochita-server/src/main/resources/application.yml)

## Running

This workspace currently has Java 17 installed, but no local Gradle or Maven binary.

To run the server locally once Gradle is available:

```bash
cd /Users/minjooncho/SandBox/pochita-server
gradle bootRun
```

Or generate a Gradle wrapper and run:

```bash
./gradlew bootRun
```

The API listens on `http://localhost:8080` and allows CORS from `http://localhost:3000`.
Locally it falls back to H2. In production it will automatically use PostgreSQL when `PGHOST`-style variables or `SPRING_DATASOURCE_URL` / `JDBC_DATABASE_URL` are present.

## Docker

You can also build the backend in Docker:

```bash
cd /Users/minjooncho/SandBox/pochita-server
docker build -t pochita-server .
docker run --rm -p 8080:8080 pochita-server
```

For full frontend + backend deployment, see [DEPLOY.md](/Users/minjooncho/SandBox/DEPLOY.md).
For production deployment on Vercel + Railway, see [DEPLOY_VERCEL_RAILWAY.md](/Users/minjooncho/SandBox/DEPLOY_VERCEL_RAILWAY.md).

## Example Requests

Register:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@test.com",
    "password": "123456",
    "nickname": "테스트계정",
    "university": "포치타대학교",
    "major": "컴퓨터공학과",
    "year": "4학년",
    "avatarEmoji": "🦊"
  }'
```

Start a session:

```bash
curl -X POST http://localhost:8080/api/sessions/start \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "test",
    "categoryId": "게임"
  }'
```

Get user stats:

```bash
curl http://localhost:8080/api/stats/users/test
```
