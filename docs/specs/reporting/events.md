# Reporting Events

Reporting Service consumes domain events from Kafka and updates local report projections.

## Event Envelope

All consumed events are expected to use the shared envelope.

```json
{
  "eventId": "f3ab8c5d-b8f8-4b19-9e35-4d83c57b02a2",
  "eventType": "AttendanceCheckedOut",
  "occurredAt": "2026-06-01T11:30:00Z",
  "version": 1,
  "producer": "attendance-service",
  "correlationId": "7c0bd8c2-d29d-489f-8dbf-3f913f1fd89e",
  "payload": {}
}
```

## Topics

Reporting Service consumes from these initial topics:

```text
department.events
employee.events
attendance.events
```

## Department Events

### DepartmentCreated

Creates or updates `department_report_views`.

Expected payload fields:

- `departmentId`
- `name`
- `managerUserId`
- `createdAt`

### DepartmentManagerAssigned

Updates `department_report_views.manager_user_id`.

Expected payload fields:

- `departmentId`
- `managerUserId`
- `assignedAt`

## Employee Events

### EmployeeCreated

Creates or updates `employee_report_views`.

Expected payload fields:

- `employeeId`
- `departmentId`
- `status`
- `jobTitle`
- `createdAt`

### EmployeeDepartmentChanged

Updates `employee_report_views.department_id`.

Expected payload fields:

- `employeeId`
- `departmentId`
- `changedAt`

### EmployeeStatusChanged

Updates `employee_report_views.status`.

Expected payload fields:

- `employeeId`
- `status`
- `changedAt`

## Attendance Events

### AttendanceCheckedIn

Creates or updates the monthly attendance projection for the employee and attendance month.

Expected payload fields:

- `attendanceRecordId`
- `employeeId`
- `attendanceDate`
- `checkInAt`
- `statuses`

### AttendanceCheckedOut

Updates the monthly attendance projection for the employee and attendance month, including worked minutes.

Expected payload fields:

- `attendanceRecordId`
- `employeeId`
- `attendanceDate`
- `checkInAt`
- `checkOutAt`
- `statuses`
- `workedMinutes`

### AttendanceViolationDetected

Updates late or early-leave counts when the violation is not already reflected in the monthly attendance projection.

Expected payload fields:

- `attendanceRecordId`
- `employeeId`
- `attendanceDate`
- `violationType`
- `detectedAt`

## Reliability Expectations

- Consumers must be idempotent because Kafka events may be delivered more than once.
- Consumers must use `eventId` or stable payload IDs to avoid double-counting projections.
- Projection updates must not call other services during event processing.
- If an event arrives before related projection data exists, Reporting Service stores the available data and fills missing fields when later events arrive.
- Report APIs may show eventually consistent data.

## Produced Events

Reporting Service does not publish domain events in the initial milestone.
