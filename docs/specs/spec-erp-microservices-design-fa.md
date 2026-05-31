# طراحی سیستم ERP مبتنی بر Microservices

## 1. هدف

این پروژه یک شبیه سازی حرفه ای و رزومه محور از یک ERP Backend برای یک تیم 2 تا 4 نفره Java Backend است. سیستم، دامنه ای کوچک از منابع انسانی، کاربران، کارمندان، دپارتمان ها و حضور و غیاب را با Spring Boot Microservices، API Gateway، Service Discovery، Docker، Kafka، PostgreSQL، CI/CD، Git Flow، Pull Request، Code Review و مدیریت تسک به سبک Jira پیاده سازی می کند.

هدف اصلی فقط ساخت چند قابلیت نیست. هدف این است که تیم تجربه واقعی طراحی، مستندسازی، پیاده سازی، تست، بازبینی کد و تحویل نرم افزار سازمانی را شبیه سازی کند.

## 2. معیارهای موفقیت

پروژه زمانی موفق است که تیم بتواند موارد زیر را نشان دهد:

- چند سرویس Spring Boot مستقل و قابل استقرار جداگانه.
- مسیریابی همه درخواست های بیرونی از طریق API Gateway.
- ثبت و کشف سرویس ها با Service Discovery.
- احراز هویت مبتنی بر JWT و مجوزدهی Role-Based.
- مالکیت دیتابیس جداگانه برای هر سرویس.
- ارتباطات Async با Kafka برای رویدادهای مهم دامنه.
- محیط اجرای محلی با Docker Compose.
- مستندات OpenAPI برای API سرویس ها.
- Migration دیتابیس با Flyway یا Liquibase.
- Unit Test و Integration Test.
- اجرای CI برای build و test روی Pull Request ها.
- همکاری تیمی مبتنی بر PR و Code Review.
- Epic، Story و Acceptance Criteria به سبک Jira.
- README، دیاگرام معماری و مستندات آماده برای مصاحبه.

## 3. محدوده پیشنهادی MVP

MVP شامل سرویس های زیر است:

1. API Gateway
2. Discovery Server
3. Identity Service
4. Employee Service
5. Department Service
6. Attendance Service
7. Reporting Service
8. Notification Service

مواردی مثل Payroll، Leave Approval پیچیده، Document Management و Kubernetes پیشرفته در MVP نیستند. این موارد برای نسخه 2 مناسب اند، بعد از اینکه هسته سیستم پایدار شد.

## 4. نمای کلی معماری

سیستم از معماری Microservices استفاده می کند. برای Query و Command مستقیم از HTTP API استفاده می شود و برای اطلاع رسانی بین سرویس ها و ساخت Projection های گزارش گیری از Kafka استفاده می شود.

کلاینت های بیرونی فقط با API Gateway صحبت می کنند. Gateway مسیرها را مدیریت می کند و درخواست ها را به سرویس های Backend می فرستد. سرویس ها در Discovery Server ثبت می شوند. هر سرویس تجاری دیتابیس PostgreSQL خودش را دارد و هیچ سرویسی دیتابیس سرویس دیگر را مستقیم نمی خواند یا تغییر نمی دهد.

Kafka برای Domain Event هایی مثل ایجاد کارمند، تغییر دپارتمان، ثبت check-in، تخلف حضور و غیاب و ایجاد Notification استفاده می شود. Reporting Service گزارش ها را از روی Event ها و Projection های داخلی خودش می سازد، نه از طریق join بین دیتابیس سرویس ها.

## 5. تکنولوژی های هدف

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Security
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka یا Consul
- Spring Data JPA
- PostgreSQL
- Flyway یا Liquibase
- Apache Kafka
- Docker و Docker Compose
- OpenAPI / Swagger
- JUnit 5
- Mockito
- Testcontainers
- GitHub Actions
- اختیاری: Prometheus، Grafana، Loki، SonarQube

## 6. ساختار Repository

ساختار پیشنهادی یک mono-repo است:

```text
erp-system
|-- api-gateway
|-- discovery-server
|-- identity-service
|-- employee-service
|-- department-service
|-- attendance-service
|-- reporting-service
|-- notification-service
|-- common-lib
|-- docker-compose.yml
|-- docs
|   |-- adr
|   |-- api
|   |-- diagrams
|   `-- specs
`-- .github
    `-- workflows
```

برای تیم 2 تا 4 نفره، mono-repo پیشنهاد می شود چون onboarding، اجرای محلی، review و CI را ساده تر می کند.

## 7. مسئولیت سرویس ها

### 7.1 API Gateway

API Gateway تنها نقطه ورود خارجی سیستم است.

مسئولیت ها:

- Route کردن درخواست ها به سرویس های Backend.
- اعمال تنظیمات مشترک CORS.
- اعتبارسنجی یا forward کردن JWT.
- ارائه مسیرهای پایدار برای سرویس ها.
- فراهم کردن نقطه مرکزی برای Rate Limiting در نسخه 2.

نمونه route ها:

```text
/api/auth/**          -> identity-service
/api/users/**         -> identity-service
/api/employees/**     -> employee-service
/api/departments/**   -> department-service
/api/attendance/**    -> attendance-service
/api/reports/**       -> reporting-service
```

### 7.2 Discovery Server

Discovery Server اجازه می دهد سرویس ها خودشان را register کنند و سرویس های دیگر را پیدا کنند.

مسئولیت ها:

- Service registration.
- Service lookup.
- کمک به توسعه محلی با URL های dynamic برای سرویس ها.

### 7.3 Identity Service

Identity Service مالک users، credentials، roles و authentication است.

مسئولیت ها:

- ایجاد حساب کاربری داخلی.
- احراز هویت کاربران.
- صدور JWT access token.
- ذخیره password hash.
- مدیریت وضعیت کاربر.
- مدیریت role ها.

Entity های اصلی:

- User
- Role

Role های لازم:

- ADMIN
- HR_MANAGER
- DEPARTMENT_MANAGER
- EMPLOYEE

API های کلیدی:

```text
POST /api/auth/login
POST /api/users
GET /api/users/{id}
PATCH /api/users/{id}/status
PUT /api/users/{id}/roles
```

### 7.4 Department Service

Department Service مالک ساختار دپارتمان های سازمان است.

مسئولیت ها:

- ایجاد دپارتمان.
- ویرایش دپارتمان.
- تعیین مدیر دپارتمان.
- نگهداری رابطه parent-child بین دپارتمان ها.
- انتشار event های مربوط به دپارتمان.

Entity اصلی:

- Department

API های کلیدی:

```text
POST /api/departments
GET /api/departments
GET /api/departments/{id}
PUT /api/departments/{id}
PATCH /api/departments/{id}/manager
```

### 7.5 Employee Service

Employee Service مالک پروفایل کارمند و وضعیت استخدامی اوست.

مسئولیت ها:

- ایجاد پروفایل کارمند.
- ویرایش پروفایل کارمند.
- انتساب کارمند به دپارتمان.
- تعیین مدیر مستقیم کارمند.
- تغییر وضعیت استخدامی.
- جستجوی کارمندان.
- انتشار event های مربوط به کارمند.

Entity اصلی:

- Employee

وضعیت های کارمند:

- ACTIVE
- ON_LEAVE
- SUSPENDED
- TERMINATED

API های کلیدی:

```text
POST /api/employees
GET /api/employees
GET /api/employees/{id}
PUT /api/employees/{id}
PATCH /api/employees/{id}/department
PATCH /api/employees/{id}/status
```

### 7.6 Attendance Service

Attendance Service مالک check-in، check-out و قوانین حضور و غیاب است.

مسئولیت ها:

- ثبت check-in.
- ثبت check-out.
- جلوگیری از check-in تکراری برای یک کارمند در یک روز.
- محاسبه مدت کارکرد.
- تشخیص late arrival.
- تشخیص early leave.
- تولید summary ماهانه حضور و غیاب.
- انتشار event های حضور و غیاب.

Entity های اصلی:

- AttendanceRecord
- AttendancePolicy

قوانین پیش فرض:

- شروع روز کاری ساعت 09:00 است.
- check-in بعد از 09:15 وضعیت LATE دارد.
- check-out قبل از 16:00 وضعیت EARLY_LEAVE دارد.
- نبود check-in در یک روز کاری الزامی، ABSENT محسوب می شود.
- هر کارمند در هر تاریخ فقط یک attendance record دارد.

API های کلیدی:

```text
POST /api/attendance/check-in
POST /api/attendance/check-out
GET /api/attendance/employees/{employeeId}
GET /api/attendance/employees/{employeeId}/monthly-summary
```

### 7.7 Reporting Service

Reporting Service مالک view های فقط خواندنی برای گزارش گیری است.

مسئولیت ها:

- مصرف event های employee، department و attendance.
- ساخت Projection های گزارش.
- ارائه گزارش های سطح دپارتمان و کارمند.
- جلوگیری از join مستقیم بین دیتابیس سرویس ها.

Read model های اصلی:

- EmployeeReportView
- DepartmentReportView
- AttendanceMonthlyReportView

API های کلیدی:

```text
GET /api/reports/attendance/monthly
GET /api/reports/departments/{departmentId}/attendance
GET /api/reports/employees/status-summary
GET /api/reports/departments/headcount
```

### 7.8 Notification Service

Notification Service رویدادها را مصرف می کند و notification record می سازد.

مسئولیت ها:

- مصرف event های attendance violation.
- مصرف event های تغییر وضعیت کارمند.
- ایجاد notification برای HR یا مدیر دپارتمان.
- شبیه سازی ارسال email با ذخیره و log کردن notification.

Entity اصلی:

- Notification

API های کلیدی:

```text
GET /api/notifications
PATCH /api/notifications/{id}/read
```

## 8. قوانین مالکیت داده

هر سرویس schema و دیتابیس خودش را دارد.

قوانین:

- هیچ سرویسی نباید دیتابیس سرویس دیگر را مستقیم query کند.
- ارجاع بین سرویس ها با ID انجام می شود، نه foreign key بین دیتابیس ها.
- سرویس ها برای validation فوری می توانند HTTP call بزنند.
- سرویس ها می توانند event های Kafka را مصرف کنند و read model داخلی بسازند.
- Reporting باید از Projection یا read model مبتنی بر event استفاده کند.

مثال:

Employee Service می تواند `departmentId` را ذخیره کند، اما نام و hierarchy دپارتمان متعلق به Department Service است.

## 9. طراحی Kafka Event

نام topic ها باید پایدار و domain-oriented باشد.

Topic های پیشنهادی:

```text
identity.user-events
organization.department-events
hr.employee-events
attendance.attendance-events
notification.notification-events
```

Event envelope پیشنهادی:

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

Event های اولیه:

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

## 10. مدل امنیتی

Identity Service توکن JWT صادر می کند. API Gateway و سرویس های Backend باید token را validate کنند.

قوانین دسترسی:

- ADMIN به همه API های مدیریتی دسترسی دارد.
- HR_MANAGER می تواند employees، departments، attendance و reports را مدیریت کند.
- DEPARTMENT_MANAGER می تواند کارمندان و attendance دپارتمان خودش را ببیند.
- EMPLOYEE می تواند پروفایل و attendance خودش را ببیند.

نیازمندی های امنیتی:

- پسوردها باید با BCrypt hash شوند.
- پسورد plain-text هرگز نباید log شود.
- JWT secret یا signing key باید از environment variable بیاید.
- API های protected باید token گم شده، نامعتبر یا منقضی را reject کنند.
- برای عملیات حساس از method-level authorization استفاده شود.

## 11. استانداردهای API

همه API ها باید از convention مشترک پیروی کنند.

فرمت response:

```json
{
  "data": {},
  "message": "Success",
  "timestamp": "2026-05-31T10:15:30Z"
}
```

فرمت error response:

```json
{
  "errorCode": "EMPLOYEE_NOT_FOUND",
  "message": "Employee was not found",
  "path": "/api/employees/123",
  "timestamp": "2026-05-31T10:15:30Z"
}
```

قرارداد HTTP status:

- 200 برای read و update موفق.
- 201 برای create موفق.
- 204 برای delete یا update بدون body.
- 400 برای validation error.
- 401 برای request بدون authentication.
- 403 برای request بدون authorization.
- 404 برای resource پیدا نشده.
- 409 برای conflict تجاری.
- 500 برای خطای غیرمنتظره سرور.

## 12. استانداردهای دیتابیس

هر سرویس باید migration script داشته باشد.

قوانین:

- از UUID primary key استفاده شود مگر دلیل جدی برای خلاف آن وجود داشته باشد.
- هر جدول `created_at` و `updated_at` داشته باشد.
- برای رکوردهای mutable مهم از optimistic locking با ستون `version` استفاده شود.
- فیلدهای ضروری و uniqueness با constraint دیتابیس محافظت شوند.
- برای lookup های رایج index ایجاد شود.

جدول های نمونه:

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

## 13. استراتژی تست

تست بخشی از Definition of Done است.

سطح های تست لازم:

- Unit test برای business rule ها.
- Web layer test برای controller ها.
- Repository test برای query های غیرساده.
- Integration test با Testcontainers برای رفتار دیتابیس.
- Kafka integration test برای producer و consumer ها.
- Security test برای endpoint های protected.

Attendance Service باید بیشترین پوشش تست را داشته باشد، چون قوانین تجاری آن مهم تر و پیچیده تر است.

نمونه acceptance test:

```text
Given an active employee
And the workday starts at 09:00
When the employee checks in at 09:20
Then the attendance record is created
And the record status contains LATE
And an AttendanceViolationDetected event is published
```

## 14. فرایند تیمی

تیم باید از Git Flow و Pull Request استفاده کند.

Branch ها:

```text
main
develop
feature/ERP-123-short-name
release/1.0.0
hotfix/1.0.1
```

قوانین Pull Request:

- هر تغییر باید از طریق PR انجام شود.
- حداقل یک عضو تیم باید PR را review کند.
- CI باید قبل از merge پاس شود.
- توضیح PR باید به Jira ticket لینک شود.
- توضیح PR باید evidence تست داشته باشد.
- Feature های بزرگ باید به PR های کوچک تقسیم شوند.

سبک commit پیشنهادی:

```text
feat(identity): add login endpoint
fix(attendance): prevent duplicate check-in
test(employee): add employee creation integration test
docs(architecture): add service boundary decision
ci: add GitHub Actions build workflow
```

## 15. ساختار Jira

Epic های پیشنهادی:

- ERP-001 Platform Foundation
- ERP-002 Identity and Access Management
- ERP-003 Organization Management
- ERP-004 Employee Management
- ERP-005 Attendance Management
- ERP-006 Reporting
- ERP-007 Events and Notifications
- ERP-008 DevOps and Observability

هر Story باید شامل این موارد باشد:

- User story
- Business rules
- API contract
- Database changes
- Kafka events
- Validation rules
- Acceptance criteria
- Test expectations

نمونه Story:

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

## 16. نقشه راه Milestone ها

### Milestone 0: Team Setup

مدت: 2 تا 3 روز.

خروجی ها:

- Repository آماده شده.
- README ایجاد شده.
- Branch strategy مستند شده.
- PR template ایجاد شده.
- Issue template ایجاد شده.
- Jira board ساخته شده.
- Definition of Done مستند شده.

### Milestone 1: Platform Foundation

مدت: 1 هفته.

خروجی ها:

- Discovery Server.
- API Gateway.
- Docker Compose با PostgreSQL و Kafka.
- فرمت مشترک response و error.
- CI pipeline اولیه.

### Milestone 2: Identity Service

مدت: 1 تا 2 هفته.

خروجی ها:

- User model.
- Role model.
- Login API.
- JWT generation.
- JWT validation.
- Protected endpoint tests.

### Milestone 3: Department and Employee Services

مدت: 2 هفته.

خروجی ها:

- Department CRUD.
- Department manager assignment.
- Employee CRUD.
- Employee department assignment.
- Employee status changes.
- Department and employee events.

### Milestone 4: Attendance Service

مدت: 2 هفته.

خروجی ها:

- Check-in.
- Check-out.
- جلوگیری از duplicate check-in.
- Late arrival detection.
- Early leave detection.
- Monthly attendance summary.
- Attendance events.

### Milestone 5: Reporting Service

مدت: 1 هفته.

خروجی ها:

- Event consumers.
- Reporting projections.
- Monthly attendance report.
- Department headcount report.
- Employee status summary.

### Milestone 6: Notification Service

مدت: 1 هفته.

خروجی ها:

- Event consumers.
- Notification records.
- Read/unread notification API.
- Attendance violation notification flow.

### Milestone 7: DevOps and Polish

مدت: 1 تا 2 هفته.

خروجی ها:

- اجرای کامل با Docker Compose.
- CI pipeline کامل تر.
- Integration test suite.
- Architecture diagrams.
- Postman collection.
- README نهایی.
- Interview notes.

## 17. گزینه های تقسیم کار تیمی

برای 2 نفر:

- نفر 1: Gateway، Discovery، Identity، Security، CI/CD.
- نفر 2: Department، Employee، Attendance، Reporting.
- هر دو: spec، test و PR review.

برای 3 نفر:

- نفر 1: Platform، Gateway، Discovery، Identity، Security.
- نفر 2: Department و Employee Services.
- نفر 3: Attendance، Reporting و Kafka consumers.

برای 4 نفر:

- نفر 1: Platform، DevOps، Gateway، Discovery.
- نفر 2: Identity و Security.
- نفر 3: Department و Employee Services.
- نفر 4: Attendance، Reporting، Notification.

## 18. Architecture Decision Record ها

تیم باید برای تصمیم های زیر ADR بنویسد:

- ADR-001: Mono-repo به جای multi-repo.
- ADR-002: PostgreSQL database per service.
- ADR-003: Kafka برای asynchronous domain events.
- ADR-004: JWT-based authentication.
- ADR-005: Reporting via projections به جای cross-service joins.
- ADR-006: Docker Compose برای local development.
- ADR-007: Git Flow با pull request و protected branches.

## 19. ایده های نسخه 2

بعد از تکمیل MVP، تیم می تواند موارد زیر را اضافه کند:

- Leave Management Service.
- Payroll Simulation Service.
- Approval Workflow Service.
- Audit Log Service.
- Document Service.
- Kubernetes deployment.
- Distributed tracing با OpenTelemetry.
- Contract tests با Spring Cloud Contract.

تا وقتی MVP پایدار و مستند نشده، این موارد نباید شروع شوند.

## 20. مدارک رزومه و مصاحبه

خروجی نهایی پروژه باید شامل این موارد باشد:

- GitHub repository عمومی.
- README تمیز با دستور اجرای پروژه.
- Architecture diagram.
- جدول مسئولیت سرویس ها.
- API documentation.
- Kafka event documentation.
- Screenshot از CI موفق.
- نمونه PR با code review comment.
- Test reports.
- دستور اجرای Docker Compose.

نمونه bullet رزومه:

```text
Designed and implemented a spec-driven HR ERP backend using Spring Boot microservices, Spring Cloud Gateway, Eureka, Kafka, PostgreSQL, Docker, GitHub Actions, JWT RBAC, OpenAPI, Flyway, and Testcontainers, simulating enterprise team workflows with Git Flow, Jira stories, pull requests, and code review.
```

## 21. خارج از محدوده MVP

موارد زیر عمدا در MVP نیستند:

- Frontend application.
- Payroll calculations.
- Real email delivery.
- Real SMS delivery.
- Advanced leave approval workflow.
- Kubernetes production deployment.
- Multi-tenant company support.
- Complex shift scheduling.
- Biometric attendance integration.

این محدودیت ها باعث می شوند پروژه برای یک تیم کوچک قابل انجام بماند و همزمان ارزش مصاحبه ای قوی داشته باشد.
