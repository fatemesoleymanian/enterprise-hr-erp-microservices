# Reporting Service Requirements

Reporting Service owns read-only report projections.

## Scope

- Consume Kafka events from operational services.
- Build local reporting read models.
- Expose department headcount reports.
- Expose employee status summary reports.
- Expose monthly attendance reports.
- Expose department-level attendance reports.

## Out Of Scope

- Creating or updating employees.
- Creating or updating departments.
- Creating or updating attendance records.
- Querying another service database directly.
- Owning operational business rules for employee, department, or attendance data.

## Business Rules

- Reporting Service must not query operational service databases.
- Reporting Service consumes Kafka events and updates local read models.
- Reports may be eventually consistent.
- Reports expose department headcount, employee status summary, and monthly attendance summary.
- Report APIs read only from Reporting Service projections.
- Projection updates must be idempotent because Kafka events may be delivered more than once.
- Projection records use stable IDs from event payloads.
- If a report has no matching projection data, the API returns an empty result or zero counts instead of querying another service.

## Read Models

- `EmployeeReportView`
- `DepartmentReportView`
- `AttendanceMonthlyReportView`

## Consumed Events

- `DepartmentCreated`
- `DepartmentManagerAssigned`
- `EmployeeCreated`
- `EmployeeDepartmentChanged`
- `EmployeeStatusChanged`
- `AttendanceCheckedIn`
- `AttendanceCheckedOut`
- `AttendanceViolationDetected`

## Report APIs

- `GET /api/reports/attendance/monthly`
- `GET /api/reports/departments/{departmentId}/attendance`
- `GET /api/reports/employees/status-summary`
- `GET /api/reports/departments/headcount`

## Security Expectations

- Unauthenticated requests return `401 Unauthorized`.
- HR managers and admins may view all reports.
- Department managers may view reports for departments they manage.
- Employees do not directly access cross-employee reporting endpoints.

## Dependencies

- PostgreSQL stores reporting projections in `reporting_db`.
- Kafka provides employee, department, and attendance events.
- API Gateway exposes Reporting Service under `/api/reports/**`.
- Eureka registers the service as `reporting-service`.

## Service Boundary Rules

- Reporting Service owns only reporting projection data.
- Reporting Service must not read or write identity, employee, department, attendance, or notification databases.
- Operational services must not write Reporting Service projections directly.
- Reporting Service rebuilds or updates projections only from events.
