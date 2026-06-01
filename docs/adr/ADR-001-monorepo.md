# ADR-001: Use A Mono-Repo For The ERP Microservices Project

## Status

Accepted

## Date

2026-06-01

## Context

The project is a portfolio-grade ERP backend built by a 3-member Java backend team. The system will contain multiple Spring Boot services, including API Gateway, Discovery Server, Identity Service, Department Service, Employee Service, Attendance Service, Reporting Service, Notification Service, and a shared common library.

The team needs a structure that is easy to understand, easy to review, simple to run locally, and practical for learning professional collaboration workflows.

## Decision

Use a single mono-repo named `enterprise-hr-erp-microservices`.

Each service will live in its own top-level folder:

```text
api-gateway
discovery-server
identity-service
department-service
employee-service
attendance-service
reporting-service
notification-service
common-lib
```

Shared documentation, architecture decisions, API contracts, Jira planning docs, and team workflow docs will live under `docs`.

## Consequences

### Positive

- Easier onboarding for a small team.
- One repository contains all service code and documentation.
- Pull requests can show cross-service changes in one place.
- CI/CD configuration starts simpler.
- Local development with Docker Compose is easier to document.
- Architecture and service boundaries are easier to review together.

### Negative

- Repository size can grow over time.
- CI may need path-based optimization later.
- Teams must be disciplined about service ownership boundaries.
- A mono-repo does not mean services can share databases or bypass APIs.

## Rules

- Each service owns its source code and database schema.
- No service may directly read or write another service's database.
- Shared code must live in `common-lib` only when it is stable and genuinely shared.
- Documentation changes should be committed with the related feature when possible.
- Service boundaries must be reviewed in pull requests.
