# ADR-003: Use Docker Compose For Local Development

## Status

Accepted

## Date

2026-06-01

## Context

The ERP microservices project needs a local runtime that the full team can start consistently without manually installing and configuring every dependency on each machine.

The system includes multiple PostgreSQL databases and Kafka, and later milestones add several Spring Boot services. A reproducible local environment is important for development, testing, and demonstrations.

## Decision

Use Docker Compose as the local development infrastructure layer.

- Each service that owns data will get its own PostgreSQL container.
- Kafka will run as a shared message broker for event-driven communication.
- Docker Compose will provide a consistent local startup story for the team.

## Consequences

### Positive

- New team members can start the infrastructure with one command.
- Each service can keep its own database ownership boundary.
- Kafka is available locally for event publishing and consumption.
- The team can document the same environment used for development and demos.

### Negative

- The compose file will grow as services are added.
- Local startup depends on Docker being available and healthy on the developer machine.
- Developers may need to manage container lifecycle issues during setup.

## Rules

- Each business service must use its own PostgreSQL container and database name.
- Services must not connect directly to another service's database container.
- Kafka runs once as shared infrastructure for the whole platform.
- The compose file should remain readable and split by service responsibility as the system grows.
