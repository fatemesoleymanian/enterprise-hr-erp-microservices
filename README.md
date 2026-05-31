# Enterprise HR ERP Microservices

Enterprise HR ERP Microservices is a backend system for managing users, employees, departments, attendance, reports, and notifications using a microservices architecture.

The project is built with Spring Boot and focuses on practical backend engineering patterns such as API Gateway routing, service discovery, database-per-service ownership, Kafka-based event communication, JWT authentication, Dockerized local development, automated testing, and CI/CD.

## Table Of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Repository Structure](#repository-structure)
- [Getting Started](#getting-started)
- [API Routes](#api-routes)
- [Kafka Events](#kafka-events)
- [Testing](#testing)
- [Development Workflow](#development-workflow)
- [Documentation](#documentation)
- [Project Status](#project-status)
- [License](#license)

## Overview

This system models a small enterprise HR domain. It provides backend services for identity management, employee records, department organization, attendance tracking, event-driven reporting, and notifications.

The project uses a microservices architecture where each business service owns its data and communicates with other services through well-defined APIs and domain events.

## Features

- User authentication and role-based access control
- Employee profile management
- Department management
- Employee-to-department assignment
- Attendance check-in and check-out
- Late arrival and early leave detection
- Monthly attendance summaries
- Event-driven reporting projections
- Notification records for important HR events
- API Gateway as the single external entry point
- Service discovery for backend services
- PostgreSQL database per business service
- Kafka-based asynchronous messaging
- Docker Compose local infrastructure
- CI pipeline for build and test automation

## Architecture

External clients communicate with the system through the API Gateway. Backend services register with the Discovery Server. Each business service owns a separate PostgreSQL database. Kafka is used for asynchronous communication between services.

```text
Client
  |
  v
API Gateway
  |
  |-- Identity Service
  |-- Department Service
  |-- Employee Service
  |-- Attendance Service
  |-- Reporting Service
  `-- Notification Service

Discovery Server provides service registration.
Kafka carries domain events between services.
Each business service owns its own PostgreSQL database.
```

## Services

| Service | Responsibility |
| --- | --- |
| `api-gateway` | Routes external API traffic to backend services |
| `discovery-server` | Registers services and enables service discovery |
| `identity-service` | Manages users, roles, login, JWT, and authorization |
| `department-service` | Manages departments, hierarchy, and department managers |
| `employee-service` | Manages employee profiles, status, department assignment, and manager assignment |
| `attendance-service` | Manages check-in, check-out, attendance rules, and monthly summaries |
| `reporting-service` | Builds read-only reporting views from Kafka events |
| `notification-service` | Creates and stores notifications from domain events |
| `common-lib` | Contains shared API response and error response types |

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Data JPA
- PostgreSQL
- Flyway
- Apache Kafka
- Docker and Docker Compose
- OpenAPI / Swagger
- JUnit 5
- Mockito
- Testcontainers
- GitHub Actions

## Repository Structure

```text
erp-system
|-- api-gateway
|-- discovery-server
|-- identity-service
|-- department-service
|-- employee-service
|-- attendance-service
|-- reporting-service
|-- notification-service
|-- common-lib
|-- docs
|   |-- adr
|   |-- api
|   |-- diagrams
|   |-- specs
|   |-- jira
|   |-- team
|   `-- interview
|-- postman
|-- docker-compose.yml
|-- README.md
`-- .github
    `-- workflows
```

## Getting Started

### Prerequisites

- Java 21
- Maven or Maven Wrapper
- Docker
- Docker Compose
- Git

### Clone The Repository

```bash
git clone https://github.com/<your-org>/enterprise-hr-erp-microservices.git
cd enterprise-hr-erp-microservices
```

### Start Infrastructure

```bash
docker compose up -d
```

This starts the local PostgreSQL and Kafka infrastructure required by the services.

### Start Services

Start services in this order:

1. `discovery-server`
2. `api-gateway`
3. Business services:

```text
identity-service
department-service
employee-service
attendance-service
reporting-service
notification-service
```

From each service directory, run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell, use:

```powershell
.\mvnw spring-boot:run
```

## API Routes

The API Gateway exposes these route prefixes:

```text
/api/auth/**          -> identity-service
/api/users/**         -> identity-service
/api/departments/**   -> department-service
/api/employees/**     -> employee-service
/api/attendance/**    -> attendance-service
/api/reports/**       -> reporting-service
/api/notifications/** -> notification-service
```

## Kafka Events

Initial domain events:

- `UserCreated`
- `UserDisabled`
- `DepartmentCreated`
- `DepartmentManagerAssigned`
- `EmployeeCreated`
- `EmployeeDepartmentChanged`
- `EmployeeStatusChanged`
- `AttendanceCheckedIn`
- `AttendanceCheckedOut`
- `AttendanceViolationDetected`
- `NotificationCreated`

Example event envelope:

```json
{
  "eventId": "uuid",
  "eventType": "EmployeeCreated",
  "occurredAt": "2026-05-31T10:15:30Z",
  "version": 1,
  "producer": "employee-service",
  "correlationId": "uuid",
  "payload": {}
}
```

## Testing

Run tests from an individual service directory:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw test
```

Recommended test coverage:

- Unit tests for business rules
- Controller tests for API behavior
- Repository tests for persistence behavior
- Security tests for protected endpoints
- Integration tests with Testcontainers
- Kafka producer and consumer tests

## Development Workflow

This project follows a pull-request-based workflow.

Recommended branches:

- `main`: stable release branch
- `develop`: integration branch
- `feature/ERP-123-short-name`: feature branches
- `release/x.y.z`: release preparation
- `hotfix/x.y.z`: urgent fixes

Recommended commit style:

```text
feat(identity): add login endpoint
fix(attendance): prevent duplicate check-in
test(employee): add employee creation integration test
docs(architecture): add service boundary decision
ci: add GitHub Actions build workflow
```

## Documentation

Recommended documentation locations:

```text
docs/specs       Feature and service specifications
docs/api         API contracts
docs/adr         Architecture decision records
docs/diagrams    Architecture diagrams
docs/team        Team workflow standards
docs/interview   Project walkthrough and resume notes
postman          API collection
```

## Project Status

This project is under active development.

Planned MVP modules:

- API Gateway
- Discovery Server
- Identity Service
- Department Service
- Employee Service
- Attendance Service
- Reporting Service
- Notification Service

Future improvements may include leave management, payroll simulation, audit logging, distributed tracing, contract testing, and Kubernetes deployment.

## License

This project is intended for educational and portfolio use. Add a license file before using it in a public or commercial context.
