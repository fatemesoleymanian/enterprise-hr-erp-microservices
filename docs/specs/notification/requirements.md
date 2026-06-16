# Notification Service Requirements

Notification Service stores notifications triggered by domain events.

## Scope

- Consume domain events that require HR or manager attention.
- Create notification records from attendance violation events.
- Create notification records from employee status change events.
- Expose unread and read notifications through an API.
- Allow a notification to be marked as read.
- Simulate delivery by storing notifications in the service database.

## Out Of Scope

- Sending real email, SMS, push, or chat messages.
- Managing user preferences.
- Owning employee, department, attendance, or identity data.
- Querying another service database directly.
- Retrying external delivery providers.

## Business Rules

- Attendance violations create HR notifications.
- Employee status changes create HR notifications.
- Notifications can be read or unread.
- New notifications are unread by default.
- Marking a notification as read sets `read` to `true` and records `read_at`.
- Marking an already-read notification as read is idempotent.
- Missing notifications return `404 Not Found`.
- Duplicate consumed events must not create duplicate notifications.
- Real email and SMS delivery are outside MVP.

## Notification Types

- `ATTENDANCE_VIOLATION`
- `EMPLOYEE_STATUS_CHANGED`

## Recipient Model

- `recipient_role` identifies the target role, such as `HR`.
- `recipient_user_id` is optional and is used only when a notification targets one user.
- Initial MVP notifications are role-based HR notifications.

## Consumed Events

- `AttendanceViolationDetected`
- `EmployeeStatusChanged`

## Notification APIs

- `GET /api/notifications`
- `PATCH /api/notifications/{id}/read`

## Security Expectations

- Unauthenticated requests return `401 Unauthorized`.
- HR managers and admins may list HR notifications.
- Users may only list notifications they are allowed to view.
- Marking a notification as read requires access to that notification.

## Dependencies

- PostgreSQL stores notifications in `notification_db`.
- Kafka provides attendance and employee events.
- API Gateway exposes Notification Service under `/api/notifications/**`.
- Eureka registers the service as `notification-service`.

## Service Boundary Rules

- Notification Service owns only notification data.
- Notification Service must not read or write identity, employee, department, attendance, or reporting databases.
- Operational services must not write Notification Service tables directly.
- Notification Service creates notifications only from consumed events or future notification commands owned by this service.
