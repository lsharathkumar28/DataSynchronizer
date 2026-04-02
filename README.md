# DataSynchronizer

Backend service for managing user records. Built with Spring Boot, PostgreSQL, and Kafka.

The basic idea: this app owns user data (CRUD via REST), and whenever something changes, it fires off a Kafka event so other systems can pick it up and stay in sync. The `users` table has standard fields plus a JSONB `attributes` column for anything extra you want to throw in.

## Running it

Make sure Docker is installed, then:

```bash
git clone https://github.com/lsharathkumar28/DataSynchronizer.git
cd DataSynchronizer
docker compose up --build
```

That spins up Postgres (5432), Kafka (9092), and the app itself (8080). First time takes a while because Maven has to pull all the dependencies inside the container. After that it's much faster.

Once it's up the APIs can be accessed via the Swagger URL: http://localhost:8080/swagger-ui.html

### Running the application locally

There is no need to rebuild the Docker image every time the source code is changed. Just run Postgres and Kafka in Docker and the app on your machine:

```bash
docker compose up postgres kafka -d
./mvnw spring-boot:run    # needs Java 17+
```

## Endpoints

All under `/api/v1/users`:

- `GET /api/v1/users` — list all
- `GET /api/v1/users/{id}` — get by UUID
- `POST /api/v1/users` — create (required: `name`, `firstName`, `lastName`, `emailId`)
- `PUT /api/v1/users/{id}` — update
- `DELETE /api/v1/users/{id}` — delete
- `GET /api/v1/users/search?emailId=...` — search by email

Example:

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "firstName": "John",
    "lastName": "Doe",
    "emailId": "john@example.com",
    "phoneNumber": "555-0101",
    "attributes": {"department": "Engineering", "title": "Developer"}
  }'
```

## Kafka

Every create/update/delete pushes a message to the `user-events` topic. Key is the user UUID, value is JSON:

```json
{
  "userId": "some-uuid",
  "changeType": "CREATED",
  "timestamp": "2026-04-01T10:30:00Z",
  "name": "John Doe",
  "firstName": "John",
  "lastName": "Doe",
  "emailId": "john@example.com",
  "attributes": {"department": "Engineering"}
}
```

Deletes only have `userId`, `changeType`, and `timestamp` — everything else is null.

## Project layout

```
├── compose.yaml        # postgres + kafka + app
├── Dockerfile          # multi-stage build
├── init.sql            # table creation (runs on first DB startup)
└── src/main/java/com/outreach/datasynchronizer/
    ├── controller/     # REST API
    ├── service/        # business logic + Kafka publishing
    ├── repository/     # Spring Data JPA
    ├── entity/         # User JPA entity
    ├── dto/            # request/response objects
    ├── event/          # Kafka event stuff
    ├── exception/      # error handling
    └── config/         # Kafka + Swagger setup
```

## Shutting down

```bash
docker compose down        # stop everything
docker compose down -v     # stop + wipe the database
```

## See also

[DataSyncDriver](https://github.com/lsharathkumar28/DataSyncDriver) — consumes from this service (REST + Kafka) and writes data out to external systems (CSV, JSON, etc.).
