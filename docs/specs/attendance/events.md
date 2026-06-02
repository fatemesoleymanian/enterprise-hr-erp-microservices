# Attendance Events

Attendance Service publishes domain events to Kafka after successful attendance state changes.

## Event Envelope

All events use a shared envelope.

```json
{
  "eventId": "f3ab8c5d-b8f8-4b19-9e35-4d83c57b02a2",
  "eventType": "AttendanceCheckedIn",
  "occurredAt": "2026-06-01T04:45:00Z",
  "version": 1,
  "producer": "attendance-service",
  "correlationId": "7c0bd8c2-d29d-489f-8dbf-3f913f1fd89e",
  "payload": {}
}
```

## Topics

Initial topic naming:

```text
attendance.events
```

Reporting Service and Notification Service consume attendance events from this topic.

## AttendanceCheckedIn

Published when an employee successfully checks in.

```json
{
  "eventType": "AttendanceCheckedIn",
  "payload": {
    "attendanceRecordId": "a3e99e2d-dfa2-4e28-bc4a-11d89c7c7b75",
    "employeeId": "17f27b9b-bd8d-468a-9140-2f3c5d35a8b1",
    "attendanceDate": "2026-06-01",
    "checkInAt": "2026-06-01T04:45:00Z",
    "statuses": ["PRESENT"]
  }
}
```

## AttendanceCheckedOut

Published when an employee successfully checks out.

```json
{
  "eventType": "AttendanceCheckedOut",
  "payload": {
    "attendanceRecordId": "a3e99e2d-dfa2-4e28-bc4a-11d89c7c7b75",
    "employeeId": "17f27b9b-bd8d-468a-9140-2f3c5d35a8b1",
    "attendanceDate": "2026-06-01",
    "checkInAt": "2026-06-01T04:45:00Z",
    "checkOutAt": "2026-06-01T11:30:00Z",
    "statuses": ["PRESENT"],
    "workedMinutes": 405
  }
}
```

## AttendanceViolationDetected

Published when Attendance Service detects a late arrival or early leave.

```json
{
  "eventType": "AttendanceViolationDetected",
  "payload": {
    "attendanceRecordId": "a3e99e2d-dfa2-4e28-bc4a-11d89c7c7b75",
    "employeeId": "17f27b9b-bd8d-468a-9140-2f3c5d35a8b1",
    "attendanceDate": "2026-06-01",
    "violationType": "LATE",
    "detectedAt": "2026-06-01T04:50:00Z"
  }
}
```

## Consumers

Expected consumers:

- Reporting Service updates attendance read models.
- Notification Service creates HR notifications for attendance violations.

## Reliability Expectations

- Events are published only after the attendance database change succeeds.
- Consumers must be idempotent because Kafka events may be delivered more than once.
- Event payloads must include stable IDs so consumers can safely update projections.
