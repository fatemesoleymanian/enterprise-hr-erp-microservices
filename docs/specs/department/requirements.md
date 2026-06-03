# Department Service Requirements

Department Service owns department hierarchy management, department manager assignment, and department metadata.

## Scope

- Create departments.
- Update departments.
- View department details.
- View department hierarchy.
- Assign department managers.
- Publish department domain events.

## Out Of Scope

- Employee profile ownership.
- Attendance management.
- Payroll calculation.
- Leave management.
- Department deletion.
- User authentication and authorization ownership.

## Business Rules

- Department name must be unique.
- A department can have one parent department.
- A department can have one manager user id.
- A department may exist without a manager.
- Parent department must exist before assignment.
- A department cannot be its own parent.
- Department deletion is not part of MVP.
- Only ADMIN and HR_MANAGER can create departments.
- Only ADMIN and HR_MANAGER can update departments.
- Only ADMIN and HR_MANAGER can assign department managers.
- Duplicate department names return `409 Conflict`.
- Accessing a non-existing department returns `404 Not Found`.

## Security Expectations

- Unauthenticated requests return `401 Unauthorized`.
- Only ADMIN and HR_MANAGER may create departments.
- Only ADMIN and HR_MANAGER may update departments.
- Only ADMIN and HR_MANAGER may assign managers.
- Authenticated users may view department information.

## Dependencies

- PostgreSQL stores department data.
- Kafka is used to publish department events.
- API Gateway exposes Department Service under `/api/departments/**`.
- Eureka registers the service as `department-service`.

## Service Boundary Rules

- Department Service owns only department data.
- Department Service must not directly query employee, attendance, payroll, reporting, or notification databases.
- Other services must consume Department Service events instead of reading Department Service tables.
