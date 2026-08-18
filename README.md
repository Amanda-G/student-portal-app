# student-portal-app

Student portal to manage students and courses. Students can be enrolled in multiple courses, courses
can have multiple students.

## Tech stack

- Backend: Java 25, Spring Boot 4.1, Spring Data JPA, H2 (in-memory)
- Frontend: Angular 21, Angular Material, RxJS
- Tests: JUnit 5 + Mockito, TestRestTemplate integration tests, vitest for Angular

## Prerequisites

- JDK 25 (gradle toolchain will complain if missing)
- Node 20+ and npm

DB is embedded H2, no local install needed.

## Running the backend

```bash
cd studentportal
./gradlew bootRun
```

Runs on http://localhost:8080. The database is seeded with 3 students and 4 courses on startup
(data.sql). Since H2 is in-memory, restarting resets the data.

H2 console: http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:studentportal`
- User: `sa`, empty password

## Running the frontend

```bash
cd student-portal-web
npm install
npm start
```

Runs on http://localhost:4200. Backend must be running on 8080 (CORS is configured for localhost:
4200).

## Tests

```bash
cd studentportal
./gradlew test
```

Covers service layer unit tests (Mockito) and API integration tests against a running app instance
(TestRestTemplate).

Frontend:

```bash
cd student-portal-web
npm test
```

## API

postman collection is in repo
at [student-portal.postman_collection.json](student-portal.postman_collection.json)

Students:

| Method | Path                                  | Notes                                  |
|--------|---------------------------------------|----------------------------------------|
| GET    | /api/students                         | optional `?search=` on first/last name |
| GET    | /api/students/{id}                    |                                        |
| POST   | /api/students                         | 201, validates fields                  |
| PUT    | /api/students/{id}                    | full update                            |
| DELETE | /api/students/{id}                    | also removes enrollments               |
| POST   | /api/students/{id}/courses/{courseId} | enroll                                 |
| DELETE | /api/students/{id}/courses/{courseId} | unenroll                               |

Courses:

| Method | Path              | Notes                               |
|--------|-------------------|-------------------------------------|
| GET    | /api/courses      | optional `?search=` on code/name    |
| GET    | /api/courses/{id} |                                     |
| POST   | /api/courses      | course code must be unique          |
| PUT    | /api/courses/{id} |                                     |
| DELETE | /api/courses/{id} | blocked while students are enrolled |

Validation errors come back as 400 with a `fieldErrors` map. Everything else unexpected is a 500
with a generic message.

A Postman collection with all endpoints (including the error cases) is
in [student-portal.postman_collection.json](student-portal.postman_collection.json) - import it and
hit send, baseUrl is already set to localhost:8080.

## Assumptions made

- No `RestTemplate` in the main app - there is no service to service communication here, so it's
  used where it actually fits: `TestRestTemplate` in the integration tests to call url's from test.
- `PUT` instead of `PATCH` - the edit forms always send the full object, so full replacement is the
  honest verb. No partial update use case.
- Deleting a course with enrolled students is blocked on the backend rather than cascading silently.
- Direct `@ManyToMany` join table, no separate Enrollment entity - there are no extra fields (grade,
  semester etc) to justify one yet.
- Search is done backend side with a query param, partial and case insensitive.
- No auth/pagination 
