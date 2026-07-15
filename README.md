# NeuroSession

**NeuroSession** is a data management platform built for clinical and psychological research teams working with hypnosis, virtual reality, and EEG. It replaces scattered spreadsheets with a single structured workspace where a research team can record participant baseline data, document experimental session outcomes, and track study progress.

It was built for the **GIGA Consciousness Lab** (University of Liège) to support the **SMARTHY-II** study, which compares Virtual Reality Hypnosis (VRH) against traditional hypnosis (HYP) using resting-state EEG, but the data model and workflow are generic enough to support other hypnosis/VR research protocols.

**Live demo:** [neurosession-app-production.up.railway.app](https://neurosession-app-production.up.railway.app)

---

## Table of contents

- [What it does](#what-it-does)
- [Tech stack](#tech-stack)
- [How it's put together](#how-its-put-together)
- [Data model](#data-model)
- [API reference](#api-reference)
- [Running it locally](#running-it-locally)
- [Environment variables](#environment-variables)
- [Project structure](#project-structure)
- [Security notes](#security-notes)
- [Known limitations / roadmap](#known-limitations--roadmap)
- [Credits](#credits)

---

## What it does

A researcher signs in and gets access to four things:

1. **Participant records** — pseudonymized participant profiles (age, sex, participant code) plus baseline psychometric scores: EHS (hypnotic susceptibility), STAI (state/trait anxiety), QPI (hypnotic depth), TAS (alexithymia), and DES (dissociation).
2. **Session documentation** — for each participant, a record of their VRH session and/or HYP session: pre/post VAS scores (anxiety, pain), subjective experience (absorption, dissociation, automaticity, vigilance, time perception), cybersickness (CSQ), presence, and an 8-item satisfaction questionnaire.
3. **Study monitoring** — a dashboard showing which participants have completed which session type and which are still missing data, so nothing falls through the cracks as the study progresses.
4. **LLM-assisted reporting** *(in progress)* — a first-draft participant summary generated from the collected data, intended purely as a documentation starting point. Every generated summary requires human review before it's used in any report or publication.

Each researcher only sees the participants they created — there's no cross-researcher data leakage within the same deployment.

---

## Tech stack

| Layer          | Choice                                                              |
|----------------|----------------------------------------------------------------------|
| Backend        | Java 17, Spring Boot 3.5, Spring Web, Spring Data JPA                |
| Auth           | Spring Security + JWT (`jjwt`), stateless sessions, BCrypt hashing   |
| Database       | PostgreSQL, Hibernate (schema managed manually — `ddl-auto: none`)   |
| Frontend       | AngularJS 1.8 (SPA, hash-based routing), plain CSS (no framework)    |
| Dev environment| Maven Wrapper, local PostgreSQL instance                              |

There's no build step for the frontend — it's served directly as static files by Spring Boot from `src/main/resources/static`.

---

## How it's put together

```
Browser (AngularJS SPA)
   │  fetches views/*.html + calls /api/**
   ▼
Spring Boot app
   ├─ AuthController        /api/auth/**        (public: register, login)
   ├─ ParticipantController /api/participants/** (JWT required)
   ├─ ExperimentSessionCtrl /api/participants/{id}/sessions, /api/sessions/**
   └─ JwtAuthenticationFilter validates the Bearer token on every other request
   │
   ▼
PostgreSQL (users, participants, experiment_sessions)
```

- **Auth flow**: `POST /api/auth/login` verifies credentials against a BCrypt hash and returns a signed JWT. The Angular app stores it in `localStorage` and an HTTP interceptor (`authInterceptor`) attaches it as `Authorization: Bearer <token>` on every subsequent request.
- **Authorization**: all `/api/**` routes except `/api/auth/**` require a valid JWT (`SecurityConfig`, stateless sessions — no server-side session state).
- **Data scoping**: `Participant` rows are tied to the `User` (researcher) that created them; participant queries are always filtered by the authenticated user's ID, not just by role.

---

## Data model

**User** (a researcher or admin account)
- `firstName`, `lastName`, `email` (unique), `password` (BCrypt-hashed), `role` (`RESEARCHER` or `ADMIN`)

**Participant** (one row per study participant, owned by a researcher)
- `participantCode`, `sex`, `age`, `timeStarted`
- Baseline scores: `qpiTotal`, `staiEtatTotal`, `staiTraitTotal`, `tasTotal`, `desTotal`, `ehsScore`, `ehsLevel`

**ExperimentSession** (0–2 per participant: one `VRH`, one `HYP`)
- `sessionType` (`"VRH"` or `"HYP"`)
- VAS measures: `vasPreAnxiete`, `vasPostAnxiete`, `vasPreDouleur`, `vasPostDouleur`
- Subjective experience: `absorption`, `dissociation`, `automaticite`, `eveilVigilance`, `perceptionTempsMinutes`
- Cybersickness: `csqNausea`, `csqOculomotor`
- Presence: `presenceSpatiale`, `validiteEcologique`, `presenceSociale`, `copresence`
- Satisfaction: `satisfactionQ1`–`satisfactionQ8`

---

## API reference

All endpoints below (except auth) require an `Authorization: Bearer <token>` header.

| Method | Path                                   | Description                                          |
|--------|-----------------------------------------|-------------------------------------------------------|
| POST   | `/api/auth/register`                   | Create a researcher/admin account                      |
| POST   | `/api/auth/login`                      | Log in, get a JWT                                      |
| GET    | `/api/participants`                    | List the current researcher's participants             |
| POST   | `/api/participants`                    | Create a participant                                    |
| GET    | `/api/participants/{id}`               | Get one participant                                     |
| PUT    | `/api/participants/{id}`               | Update a participant                                    |
| DELETE | `/api/participants/{id}`               | Delete a participant                                    |
| GET    | `/api/participants/progress`           | Completion status (VRH/HYP done or missing) per participant |
| GET    | `/api/participants/{id}/sessions`      | List a participant's sessions                           |
| POST   | `/api/participants/{id}/sessions`      | Add a session (VRH or HYP) for a participant            |
| PUT    | `/api/sessions/{sessionId}`            | Update a session                                        |
| DELETE | `/api/sessions/{sessionId}`            | Delete a session                                        |

---

## Running it locally

### Prerequisites

- Java 17
- A local PostgreSQL instance

### 1. Create the database

You need a local PostgreSQL server running. Create a database for the app:

```bash
createdb neurosession_dev
```

(or run `CREATE DATABASE neurosession_dev;` from `psql`.) The schema itself isn't managed by Hibernate (`ddl-auto: none`), so you'll also need to run whatever DDL/migration scripts create the `users`, `participants`, and `experiment_sessions` tables before starting the app.

### 2. Set your secrets

`application.yml` reads credentials from environment variables — nothing sensitive is committed. Either export them in your shell, or set them in your IDE's run configuration:

```bash
export DB_PASSWORD=<your local postgres password>
export JWT_SECRET=<any long random string>
```

`DB_URL` (`jdbc:postgresql://localhost:5432/neurosession_dev`) and `DB_USERNAME` (`postgres`) already default to sensible local values — override them only if your setup differs.

Alternatively, create `src/main/resources/application-local.yml` (already gitignored) with:

```yaml
spring:
  datasource:
    password: <your local postgres password>
app:
  jwt:
    secret: <any long random string>
```

and run with the `local` Spring profile active (`SPRING_PROFILES_ACTIVE=local`).

### 3. Run the app

```bash
./mvnw spring-boot:run
```

The app is served at **http://localhost:8080** — the static frontend and REST API are on the same origin, so there's nothing else to start.

### 4. Create an account and log in

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Doe","email":"jane@uliege.be","password":"changeme","role":"RESEARCHER"}'
```

Then sign in from the browser at `http://localhost:8080`.

---

## Environment variables

| Variable      | Required | Default (if any)                                   | Purpose                          |
|---------------|----------|------------------------------------------------------|-----------------------------------|
| `DB_URL`      | No       | `jdbc:postgresql://localhost:5432/neurosession_dev`  | Postgres JDBC URL                  |
| `DB_USERNAME` | No       | `postgres`                                            | Postgres username                  |
| `DB_PASSWORD` | **Yes**  | —                                                      | Postgres password                  |
| `JWT_SECRET`  | **Yes**  | —                                                      | Signing key for issued JWTs        |

---

## Project structure

```
neurosession/
├── pom.xml
└── src/main/
    ├── java/com/neurosession/neurosession/
    │   ├── config/SecurityConfig.java
    │   ├── controller/             # REST controllers
    │   ├── dto/                    # Request/response payloads
    │   ├── entity/                 # JPA entities
    │   ├── repository/             # Spring Data repositories
    │   ├── security/               # JWT filter, JWT util, UserDetails
    │   └── service/                # Business logic
    └── resources/
        ├── application.yml         # Config (secrets via env vars)
        └── static/
            ├── index.html          # SPA shell
            ├── app.js              # AngularJS routes + controllers
            ├── css/style.css
            └── views/              # login, dashboard, participants,
                                     # participant-detail, monitoring
```

---

## Security notes

- Passwords are hashed with BCrypt before storage — never stored or logged in plain text.
- Authentication is stateless JWT (no server-side session), validated on every request by `JwtAuthenticationFilter`.
- Participant data is scoped per researcher at the query level, not just hidden in the UI.
- No secrets are committed to this repository. `application.yml` reads `DB_PASSWORD` and `JWT_SECRET` from the environment; local overrides go in the gitignored `application-local.yml`.

---

## Known limitations / roadmap

- **LLM-assisted reporting** is not yet implemented — the login page marks it "in progress." When built, generated summaries will always require human review before use; they are a documentation aid, not a substitute for clinical or scientific judgment.
- No password-reset or email-verification flow yet.
- No automated tests beyond the default Spring Boot context-load test.
- The frontend is a small AngularJS app with no build tooling — fine for its current size, but would need a rethink if the feature set grows significantly.

---

## Credits

- **Study supervision**: Dr. Audrey Vanhaudenhuyse — GIGA Consciousness Lab, University of Liège
- **Development**: Moythry Manir Samia
