# ERP Microservices System Design

## 1. Purpose

This project is a portfolio-grade ERP backend simulation for a 2-4 member Java backend team. The system will model a small enterprise HR and attendance domain using Spring Boot microservices, API Gateway, Service Discovery, Docker, Kafka, PostgreSQL, CI/CD, Git Flow, Pull Requests, Code Review, and Jira-style issue tracking.

The main goal is not only to build features. The goal is to simulate how a real backend team designs, documents, implements, reviews, tests, and ships enterprise software.

## 2. Success Criteria

The project is successful when the team can demonstrate all of the following:

- Multiple independently deployable Spring Boot services.
- API Gateway routing all external traffic.
- Service discovery for internal service registration.
- JWT-based authentication and role-based authorization.
- Per-service database ownership.
- Kafka-based asynchronous communication for important domain events.
- Docker Compose local environment.
- OpenAPI documentation for service APIs.
- Database migrations with Flyway or Liquibase.
- Unit tests and integration tests.
- CI pipeline that builds and tests services on pull requests.
- PR-based collaboration with code review.
- Jira-style epics, stories, and acceptance criteria.
- Clear README, architecture diagrams, and interview-ready documentation.

## 3. Recommended MVP Scope

The MVP should include these services:

1. API Gateway
2. Discovery Server
3. Identity Service
4. Employee Service
5. Department Service
6. Attendance Service
7. Reporting Service
8. Notification Service

The MVP should not include payroll, complex leave approval workflows, document management, or advanced Kubernetes deployment. Those belong in version 2 after the core system is stable.

## 4. Architecture Overview

The system uses a microservices architecture with synchronous HTTP APIs for direct queries and commands, plus asynchronous Kafka events for cross-service notifications and reporting projections.

External clients call the API Gateway. The gateway validates routes and forwards requests to backend services. Backend services register with the Discovery Server. Each business service owns its own PostgreSQL database. Services do not directly read or write another service's database.

Kafka is used for domain events such as employee creation, department changes, attendance check-ins, attendance violations, and notification triggers. Reporting Service builds read models from Kafka events instead of joining across operational databases.

## 5. Target Technology Stack

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Security
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka or Consul
- Spring Data JPA
- PostgreSQL
- Flyway or Liquibase
- Apache Kafka
- Docker and Docker Compose
- OpenAPI / Swagger
- JUnit 5
- Mockito
- Testcontainers
- GitHub Actions
- Optional: Prometheus, Grafana, Loki, SonarQube

## 6. Repository Structure

The recommended repository layout is a single mono-repo:

```text
erp-system
├── api-gateway
├── discovery-server
├── identity-service
├── employee-service
├── department-service
├── attendance-service
├── reporting-service
├── notification-service
├── common-lib
├── docker-compose.yml
├── docs
│   ├── adr
│   ├── api
│   ├── diagrams
│   └── specs
└── .github
    └── workflows
```

The mono-repo is recommended for this team size because it keeps onboarding, local development, PR review, and CI configuration simpler.

## 7. Service Responsibilities

### 7.1 API Gateway

The API Gateway is the single external entry point.

Responsibilities:

- Route requests to backend services.
- Apply common CORS configuration.
- Validate or forward JWT tokens.
- Expose service paths under stable prefixes.
- Provide a central place for rate limiting in version 2 if the team adds public or high-traffic APIs.

Example routes:

```text
/api/auth/**          -> identity-service
/api/users/**         -> identity-service
/api/employees/**     -> employee-service
/api/departments/**   -> department-service
/api/attendance/**    -> attendance-service
/api/reports/**       -> reporting-service
```

### 7.2 Discovery Server

The Discovery Server allows services to register themselves and discover other services.

Responsibilities:

- Service registration.
- Service lookup.
- Local development support for dynamic service URLs.

### 7.3 Identity Service

Identity Service owns users, credentials, roles, and authentication.

Responsibilities:

- Create internal user accounts.
- Authenticate users.
- Issue JWT access tokens.
- Store password hashes.
- Manage user status.
- Manage roles.

Primary entities:

- User
- Role

Required roles:

- ADMIN
- HR_MANAGER
- DEPARTMENT_MANAGER
- EMPLOYEE

Key APIs:

```text
POST /api/auth/login
POST /api/users
GET /api/users/{id}
PATCH /api/users/{id}/status
PUT /api/users/{id}/roles
```

### 7.4 Department Service

Department Service owns organizational departments.

Responsibilities:

- Create departments.
- Update departments.
- Assign department managers.
- Maintain parent-child department relationships.
- Publish department events.

Primary entities:

- Department

Key APIs:

```text
POST /api/departments
GET /api/departments
GET /api/departments/{id}
PUT /api/departments/{id}
PATCH /api/departments/{id}/manager
```

### 7.5 Employee Service

Employee Service owns employee profiles and employment state.

Responsibilities:

- Create employee profile.
- Update employee profile.
- Assign employee to department.
- Assign employee manager.
- Change employee employment status.
- Search employees.
- Publish employee events.

Primary entities:

- Employee

Employee statuses:

- ACTIVE
- ON_LEAVE
- SUSPENDED
- TERMINATED

Key APIs:

```text
POST /api/employees
GET /api/employees
GET /api/employees/{id}
PUT /api/employees/{id}
PATCH /api/employees/{id}/department
PATCH /api/employees/{id}/status
```

### 7.6 Attendance Service

Attendance Service owns check-in, check-out, and attendance rules.

Responsibilities:

- Record check-in.
- Record check-out.
- Prevent duplicate check-in for the same employee and date.
- Calculate work duration.
- Detect late arrivals.
- Detect early leave.
- Generate monthly employee attendance summaries.
- Publish attendance events.

Primary entities:

- AttendanceRecord
- AttendancePolicy

Default business rules:

- Workday starts at 09:00.
- Check-in after 09:15 is LATE.
- Check-out before 16:00 is EARLY_LEAVE.
- Missing check-in for a required workday is ABSENT.
- One employee can have only one attendance record per date.

Key APIs:

```text
POST /api/attendance/check-in
POST /api/attendance/check-out
GET /api/attendance/employees/{employeeId}
GET /api/attendance/employees/{employeeId}/monthly-summary
```

### 7.7 Reporting Service

Reporting Service owns read-only reporting views.

Responsibilities:

- Consume employee, department, and attendance events.
- Build reporting projections.
- Provide department-level and employee-level reports.
- Avoid direct joins across service databases.

Primary read models:

- EmployeeReportView
- DepartmentReportView
- AttendanceMonthlyReportView

Key APIs:

```text
GET /api/reports/attendance/monthly
GET /api/reports/departments/{departmentId}/attendance
GET /api/reports/employees/status-summary
GET /api/reports/departments/headcount
```

### 7.8 Notification Service

Notification Service consumes events and stores notification records.

Responsibilities:

- Consume attendance violation events.
- Consume employee status events.
- Create notification records for HR or department managers.
- Simulate email delivery by storing and logging notifications.

Primary entities:

- Notification

Key APIs:

```text
GET /api/notifications
PATCH /api/notifications/{id}/read
```

## 8. Data Ownership Rules

Each service owns its own schema and database.

Rules:

- No service may directly query another service's database.
- Cross-service references use IDs, not foreign key constraints across databases.
- Services may call another service over HTTP for immediate validation.
- Services may consume Kafka events to keep local read models.
- Reporting must use projections or event-fed read models, not operational database joins.

Example:

Employee Service may store `departmentId`, but Department Service owns the department name and hierarchy.

## 9. Kafka Event Design

Kafka topic names should be stable and domain-oriented.

Recommended topics:

```text
identity.user-events
organization.department-events
hr.employee-events
attendance.attendance-events
notification.notification-events
```

Recommended event envelope:

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

Initial events:

- UserCreated
- UserDisabled
- DepartmentCreated
- DepartmentManagerAssigned
- EmployeeCreated
- EmployeeDepartmentChanged
- EmployeeStatusChanged
- AttendanceCheckedIn
- AttendanceCheckedOut
- AttendanceViolationDetected
- NotificationCreated

## 10. Security Model

Identity Service issues JWT tokens. The API Gateway and backend services should validate tokens.

Access rules:

- ADMIN can access all administrative APIs.
- HR_MANAGER can manage employees, departments, attendance, and reports.
- DEPARTMENT_MANAGER can view employees and attendance for their department.
- EMPLOYEE can view their own profile and attendance records.

Security requirements:

- Passwords must be hashed with BCrypt.
- Plain-text passwords must never be logged.
- JWT secret or signing keys must come from environment variables.
- Protected APIs must reject missing, invalid, or expired tokens.
- Services should use method-level authorization for sensitive actions.

## 11. API Standards

All APIs should follow common conventions.

Response format:

```json
{
  "data": {},
  "message": "Success",
  "timestamp": "2026-05-31T10:15:30Z"
}
```

Error response format:

```json
{
  "errorCode": "EMPLOYEE_NOT_FOUND",
  "message": "Employee was not found",
  "path": "/api/employees/123",
  "timestamp": "2026-05-31T10:15:30Z"
}
```

HTTP status conventions:

- 200 for successful reads and updates.
- 201 for successful creates.
- 204 for successful deletes or no-content updates.
- 400 for validation errors.
- 401 for unauthenticated requests.
- 403 for unauthorized requests.
- 404 for missing resources.
- 409 for business conflicts.
- 500 for unexpected server errors.

## 12. Database Standards

Each service should use migration scripts.

Rules:

- Use UUID primary keys unless there is a strong reason not to.
- Every table should include `created_at` and `updated_at`.
- Use optimistic locking with a `version` column for important mutable records.
- Use database constraints for required fields and uniqueness.
- Use indexes for common lookup fields.

Example tables:

Identity Service:

- users
- roles
- user_roles

Employee Service:

- employees

Department Service:

- departments

Attendance Service:

- attendance_records
- attendance_policies

Reporting Service:

- employee_report_views
- department_report_views
- attendance_monthly_report_views

Notification Service:

- notifications

## 13. Testing Strategy

Testing should be part of the definition of done.

Required test levels:

- Unit tests for business rules.
- Web layer tests for controllers.
- Repository tests where queries are non-trivial.
- Integration tests with Testcontainers for database behavior.
- Kafka integration tests for event producers and consumers.
- Security tests for protected endpoints.

Attendance Service should have the strongest test coverage because it contains the richest business rules.

Example acceptance test:

```text
Given an active employee
And the workday starts at 09:00
When the employee checks in at 09:20
Then the attendance record is created
And the record status contains LATE
And an AttendanceViolationDetected event is published
```

## 14. Team Workflow

The team should use Git Flow with pull requests.

Branches:

```text
main
develop
feature/ERP-123-short-name
release/1.0.0
hotfix/1.0.1
```

Pull request rules:

- Every change goes through a PR.
- At least one teammate must review each PR.
- CI must pass before merge.
- PR description must link to the Jira ticket.
- PR description must include testing evidence.
- Large features should be split into small PRs.

Recommended commit style:

```text
feat(identity): add login endpoint
fix(attendance): prevent duplicate check-in
test(employee): add employee creation integration test
docs(architecture): add service boundary decision
ci: add GitHub Actions build workflow
```

## 15. Jira Structure

Recommended epics:

- ERP-001 Platform Foundation
- ERP-002 Identity and Access Management
- ERP-003 Organization Management
- ERP-004 Employee Management
- ERP-005 Attendance Management
- ERP-006 Reporting
- ERP-007 Events and Notifications
- ERP-008 DevOps and Observability

Every story should include:

- User story
- Business rules
- API contract
- Database changes
- Kafka events
- Validation rules
- Acceptance criteria
- Test expectations

Example story:

```text
ERP-ATT-001: Employee check-in

As an employee,
I want to check in at the start of my workday,
so that my attendance can be recorded.

Acceptance criteria:
- A valid active employee can check in once per date.
- A duplicate check-in returns 409.
- A check-in after 09:15 is marked LATE.
- A successful check-in publishes AttendanceCheckedIn.
- A late check-in publishes AttendanceViolationDetected.
```

## 16. Milestone Roadmap

### Milestone 0: Team Setup

Duration: 2-3 days.

Deliverables:

- Repository initialized.
- README created.
- Branch strategy documented.
- PR template created.
- Issue template created.
- Jira board created.
- Definition of Done documented.

### Milestone 1: Platform Foundation

Duration: 1 week.

Deliverables:

- Discovery Server.
- API Gateway.
- Docker Compose with PostgreSQL and Kafka.
- Common response and error format.
- Base CI pipeline.

### Milestone 2: Identity Service

Duration: 1-2 weeks.

Deliverables:

- User model.
- Role model.
- Login API.
- JWT generation.
- JWT validation.
- Protected endpoint tests.

### Milestone 3: Department and Employee Services

Duration: 2 weeks.

Deliverables:

- Department CRUD.
- Department manager assignment.
- Employee CRUD.
- Employee department assignment.
- Employee status changes.
- Department and employee events.

### Milestone 4: Attendance Service

Duration: 2 weeks.

Deliverables:

- Check-in.
- Check-out.
- Duplicate check-in prevention.
- Late arrival detection.
- Early leave detection.
- Monthly attendance summary.
- Attendance events.

### Milestone 5: Reporting Service

Duration: 1 week.

Deliverables:

- Event consumers.
- Reporting projections.
- Monthly attendance report.
- Department headcount report.
- Employee status summary.

### Milestone 6: Notification Service

Duration: 1 week.

Deliverables:

- Event consumers.
- Notification records.
- Read/unread notification API.
- Attendance violation notification flow.

### Milestone 7: DevOps and Polish

Duration: 1-2 weeks.

Deliverables:

- Full Docker Compose startup.
- Improved CI pipeline.
- Integration test suite.
- Architecture diagrams.
- Postman collection.
- Final README.
- Interview notes.

## 17. Team Assignment Options

For 2 members:

- Member 1: Gateway, Discovery, Identity, Security, CI/CD.
- Member 2: Department, Employee, Attendance, Reporting.
- Both: specs, tests, PR reviews.

For 3 members:

- Member 1: Platform, Gateway, Discovery, Identity, Security.
- Member 2: Department and Employee Services.
- Member 3: Attendance, Reporting, Kafka consumers.

For 4 members:

- Member 1: Platform, DevOps, Gateway, Discovery.
- Member 2: Identity and Security.
- Member 3: Department and Employee Services.
- Member 4: Attendance, Reporting, Notification.

## 18. Architecture Decisions To Record

The team should create ADRs for these decisions:

- ADR-001: Mono-repo instead of multi-repo.
- ADR-002: PostgreSQL database per service.
- ADR-003: Kafka for asynchronous domain events.
- ADR-004: JWT-based authentication.
- ADR-005: Reporting via projections instead of cross-service joins.
- ADR-006: Docker Compose for local development.
- ADR-007: Git Flow with pull requests and protected branches.

## 19. Version 2 Ideas

After the MVP is complete, the team may add:

- Leave Management Service.
- Payroll Simulation Service.
- Approval Workflow Service.
- Audit Log Service.
- Document Service.
- Kubernetes deployment.
- Distributed tracing with OpenTelemetry.
- Contract tests with Spring Cloud Contract.

These should not be started until the MVP services are stable and documented.

## 20. Resume and Interview Evidence

The final project should provide:

- Public GitHub repository.
- Clean README with setup instructions.
- Architecture diagram.
- Service responsibility table.
- API documentation.
- Kafka event documentation.
- Screenshots of CI passing.
- Example PRs with code review comments.
- Test reports.
- Docker Compose startup instructions.

Example resume bullet:

```text
Designed and implemented a spec-driven HR ERP backend using Spring Boot microservices, Spring Cloud Gateway, Eureka, Kafka, PostgreSQL, Docker, GitHub Actions, JWT RBAC, OpenAPI, Flyway, and Testcontainers, simulating enterprise team workflows with Git Flow, Jira stories, pull requests, and code review.
```

## 21. Out Of Scope For MVP

The following are intentionally out of scope for the MVP:

- Frontend application.
- Payroll calculations.
- Real email delivery.
- Real SMS delivery.
- Advanced leave approval workflow.
- Kubernetes production deployment.
- Multi-tenant company support.
- Complex shift scheduling.
- Biometric attendance integration.

These exclusions keep the project achievable for a small team while preserving strong backend interview value.
