# Jira Epics And Story Standard

This project uses Jira-style planning even when issues are tracked in GitHub. Each ticket should be small enough to review in a focused pull request.

## Epics

- ERP-001 Platform Foundation
- ERP-002 Identity and Access Management
- ERP-003 Organization Management
- ERP-004 Employee Management
- ERP-005 Attendance Management
- ERP-006 Reporting
- ERP-007 Events and Notifications
- ERP-008 DevOps and Observability
- ERP-009 Documentation and Interview Evidence

## Sprint 0 Tickets

- ERP-001: Initialize repository and branch protections.
- ERP-002: Add team workflow docs.
- ERP-003: Add PR and issue templates.
- ERP-004: Add Jira epics and story quality standard.

## Story Quality Standard

Every story must include:

- User story
- Business rules
- API contract when an endpoint is affected
- Database changes when persistence is affected
- Kafka events when asynchronous communication is affected
- Acceptance criteria
- Test expectations

## Example Story

Title: ERP-ATT-001 Employee check-in

User story:

As an employee,
I want to check in at the start of my workday,
so that my attendance can be recorded.

Business rules:

- A valid active employee can check in once per date.
- A duplicate check-in returns 409.
- A check-in after 09:15 is marked LATE.

API contract:

- Method: POST
- Path: /api/attendance/check-in
- Request: employeeId, checkInAt
- Response: attendanceRecordId, employeeId, attendanceDate, checkInAt, status

Database changes:

- Table: attendance_records
- Columns: id, employee_id, attendance_date, check_in_at, status, created_at, updated_at, version
- Indexes: employee_id, attendance_date, status

Events:

- Produced: AttendanceCheckedIn, AttendanceViolationDetected
- Consumed: none

Acceptance criteria:

- Given an active employee with no attendance record today, when the employee checks in, then the API returns 201.
- Given an employee already checked in today, when the employee checks in again, then the API returns 409.
- Given the employee checks in at 09:20, when the policy late threshold is 09:15, then the record status includes LATE.

Test expectations:

- Unit tests cover late arrival policy.
- Integration tests cover duplicate check-in persistence.
- Security tests verify unauthenticated requests return 401.
