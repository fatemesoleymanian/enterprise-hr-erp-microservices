# برنامه پیاده سازی ERP مبتنی بر Microservices

> **برای اجرا توسط agentic workers:** هنگام اجرای این برنامه باید از `superpowers:subagent-driven-development` یا `superpowers:executing-plans` استفاده شود. Step ها با checkbox نوشته شده اند تا قابل پیگیری باشند.

**هدف:** ساخت یک HR ERP Backend به صورت spec-driven با Spring Boot Microservices، API Gateway، Service Discovery، PostgreSQL، Kafka، Docker، CI/CD، تست و استانداردهای تیمی.

**معماری:** پروژه به صورت mono-repo ساخته می شود و شامل چند سرویس Spring Boot مستقل است. همه ترافیک خارجی از Spring Cloud Gateway وارد می شود، سرویس ها در Eureka ثبت می شوند، هر سرویس تجاری دیتابیس PostgreSQL خودش را دارد و Kafka برای domain event هایی استفاده می شود که Reporting و Notification مصرف می کنند.

**Tech Stack:** Java 21، Spring Boot 3.x، Spring Cloud Gateway، Spring Cloud Netflix Eureka، Spring Security، Spring Data JPA، PostgreSQL، Flyway، Kafka، Docker Compose، OpenAPI، JUnit 5، Mockito، Testcontainers، GitHub Actions.

---

## اصول پیاده سازی

- Spec-first کار کنید: هر سرویس قبل از کدنویسی باید requirements، API contract، database design، event design و acceptance criteria داشته باشد.
- Pull Request ها کوچک باشند: هر PR فقط یک feature یا تغییر infrastructure مشخص را پوشش دهد.
- از Git Flow استفاده شود: `main`، `develop`، `feature/ERP-123-short-name`، `release/x.y.z`، `hotfix/x.y.z`.
- هیچ سرویس نباید دیتابیس سرویس دیگر را مستقیم بخواند.
- برای Reporting از event-driven read model استفاده شود.
- تست ها قبل از پیاده سازی یا همزمان با آن نوشته شوند.
- MVP روی identity، organization، employee، attendance، reporting و notification متمرکز بماند.

## ساختار Repository هدف

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

## تقسیم کار تیمی

برای 2 نفر:

- Developer A: platform، gateway، discovery، identity، security، CI/CD.
- Developer B: department، employee، attendance، reporting، notification.
- هر دو نفر: PR review، spec، test و نگهداری مستندات.

برای 3 نفر:

- Developer A: platform، gateway، discovery، identity، security.
- Developer B: department و employee services.
- Developer C: attendance، reporting، Kafka consumers، notification.

برای 4 نفر:

- Developer A: platform، DevOps، gateway، discovery، CI/CD.
- Developer B: identity و security.
- Developer C: department و employee services.
- Developer D: attendance، reporting، notification، Kafka.

## نقشه Epic های Jira

```text
ERP-001 Platform Foundation
ERP-002 Identity and Access Management
ERP-003 Organization Management
ERP-004 Employee Management
ERP-005 Attendance Management
ERP-006 Reporting
ERP-007 Events and Notifications
ERP-008 DevOps and Observability
ERP-009 Documentation and Interview Evidence
```

---

## Milestone 0: آماده سازی تیم و Repository

**هدف:** قبل از شروع کدنویسی، سیستم کاری تیم مشخص شود.

**Owner:** همه اعضای تیم.

**مدت:** 2 تا 3 روز.

**فایل ها:**

- Create: `README.md`
- Create: `docs/team/branching-strategy.md`
- Create: `docs/team/definition-of-done.md`
- Create: `docs/team/code-review-guidelines.md`
- Create: `docs/team/commit-convention.md`
- Create: `docs/jira/epics-and-stories.md`
- Create: `.github/pull_request_template.md`
- Create: `.github/ISSUE_TEMPLATE/feature_request.md`
- Create: `.github/ISSUE_TEMPLATE/bug_report.md`
- Create: `docs/adr/ADR-001-monorepo.md`

### Task 0.1: استانداردهای Repository

- [ ] Repository با نام `erp-system` در GitHub ساخته شود.
- [ ] Branch های `main` و `develop` ساخته شوند.
- [ ] روی `main` قوانین زیر فعال شود:

```text
Require pull request before merging.
Require at least 1 approval.
Require status checks to pass.
Block force pushes.
Block branch deletion.
```

- [ ] روی `develop` قوانین زیر فعال شود:

```text
Require pull request before merging.
Require at least 1 approval.
Require status checks to pass.
```

- [ ] فایل `docs/team/branching-strategy.md` ساخته شود و Git Flow را توضیح دهد:

```markdown
# Branching Strategy

We use Git Flow.

Branches:

- `main`: production-ready releases only.
- `develop`: integration branch for completed features.
- `feature/ERP-123-short-name`: feature work.
- `release/1.0.0`: release stabilization.
- `hotfix/1.0.1`: urgent production fixes.

Rules:

- No direct commits to `main`.
- No direct commits to `develop`.
- Every change must be reviewed through a pull request.
- Feature branches must be created from `develop`.
- Pull requests must reference a Jira ticket.
```

- [ ] Commit:

```bash
git add docs/team/branching-strategy.md
git commit -m "docs(team): add branching strategy"
```

### Task 0.2: Definition of Done و Code Review

- [ ] فایل `docs/team/definition-of-done.md` ساخته شود:

```markdown
# Definition Of Done

A ticket is done only when:

- Requirements are documented.
- API behavior is documented when an endpoint changes.
- Database migration is included when schema changes.
- Unit tests cover business rules.
- Integration tests cover database or Kafka behavior when relevant.
- OpenAPI documentation is updated.
- Docker startup still works.
- Pull request has at least one approval.
- CI pipeline passes.
- Reviewer comments are resolved.
```

- [ ] فایل `docs/team/code-review-guidelines.md` ساخته شود:

```markdown
# Code Review Guidelines

Reviewers check:

- Correctness of business behavior.
- Service boundary violations.
- Security issues.
- Missing tests.
- Database migration safety.
- Error handling.
- API contract consistency.
- Naming and readability.

Reviewers should ask questions before requesting large rewrites.
```

- [ ] فایل `.github/pull_request_template.md` ساخته شود و شامل Jira Ticket، Summary، Type، Testing Evidence و Checklist باشد.

- [ ] Commit:

```bash
git add docs/team .github/pull_request_template.md
git commit -m "docs(team): add definition of done and PR template"
```

### Task 0.3: استاندارد Story در Jira

- [ ] فایل `docs/jira/epics-and-stories.md` ساخته شود.
- [ ] Epic ها در آن ثبت شوند.
- [ ] یک نمونه Story کامل مثل `ERP-ATT-001 Employee check-in` در آن قرار گیرد.
- [ ] هر Story باید شامل این موارد باشد:

```text
User story
Business rules
API contract
Database changes
Events
Acceptance criteria
Test expectations
```

- [ ] Commit:

```bash
git add docs/jira/epics-and-stories.md
git commit -m "docs(jira): add epics and story quality standard"
```

---

## Milestone 1: Platform Foundation

**هدف:** زیرساخت اولیه اجرا، Gateway، Discovery، Docker و CI آماده شود.

**Owner:** Platform developer.

**مدت:** 1 هفته.

**فایل ها:**

- Create: `discovery-server`
- Create: `api-gateway`
- Create: `common-lib`
- Create: `docker-compose.yml`
- Create: `.github/workflows/ci.yml`
- Create: `docs/adr/ADR-002-service-discovery.md`
- Create: `docs/adr/ADR-003-docker-compose-local-dev.md`

### Task 1.1: ساخت سرویس های پایه

- [ ] `discovery-server` با Spring Initializr ساخته شود:

```text
Project: Maven
Language: Java
Spring Boot: 3.x
Java: 21
Dependencies: Eureka Server, Spring Boot Actuator
Group: com.example.erp
Artifact: discovery-server
```

- [ ] `api-gateway` با Spring Initializr ساخته شود:

```text
Dependencies: Spring Cloud Gateway, Eureka Discovery Client, Spring Boot Actuator, Spring Security
```

- [ ] `common-lib` به عنوان Maven module برای DTO های مشترک و error response ساخته شود.

- [ ] Commit:

```bash
git add discovery-server api-gateway common-lib
git commit -m "feat(platform): scaffold discovery gateway and common library"
```

### Task 1.2: تنظیم Discovery Server

- [ ] در `discovery-server/src/main/resources/application.yml` پورت `8761` و نام `discovery-server` تنظیم شود.
- [ ] در main application از `@EnableEurekaServer` استفاده شود.
- [ ] تست اجرا شود:

```bash
cd discovery-server
./mvnw test
```

Expected:

```text
BUILD SUCCESS
```

- [ ] Commit:

```bash
git add discovery-server
git commit -m "feat(platform): configure discovery server"
```

### Task 1.3: تنظیم Gateway Routes

- [ ] در `api-gateway/src/main/resources/application.yml` route های زیر تعریف شوند:

```text
/api/auth/**          -> identity-service
/api/users/**         -> identity-service
/api/departments/**   -> department-service
/api/employees/**     -> employee-service
/api/attendance/**    -> attendance-service
/api/reports/**       -> reporting-service
/api/notifications/** -> notification-service
```

- [ ] تست اجرا شود:

```bash
cd api-gateway
./mvnw test
```

- [ ] Commit:

```bash
git add api-gateway
git commit -m "feat(platform): configure gateway routes"
```

### Task 1.4: افزودن Common API Response

- [ ] در `common-lib` این type ها ساخته شوند:

```text
ApiResponse<T>
ErrorResponse
```

- [ ] قرارداد response موفق:

```json
{
  "data": {},
  "message": "Success",
  "timestamp": "2026-05-31T10:15:30Z"
}
```

- [ ] قرارداد error:

```json
{
  "errorCode": "EMPLOYEE_NOT_FOUND",
  "message": "Employee was not found",
  "path": "/api/employees/123",
  "timestamp": "2026-05-31T10:15:30Z"
}
```

- [ ] Commit:

```bash
git add common-lib
git commit -m "feat(common): add standard API response types"
```

### Task 1.5: Docker Compose زیرساخت

- [ ] در `docker-compose.yml` container های زیر تعریف شوند:

```text
postgres-identity       -> port 5433
postgres-department     -> port 5434
postgres-employee       -> port 5435
postgres-attendance     -> port 5436
postgres-reporting      -> port 5437
postgres-notification   -> port 5438
kafka                   -> port 9092
```

- [ ] اجرا:

```bash
docker compose up -d
docker compose ps
```

Expected:

```text
All postgres containers are running.
Kafka container is running.
```

- [ ] Commit:

```bash
git add docker-compose.yml
git commit -m "feat(platform): add local docker compose infrastructure"
```

### Task 1.6: CI اولیه

- [ ] فایل `.github/workflows/ci.yml` ساخته شود.
- [ ] در ابتدا سرویس های زیر build/test شوند:

```text
discovery-server
api-gateway
common-lib
```

- [ ] Commit:

```bash
git add .github/workflows/ci.yml
git commit -m "ci: add initial service build workflow"
```

---

## Milestone 2: Identity And Access Management

**هدف:** پیاده سازی login، user management، JWT و role-based access.

**Owner:** Security developer.

**مدت:** 1 تا 2 هفته.

**فایل ها:**

- Create: `identity-service`
- Create: `docs/specs/identity/requirements.md`
- Create: `docs/specs/identity/api-contract.yaml`
- Create: `docs/specs/identity/database-schema.md`
- Create: `docs/specs/identity/events.md`
- Create: `docs/specs/identity/acceptance-tests.md`

### Task 2.1: Spec سرویس Identity

- [ ] requirements نوشته شود:

```text
Identity Service owns users, credentials, roles, and authentication.
Passwords are stored only as BCrypt hashes.
Disabled users cannot log in.
Login returns a JWT access token.
Only ADMIN can create users and change roles.
```

- [ ] acceptance test ها برای login موفق، login کاربر disabled و create user توسط admin نوشته شوند.

- [ ] Commit:

```bash
git add docs/specs/identity
git commit -m "docs(identity): add service spec"
```

### Task 2.2: ساخت Identity Service

- [ ] سرویس با dependency های زیر ساخته شود:

```text
Spring Web, Spring Security, Spring Data JPA, PostgreSQL Driver, Flyway,
Eureka Discovery Client, Validation, Spring Boot Actuator,
Spring for Apache Kafka, Springdoc OpenAPI
```

- [ ] service name برابر `identity-service`، port برابر `8081` و database port برابر `5433` باشد.
- [ ] Kafka روی `localhost:9092` تنظیم شود.

- [ ] Commit:

```bash
git add identity-service
git commit -m "feat(identity): scaffold service"
```

### Task 2.3: مدل داده Identity

- [ ] migration برای جدول های زیر ساخته شود:

```text
users
roles
user_roles
```

- [ ] index های لازم روی `email` و `status` ایجاد شود.
- [ ] Entity و Repository های زیر ساخته شوند:

```text
User
UserStatus
Role
UserRepository
RoleRepository
```

- [ ] تست:

```bash
cd identity-service
./mvnw test
```

- [ ] Commit:

```bash
git add identity-service
git commit -m "feat(identity): add user and role persistence model"
```

### Task 2.4: Login و JWT

- [ ] DTO های زیر ساخته شوند:

```text
LoginRequest(email, password)
LoginResponse(accessToken, tokenType, expiresInMinutes)
```

- [ ] کلاس های زیر پیاده سازی شوند:

```text
AuthController
AuthService
JwtService
SecurityConfig
```

- [ ] تست ها:

```text
AuthServiceTest verifies active user login succeeds.
AuthServiceTest verifies disabled user login fails.
JwtServiceTest verifies generated token contains subject and roles.
```

- [ ] Commit:

```bash
git add identity-service
git commit -m "feat(identity): add login and JWT issuing"
```

### Task 2.5: User Management API

- [ ] endpoint های زیر پیاده سازی شوند:

```text
POST /api/users
GET /api/users/{id}
PATCH /api/users/{id}/status
PUT /api/users/{id}/roles
```

- [ ] event های زیر publish شوند:

```text
UserCreated
UserDisabled
UserRolesChanged
```

- [ ] تست ها:

```text
ADMIN can create user.
EMPLOYEE cannot create user.
Duplicate email returns 409.
Missing required fields return 400.
```

- [ ] Commit:

```bash
git add identity-service docs/specs/identity
git commit -m "feat(identity): add user management APIs"
```

---

## Milestone 3: Department Service

**هدف:** مدیریت دپارتمان ها و مدیر دپارتمان.

**Owner:** Organization developer.

**مدت:** 1 هفته.

### Task 3.1: Spec سرویس Department

- [ ] قوانین زیر مستند شوند:

```text
Department name must be unique.
A department can have one parent department.
A department can have one manager user id.
Deleting departments is not part of MVP.
Only ADMIN and HR_MANAGER can create or update departments.
```

- [ ] event های زیر مستند شوند:

```text
DepartmentCreated
DepartmentManagerAssigned
```

- [ ] Commit:

```bash
git add docs/specs/department
git commit -m "docs(department): add service spec"
```

### Task 3.2: ساخت Department Service

- [ ] سرویس با Spring Web، JPA، PostgreSQL، Flyway، Eureka، Validation، Actuator، Kafka و OpenAPI ساخته شود.
- [ ] service name برابر `department-service`، port برابر `8082` و database port برابر `5434` باشد.
- [ ] جدول `departments` با فیلدهای زیر ساخته شود:

```text
id, name, description, parent_department_id, manager_user_id,
created_at, updated_at, version
```

- [ ] Commit:

```bash
git add department-service
git commit -m "feat(department): scaffold service and schema"
```

### Task 3.3: Department API

- [ ] endpoint ها:

```text
POST /api/departments
GET /api/departments
GET /api/departments/{id}
PUT /api/departments/{id}
PATCH /api/departments/{id}/manager
```

- [ ] تست ها:

```text
Create department returns 201.
Duplicate department name returns 409.
Assign manager updates managerUserId.
Missing department returns 404.
```

- [ ] Commit:

```bash
git add department-service docs/specs/department
git commit -m "feat(department): add department management APIs"
```

---

## Milestone 4: Employee Service

**هدف:** مدیریت پروفایل کارمند، انتساب به دپارتمان و وضعیت استخدامی.

**Owner:** HR developer.

**مدت:** 1 هفته.

### Task 4.1: Spec سرویس Employee

- [ ] قوانین زیر مستند شوند:

```text
Employee number must be unique.
Employee email must be unique.
Employee status must be ACTIVE, ON_LEAVE, SUSPENDED, or TERMINATED.
Employee can reference a department by departmentId.
Employee can reference a manager by managerEmployeeId.
Only ADMIN and HR_MANAGER can create or update employees.
```

- [ ] Commit:

```bash
git add docs/specs/employee
git commit -m "docs(employee): add service spec"
```

### Task 4.2: ساخت Employee Service

- [ ] service name برابر `employee-service`، port برابر `8083` و database port برابر `5435` باشد.
- [ ] جدول `employees` با فیلدهای زیر ساخته شود:

```text
id, user_id, employee_number, first_name, last_name, email,
job_title, department_id, manager_employee_id, status, hire_date,
created_at, updated_at, version
```

- [ ] Commit:

```bash
git add employee-service
git commit -m "feat(employee): scaffold service and schema"
```

### Task 4.3: Employee API

- [ ] endpoint ها:

```text
POST /api/employees
GET /api/employees
GET /api/employees/{id}
PUT /api/employees/{id}
PATCH /api/employees/{id}/department
PATCH /api/employees/{id}/status
```

- [ ] event ها:

```text
EmployeeCreated
EmployeeDepartmentChanged
EmployeeStatusChanged
```

- [ ] تست ها:

```text
Create employee returns 201.
Duplicate employee number returns 409.
Changing department publishes EmployeeDepartmentChanged.
Changing status publishes EmployeeStatusChanged.
Missing employee returns 404.
```

- [ ] Commit:

```bash
git add employee-service docs/specs/employee
git commit -m "feat(employee): add employee management APIs"
```

---

## Milestone 5: Attendance Service

**هدف:** پیاده سازی check-in، check-out، قوانین حضور و غیاب و summary ماهانه.

**Owner:** Attendance developer.

**مدت:** 2 هفته.

### Task 5.1: Spec سرویس Attendance

- [ ] قوانین زیر مستند شوند:

```text
One employee can have only one attendance record per date.
Workday starts at 09:00.
Check-in after 09:15 is LATE.
Check-out before 16:00 is EARLY_LEAVE.
Check-out requires an existing check-in for the same date.
Monthly summary includes present days, late days, early leave days, absent days, and total worked minutes.
```

- [ ] Acceptance test های check-in موفق، duplicate check-in و late check-in نوشته شوند.

- [ ] Commit:

```bash
git add docs/specs/attendance
git commit -m "docs(attendance): add service spec"
```

### Task 5.2: ساخت Attendance Service

- [ ] service name برابر `attendance-service`، port برابر `8084` و database port برابر `5436` باشد.
- [ ] جدول های زیر ساخته شوند:

```text
attendance_policies
attendance_records
```

- [ ] constraint یکتا برای `(employee_id, attendance_date)` اضافه شود.

- [ ] Commit:

```bash
git add attendance-service
git commit -m "feat(attendance): scaffold service and schema"
```

### Task 5.3: Attendance Rules با تست

- [ ] unit test های زیر نوشته شوند:

```text
09:00 check-in returns PRESENT.
09:15 check-in returns PRESENT.
09:16 check-in returns LATE.
15:59 check-out returns EARLY_LEAVE.
16:00 check-out does not return EARLY_LEAVE.
```

- [ ] کلاس های زیر پیاده سازی شوند:

```text
AttendancePolicyEvaluator
AttendanceStatus
AttendanceRecord
AttendanceRecordRepository
```

- [ ] تست:

```bash
cd attendance-service
./mvnw test
```

- [ ] Commit:

```bash
git add attendance-service
git commit -m "feat(attendance): add attendance policy evaluation"
```

### Task 5.4: Check-in، Check-out و Summary

- [ ] endpoint ها:

```text
POST /api/attendance/check-in
POST /api/attendance/check-out
GET /api/attendance/employees/{employeeId}
GET /api/attendance/employees/{employeeId}/monthly-summary
```

- [ ] event ها:

```text
AttendanceCheckedIn
AttendanceCheckedOut
AttendanceViolationDetected
```

- [ ] تست ها:

```text
First check-in returns 201.
Duplicate check-in returns 409.
Check-out without check-in returns 409.
Late check-in publishes AttendanceViolationDetected.
Check-out calculates worked minutes.
Monthly summary returns aggregated attendance counts.
```

- [ ] Commit:

```bash
git add attendance-service docs/specs/attendance
git commit -m "feat(attendance): add check-in check-out and summaries"
```

---

## Milestone 6: Reporting Service

**هدف:** ساخت read model های event-fed و ارائه API گزارش.

**Owner:** Reporting developer.

**مدت:** 1 هفته.

### Task 6.1: Spec سرویس Reporting

- [ ] قوانین زیر مستند شوند:

```text
Reporting Service must not query operational service databases.
Reporting Service consumes Kafka events and updates local read models.
Reports may be eventually consistent.
Reports expose department headcount, employee status summary, and monthly attendance summary.
```

- [ ] Commit:

```bash
git add docs/specs/reporting
git commit -m "docs(reporting): add service spec"
```

### Task 6.2: ساخت Reporting Service و Projection ها

- [ ] service name برابر `reporting-service`، port برابر `8085` و database port برابر `5437` باشد.
- [ ] جدول های زیر ساخته شوند:

```text
employee_report_views
department_report_views
attendance_monthly_report_views
```

- [ ] Commit:

```bash
git add reporting-service
git commit -m "feat(reporting): scaffold service and projections"
```

### Task 6.3: مصرف Event و API گزارش

- [ ] event های زیر consume شوند:

```text
DepartmentCreated
DepartmentManagerAssigned
EmployeeCreated
EmployeeDepartmentChanged
EmployeeStatusChanged
AttendanceCheckedIn
AttendanceCheckedOut
AttendanceViolationDetected
```

- [ ] endpoint ها:

```text
GET /api/reports/attendance/monthly
GET /api/reports/departments/{departmentId}/attendance
GET /api/reports/employees/status-summary
GET /api/reports/departments/headcount
```

- [ ] تست ها:

```text
EmployeeCreated creates employee projection.
EmployeeDepartmentChanged updates department in employee projection.
DepartmentCreated creates department projection.
AttendanceCheckedOut updates monthly attendance projection.
Headcount report counts active employees by department.
```

- [ ] Commit:

```bash
git add reporting-service docs/specs/reporting
git commit -m "feat(reporting): add event projections and report APIs"
```

---

## Milestone 7: Notification Service

**هدف:** ساخت notification از روی event های مهم.

**Owner:** Notification developer.

**مدت:** 1 هفته.

### Task 7.1: Spec سرویس Notification

- [ ] قوانین زیر مستند شوند:

```text
Attendance violations create HR notifications.
Employee status changes create HR notifications.
Notifications can be read or unread.
Real email and SMS delivery are outside MVP.
```

- [ ] Commit:

```bash
git add docs/specs/notification
git commit -m "docs(notification): add service spec"
```

### Task 7.2: ساخت Notification Service

- [ ] service name برابر `notification-service`، port برابر `8086` و database port برابر `5438` باشد.
- [ ] جدول `notifications` با فیلدهای زیر ساخته شود:

```text
id, recipient_role, recipient_user_id, type, title, body,
read, created_at, read_at, version
```

- [ ] Commit:

```bash
git add notification-service
git commit -m "feat(notification): scaffold service and schema"
```

### Task 7.3: مصرف Event و Notification API

- [ ] event های زیر consume شوند:

```text
AttendanceViolationDetected
EmployeeStatusChanged
```

- [ ] endpoint ها:

```text
GET /api/notifications
PATCH /api/notifications/{id}/read
```

- [ ] تست ها:

```text
AttendanceViolationDetected creates unread HR notification.
EmployeeStatusChanged creates unread HR notification.
Mark notification read sets read true and readAt.
Missing notification returns 404.
```

- [ ] Commit:

```bash
git add notification-service docs/specs/notification
git commit -m "feat(notification): add event-driven notifications"
```

---

## Milestone 8: Integration، DevOps و Polish

**هدف:** کل سیستم راحت اجرا، تست، review و ارائه شود.

**Owner:** همه اعضای تیم.

**مدت:** 1 تا 2 هفته.

### Task 8.1: افزودن همه سرویس ها به Docker Compose

- [ ] سرویس های زیر به `docker-compose.yml` اضافه شوند:

```text
discovery-server
api-gateway
identity-service
department-service
employee-service
attendance-service
reporting-service
notification-service
```

- [ ] dependency ها مشخص شوند:

```text
Gateway depends on discovery-server.
Business services depend on discovery-server, Kafka, and their own PostgreSQL container.
Reporting and notification depend on Kafka and their own PostgreSQL container.
```

- [ ] اجرا:

```bash
docker compose up --build
```

Expected:

```text
Discovery Server starts on 8761.
Gateway starts on 8080.
Business services register with Eureka.
PostgreSQL containers are healthy.
Kafka is reachable by event producers and consumers.
```

- [ ] Commit:

```bash
git add docker-compose.yml
git commit -m "devops: run full system with docker compose"
```

### Task 8.2: کامل کردن CI

- [ ] matrix در `.github/workflows/ci.yml` شامل همه سرویس ها شود:

```text
discovery-server
api-gateway
common-lib
identity-service
department-service
employee-service
attendance-service
reporting-service
notification-service
```

- [ ] Commit:

```bash
git add .github/workflows/ci.yml
git commit -m "ci: build and test all services"
```

### Task 8.3: Architecture Diagrams

- [ ] فایل `docs/diagrams/system-context.md` ساخته شود و ارتباط client، gateway، سرویس ها و Kafka را نشان دهد.
- [ ] فایل `docs/diagrams/service-communication.md` ساخته شود و sync/async communication و database ownership را توضیح دهد.

- [ ] Commit:

```bash
git add docs/diagrams
git commit -m "docs(architecture): add system diagrams"
```

### Task 8.4: مدارک مصاحبه و رزومه

- [ ] فایل `docs/interview/project-walkthrough.md` ساخته شود.
- [ ] demo flow شامل login، ایجاد department، ایجاد employee، assign، late check-in، notification و report باشد.
- [ ] فایل `docs/interview/resume-bullets.md` ساخته شود.

نمونه bullet:

```text
Designed and implemented a spec-driven HR ERP backend using Spring Boot microservices, Spring Cloud Gateway, Eureka, Kafka, PostgreSQL, Docker, GitHub Actions, JWT RBAC, OpenAPI, Flyway, and Testcontainers.
```

- [ ] Commit:

```bash
git add docs/interview
git commit -m "docs(interview): add project walkthrough and resume bullets"
```

---

## Sprint Plan پیشنهادی

### Sprint 0: Setup

مدت: 2 تا 3 روز.

Tickets:

- ERP-001: Initialize repository and branch protections.
- ERP-002: Add team workflow docs.
- ERP-003: Add PR and issue templates.
- ERP-004: Add Jira epics and story template.

### Sprint 1: Platform

مدت: 1 هفته.

Tickets:

- ERP-101: Scaffold Discovery Server.
- ERP-102: Scaffold API Gateway.
- ERP-103: Add common response and error DTOs.
- ERP-104: Add Docker Compose infrastructure.
- ERP-105: Add initial CI pipeline.

### Sprint 2: Identity

مدت: 1 تا 2 هفته.

Tickets:

- ERP-201: Add Identity Service spec.
- ERP-202: Add user and role schema.
- ERP-203: Implement login and JWT.
- ERP-204: Implement user management APIs.
- ERP-205: Add protected endpoint tests.

### Sprint 3: Organization And Employees

مدت: 2 هفته.

Tickets:

- ERP-301: Add Department Service spec.
- ERP-302: Implement Department Service.
- ERP-303: Publish department events.
- ERP-401: Add Employee Service spec.
- ERP-402: Implement Employee Service.
- ERP-403: Publish employee events.

### Sprint 4: Attendance

مدت: 2 هفته.

Tickets:

- ERP-501: Add Attendance Service spec.
- ERP-502: Add attendance schema.
- ERP-503: Implement attendance policy evaluator.
- ERP-504: Implement check-in.
- ERP-505: Implement check-out.
- ERP-506: Implement monthly summary.
- ERP-507: Publish attendance events.

### Sprint 5: Reporting And Notifications

مدت: 2 هفته.

Tickets:

- ERP-601: Add Reporting Service spec.
- ERP-602: Build reporting projections.
- ERP-603: Implement report APIs.
- ERP-701: Add Notification Service spec.
- ERP-702: Consume violation events.
- ERP-703: Implement notification APIs.

### Sprint 6: Polish And Presentation

مدت: 1 تا 2 هفته.

Tickets:

- ERP-801: Add all services to Docker Compose.
- ERP-802: Expand CI to all services.
- ERP-803: Add architecture diagrams.
- ERP-804: Add Postman collection.
- ERP-805: Add interview walkthrough.
- ERP-806: Record final demo video or screenshots.

---

## Final Verification Checklist

- [ ] `docker compose up --build` کل سیستم را بالا می آورد.
- [ ] API Gateway به همه سرویس ها route می کند.
- [ ] همه سرویس ها در Discovery Server ثبت می شوند.
- [ ] Identity Service می تواند JWT token صادر کند.
- [ ] API های protected بدون token درخواست را reject می کنند.
- [ ] Employee ساخته می شود.
- [ ] Department ساخته می شود.
- [ ] Employee به Department assign می شود.
- [ ] Employee می تواند check in و check out کند.
- [ ] Duplicate check-in مقدار 409 برمی گرداند.
- [ ] Late check-in event مربوط به attendance violation را publish می کند.
- [ ] Reporting Service read model را از Kafka event ها به روز می کند.
- [ ] Notification Service از attendance violation notification می سازد.
- [ ] GitHub Actions برای همه سرویس ها pass می شود.
- [ ] README اجرای محلی و demo flow را توضیح می دهد.
- [ ] Architecture diagrams وجود دارند.
- [ ] Resume bullets و interview walkthrough آماده هستند.

## پیشنهاد اجرا

ابتدا فقط Milestone 0 و Milestone 1 را انجام دهید. تا وقتی branch protection، PR template، Definition of Done، Discovery Server، API Gateway، Docker Compose و CI آماده نیستند، Identity Service را شروع نکنید.

این کار نظم پروژه را حفظ می کند و باعث می شود هر ماژول بعدی راحت تر review، test و در مصاحبه توضیح داده شود.
