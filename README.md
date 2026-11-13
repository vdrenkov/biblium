# Biblium

Staff-facing REST API for managing a library catalogue, clients and borrowing orders.

## Features

- Manage authors, books and client contact details.
- Create borrowing orders, check available stock, filter loans by client or date and extend due dates.
- Administrator-managed staff accounts with ADMIN and LIBRARIAN permissions.
- HttpOnly JWT authentication cookies and CSRF protection.
- Transactional stock updates, database constraints and Liquibase schema migrations.

## Stack

Java 27, Spring Boot 4.1, Spring Security, Spring Data JPA, PostgreSQL, Liquibase and Maven 3.10.
Tests use JUnit, Mockito, MockMvc and PostgreSQL Testcontainers.

## Run locally

Install Java 27 and run an existing PostgreSQL server. Create the database once:

```sql
CREATE DATABASE biblium;
```

Set the required environment variables. PowerShell example:

```powershell
$env:SPRING_DATASOURCE_PASSWORD = '<database-password>'
$env:JWT_SECRET = '<random-secret-of-at-least-64-UTF-8-bytes>'
$env:APP_BOOTSTRAP_ADMIN_USERNAME = 'admin'
$env:APP_BOOTSTRAP_ADMIN_PASSWORD = '<strong-admin-password>'
$env:JWT_COOKIE_SECURE = 'false'
```

The default database URL is `jdbc:postgresql://localhost:5432/biblium`, with username `postgres`.
Override these with `SPRING_DATASOURCE_URL` and `SPRING_DATASOURCE_USERNAME` when needed.
The bootstrap credentials create the first administrator if that username does not exist;
remove them from the environment afterwards. Existing passwords are never overwritten.

```powershell
.\mvnw.cmd spring-boot:run
```

The API listens on `http://localhost:8080`. Liquibase applies migrations and Hibernate validates
the schema. Keep secure cookies enabled over HTTPS; `JWT_COOKIE_SECURE=false` is for local HTTP only.
Database credentials and signing secrets belong in the environment, not in tracked configuration.

## API

Import [the Postman collection](Biblium.postman_collection.json), set `baseUrl`, `username` and
`password`, then run `GetCsrfToken` and `Login`. Postman retains the authentication cookie and
includes the CSRF header on state-changing requests. Obtain a fresh CSRF token after logout.
Creation requests save their returned IDs for subsequent requests.

| Resource | Operations | Access |
| --- | --- | --- |
| `/csrf`, `/login` | Obtain a CSRF token; authenticate | Public |
| `/logout` | Clear the authentication cookie | CSRF-protected |
| `/users`, `/users/{id}` | Create and read staff accounts | ADMIN |
| `/roles`, `/roles/{id}` | Create and read roles | ADMIN |
| `/authors`, `/authors/{id}` | Create and read authors | ADMIN, LIBRARIAN |
| `/books`, `/books/{id}`, `/authors/{id}/books` | Create and read available books | ADMIN, LIBRARIAN |
| `/clients`, `/clients/{id}` | Create and read clients; delete a client | ADMIN, LIBRARIAN; deletion ADMIN only |
| `/orders`, `/orders/{id}`, `/clients/{id}/orders` | Create and read loans; extend due dates | ADMIN, LIBRARIAN |

Create staff through `POST /users`. Run `GetAllRoles` to obtain the librarian role ID, then set
`newUserPassword` before running `CreateUser`. Public registration is not available. Creating staff
does not change the administrator's current login. User and role responses include their IDs;
passwords are never returned.

For state-changing requests, send `X-XSRF-TOKEN` with the token returned by `/csrf` or the value of
the `XSRF-TOKEN` cookie, together with that cookie. Authentication uses the `BIBLIUM_AUTH` cookie.

Order date filters use `GET /orders?choice=1&date=2026-11-13`: choices 1–3 select issue date
equal/before/after; 4–6 select due date equal/before/after. Due-date extensions use
`PUT /orders/{id}?choice=1&period=7`: choices 1–3 mean days/weeks/months. Add `returnOld=true` to
return the previous order, or to return a deleted client from `DELETE /clients/{id}`.

## Verification

Run all unit and PostgreSQL integration tests, then package the application:

```powershell
.\mvnw.cmd clean verify
```

Integration tests require a running Docker-compatible engine and manage their own disposable
PostgreSQL database. Local development uses your existing PostgreSQL server; no Compose setup is
required. The test image is pinned by digest and scanned for known vulnerabilities.

Run unit tests alone with `.\mvnw.cmd test`. On Linux and macOS, use `./mvnw`.
GitHub Actions runs the full build and tests, then scans the packaged runtime dependencies with
Trivy. Dependabot checks Maven dependencies and GitHub Actions weekly.

## Limitations and acknowledgments

Borrowing decrements stock; a book-return workflow is not implemented. Logout clears the browser
cookie; an already-issued JWT remains valid until its one-hour expiry. Only ADMIN and LIBRARIAN
roles have application permissions.

This is a personal portfolio project by Valentin Drenkov and is not for redistribution.
