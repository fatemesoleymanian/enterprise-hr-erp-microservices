# Attendance Service Requirements

Attendance Service owns employee check-in, check-out, attendance policy evaluation, attendance records, and monthly attendance summaries.

## Scope

- Record employee check-in.
- Record employee check-out.
- Detect late arrival.
- Detect early leave.
- Calculate worked minutes.
- Return employee attendance history.
- Return monthly attendance summaries.
- Publish attendance domain events.

## Out Of Scope

- Employee profile ownership.
- Department ownership.
- Payroll calculation.
- Leave management.
- Real-time employee validation against another service.

## Business Rules

- One employee can have only one attendance record per date.
- Workday starts at 09:00.
- Check-in after 09:15 is marked `LATE`.
- Check-in at 09:15 is still `PRESENT`.
- Check-out before 16:00 is marked `EARLY_LEAVE`.
- Check-out at 16:00 is not marked `EARLY_LEAVE`.
- Check-out requires an existing check-in for the same employee and date.
- Duplicate check-in for the same employee and date returns `409 Conflict`.
- Duplicate check-out for the same employee and date returns `409 Conflict`.
- Worked minutes are calculated from `checkInAt` to `checkOutAt`.
- Monthly summary includes present days, late days, early leave days, absent days, and total worked minutes.

## Status Values

- `PRESENT`
- `LATE`
- `EARLY_LEAVE`
- `ABSENT`

If an employee checks in late and checks out early, the record may include both `LATE` and `EARLY_LEAVE` in the service implementation. The API contract represents this as a list of statuses.

## Security Expectations

- Unauthenticated requests return `401 Unauthorized`.
- Employees may view their own attendance records.
- HR managers and admins may view attendance records for any employee.
- Only authenticated employees, HR managers, or admins may create check-in and check-out records.

## Dependencies

- PostgreSQL stores attendance policies and attendance records.
- Kafka is used to publish attendance events.
- API Gateway exposes Attendance Service under `/api/attendance/**`.
- Eureka registers the service as `attendance-service`.

## Service Boundary Rules

- Attendance Service owns only attendance data.
- Attendance Service must not directly query employee, department, identity, reporting, or notification databases.
- Reporting and Notification Services must consume Attendance Service events instead of reading Attendance Service tables.
