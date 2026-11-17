# DailyReminder API

A personal planner with authenticated activities and saved reminders.

## Run locally

Use Java 17 or newer (verified with Corretto 27), Docker, and the included Maven wrapper.

Copy `.env.example` to `.env` in this project root and fill in your settings first.
Spring Boot imports this file automatically when the working directory is the project root.
Use unquoted values (Java properties syntax); keep the real `.env` out of Git.

```bash
docker compose up -d
sh mvnw spring-boot:run
```

In IntelliJ, reload the Maven project after dependency changes, then run `DailyreminderApplication`.
The API listens on port 8080. The separate `dailyreminder-web` project provides the interface.

## Database migrations

Flyway runs automatically before Hibernate validates the schema whenever the backend starts.
No separate migration command or CLI installation is required.

```bash
sh mvnw spring-boot:run
```

Migrations live in `src/main/resources/db/migration`:

- V1: initial schema and legacy schema baseline.
- V2: allow activities without a reminder.
- V3: add reminder ownership, scheduled time, completion state, and an index.
- V4: activity completion, recurrence, verification, credential versions, and recovery tokens.

Keep applied migrations unchanged. Add a new numbered migration for each future schema change.
V3 preserves existing notes; legacy notes without an owner are excluded from personal lists.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/auth/sign-up` | Create an account: name, email, password |
| POST | `/api/auth/sign-in` | Issue access and refresh tokens |
| POST | `/api/auth/refresh` | Rotate a refresh token; a used token cannot be reused |
| GET | `/api/auth` | Current user's profile, without password data |
| PATCH | `/api/auth/profile` | Update the signed-in user's display name |
| GET | `/api/activities/me` | Current user's activities |
| POST | `/api/activities/` | Create an activity |
| GET / PATCH / DELETE | `/api/activities/{id}` | Read, update, or delete an owned activity |
| GET / POST | `/api/notes` | List or create personal reminders |
| GET / PUT / DELETE | `/api/notes/{id}` | Read, edit, or delete an owned reminder |
| PATCH | `/api/notes/{id}` | Set completion with `{ "completed": true }` |

Protected endpoints require `Authorization: Bearer <access token>`.
An access token lasts two hours; a rotating refresh token lasts seven days.
Refresh tokens cannot authorize API requests. Access tokens cannot refresh a session.
Existing tokens issued before token-type enforcement require a fresh sign-in after upgrading.

Activity example:

```json
{ "name": "Review lecture notes", "duration": 30, "dataDia": "2026-11-12" }
```

Durations are whole minutes between 1 and 1,440. Reminders are optional.

Reminder example:

```json
{ "message": "Review tomorrow's schedule", "scheduledAt": "2026-11-12T13:30:00Z" }
```

Reminder timestamps are stored as instants and displayed in the browser's local timezone.
Browser reminder alerts are opt-in and run while the app is open; there is no background reminder-email or push dispatcher.
Deleting a reminder preserves any linked activities.

Interactive documentation: `/swagger-ui.html`, `/scalar`, and `/v3/api-docs`.

## Verification

```bash
sh mvnw test
```

Integration tests use the configured PostgreSQL database, apply pending Flyway migrations,
and roll back their test accounts, activities, reminders, and tokens.
Regression tests cover account creation, login, activity CRUD, reminder CRUD and ownership,
linked-activity preservation, input validation, token expiry, and refresh-token rotation.

## Dependencies

Spring Boot 4.1.2 manages Hibernate, PostgreSQL JDBC, and Flyway versions. Flyway uses its
PostgreSQL database module. Springdoc 3.2.0 supplies Swagger and Scalar integrations.

The default application properties are for local development. Configure deployment database
credentials and a private JWT secret in the deployment environment; review profile overrides
before deployment.

## Planning features and module architecture

Activities now track completion and an optional reminder UUID. The `/today` frontend composes
activities and reminders into a local-calendar dashboard with planned/completed minutes.
Reminders support `NONE`, `DAILY`, `WEEKLY`, or `CUSTOM` recurrence, an IANA `timeZone`, and
`repeatDays` (ISO weekdays 1=Monday through 7=Sunday). Include the current `scheduledAt` value
as `occurrence` when completing a reminder to make retries idempotent.

V4 adds planning/completion fields, recurrence, account verification, credential versions,
and hashed account-action tokens. New passwords require 8–72 characters and at most 72 UTF-8
bytes. Existing accounts remain verified when V4 is applied.

See [module boundaries and testing strategy](docs/modular-monolith.md). Controllers now call
application services. Modules reference each other by IDs and integrate through public APIs
or transactional events, with ArchUnit rules guarding those boundaries.

## Account recovery and verification

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/auth/forgot-password` | Request a reset link using `{ "email": "..." }` |
| POST | `/api/auth/reset-password` | Consume `{ "token": "...", "password": "..." }` |
| POST | `/api/auth/request-verification` | Request a verification message |
| POST | `/api/auth/verify-email` | Consume a verification token |

Links are single-use; resets expire after 30 minutes and verification links after 24 hours.
Resetting a password invalidates refresh tokens and existing access tokens. Email request
endpoints return the same empty 202 response for known and unknown accounts.

Email delivery is **disabled by default** locally. Copy the supplied `.env.example` to `.env`,
fill in `SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASS`, `MAIL_FROM`, and `FRONTEND_URL`,
then set `APP_MAIL_ENABLED=true` and restart the backend. No `prod` profile is needed for local
mail delivery. Use `APP_AUTH_REQUIRE_VERIFICATION=true` when you want local sign-in to require
email verification as well.

For port 587, the template enables STARTTLS. For implicit TLS on port 465, set
`SMTP_PORT=465`, `SMTP_STARTTLS=false`, and `SMTP_SSL=true`. `SMTP_AUTH=false` supports local
mail catchers without authentication. Connection/read/write timeouts are set to five seconds.

In IntelliJ, set the run configuration's working directory to the Java project root. The
`prod` profile (`SPRING_PROFILES_ACTIVE=prod`) always enables mail and requires verification;
it also requires explicit database, JWT, frontend URL, and SMTP credentials. Real environment
variables and command-line settings can override file configuration.

No recovery messages were sent during implementation: mail is mocked in recovery tests.
Configure a real SMTP transport to exercise delivery to an inbox.

### Rich activity model (V5)

Activity status is the string-persisted `ActivityStatus` enum: `INBOX`, `TODO`, `IN_PROGRESS`, `DONE`. New activities require only a title and default to `INBOX`; description (up to 10,000 characters), scheduled date (`dataDia`), due date, estimated duration (1–1,440 minutes), and linked reminder are optional. V5 maps existing incomplete activities to `TODO` and completed activities to `DONE`, preserving completion timestamps. Status is the source of truth; responses retain a derived `completed` flag for compatibility. Reopening clears the completion timestamp; repeated completion preserves it.

PATCH omits fields to preserve them. Use `clearDuration`, `clearScheduledDate`, `clearDueDate`, and `clearReminder` to explicitly remove optional planning values; send an empty description to clear its text. Conflicting `status` and `completed` values return 400. Every mutation checks ownership.

Start/restart the backend with `sh mvnw spring-boot:run` (or run the application in IntelliJ) to apply Flyway migrations automatically. The updated frontend requires V5 and the updated backend.

### Activity organization and views (V6–V7)

The activities module owns projects, labels, and ordered checklist items. Projects and labels are scoped to the authenticated account, with case-insensitive unique names per account. Assignments validate ownership; deleting a project or label detaches it while keeping activities and checklist progress. Deleting an activity removes its checklist and label links.

- `GET/POST /api/activities/projects` and `/labels`: list/create organization items.
- `PATCH/DELETE /api/activities/projects/{id}` and `/labels/{id}`: rename/delete owned items.
- Activity create/PATCH accepts `projectId`, `labelIds` (up to 20), and `checklist` (up to 100). Each step has `text` (1–255 characters), `completed`, and an optional existing `id`. New steps omit IDs; existing IDs must belong to that activity. Array order defines checklist order. Omit arrays to preserve them; send `[]` to clear them. `clearProject` removes the project.
- Checklist progress never implicitly changes the activity status.
- Activity responses include `version`. The frontend sends it on full edits; a stale edit returns 409 rather than overwriting newer changes. Quick status operations patch only status.

Flyway V6 adds the organization tables, assignments, checklist ordering, and optimistic-lock version. It runs automatically when the updated backend starts. V6 is preserved exactly as originally applied. V7 replaces its immediate checklist identity constraint with a constraint deferred until transaction commit, so reordering preserves stable step IDs safely. Applied migrations must not be edited; subsequent corrections belong in a new version.

Verification uses a separate disposable PostgreSQL database, with email disabled. Regression coverage includes ownership, duplicate names, reordering, independent completion, optional/cleared collections, deletion cleanup, and stale edits. GitHub/Slack integrations, queues, and AI review are intentionally outside this milestone.
