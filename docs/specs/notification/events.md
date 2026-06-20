# Notification Events

Notification Service consumes domain events from Kafka and stores notification records.

## Event Envelope

All consumed events are expected to use the shared envelope.

```json
{
  "eventId": "f3ab8c5d-b8f8-4b19-9e35-4d83c57b02a2",
  "eventType": "AttendanceViolationDetected",
  "occurredAt": "2026-06-01T04:50:00Z",
  "version": 1,
  "producer": "attendance-service",
  "correlationId": "7c0bd8c2-d29d-489f-8dbf-3f913f1fd89e",
  "payload": {}
}
```

## Topics

Notification Service consumes from these initial topics:

```text
attendance.events
employee.events
```

## Attendance Events

### AttendanceViolationDetected

Creates an unread HR notification.

Expected payload fields:

- `attendanceRecordId`
- `employeeId`
- `attendanceDate`
- `violationType`
- `detectedAt`

Notification mapping:

- `recipientRole`: `HR`
- `recipientUserId`: `null`
- `type`: `ATTENDANCE_VIOLATION`
- `title`: Attendance violation detected
- `body`: Includes employee ID, violation type, and attendance date
- `read`: `false`

## Employee Events

### EmployeeStatusChanged

Creates an unread HR notification.

Expected payload fields:

- `employeeId`
- `status`
- `changedAt`

Notification mapping:

- `recipientRole`: `HR`
- `recipientUserId`: `null`
- `type`: `EMPLOYEE_STATUS_CHANGED`
- `title`: Employee status changed
- `body`: Includes employee ID and new status
- `read`: `false`

## Reliability Expectations

- Consumers must be idempotent because Kafka events may be delivered more than once.
- Consumers must use `eventId` or a stable payload identifier to avoid duplicate notifications.
- Processed event IDs are stored in `processed_notification_events`.
- Notification creation must not call other services during event processing.
- If event payloads contain only IDs, the notification body stores those IDs instead of querying another service.
- Failed event processing may be retried by Kafka consumer retry behavior.

## Produced Events

Notification Service may publish `NotificationCreated` in a later milestone if another service needs to react to notification creation.

The initial MVP only requires storing notifications and exposing notification APIs.
