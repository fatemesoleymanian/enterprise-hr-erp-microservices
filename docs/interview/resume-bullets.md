# Resume Bullets

Select the three to five bullets that best match the target backend or Java role.

## Developer C Contributions

- Built Attendance, Reporting, and Notification microservices with Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL, and Flyway in a database-per-service architecture.
- Implemented attendance check-in and check-out workflows, late-arrival and early-leave policy evaluation, employee attendance history, and monthly summaries with validation and conflict handling.
- Developed Kafka consumers for `department.events`, `employee.events`, and `attendance.events` that build local reporting projections without cross-service database joins.
- Added idempotent event processing with persisted event IDs and per-attendance-record contributions to prevent duplicate report counts and notifications during Kafka redelivery.
- Created event-driven HR notifications for attendance violations and employee status changes, including filtered retrieval and mark-as-read REST APIs.
- Verified business rules, service behavior, and REST contracts with JUnit 5, Mockito, and Spring MVC tests across the three owned services.
- Containerized the three services with Java 21 multi-stage Docker builds and documented their API workflows through a shared Postman collection and Mermaid architecture diagrams.

## Team And Project Contributions

- Collaborated in a three-developer, spec-driven workflow using Jira-style tickets, feature branches, pull requests, code review, conventional commits, and GitHub Actions CI.
- Contributed to an HR ERP platform using Spring Cloud Gateway, Eureka service discovery, Kafka messaging, PostgreSQL, Docker Compose, JWT-based security, OpenAPI contracts, and independently owned service databases.
- Documented asynchronous event flows, eventual consistency, idempotency, and service data ownership to make architecture decisions reviewable by the team.
