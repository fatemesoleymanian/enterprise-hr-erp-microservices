# Department Events

Department Service publishes domain events to Kafka after successful department state changes.

## Event Envelope

All events use a shared envelope.

```json
{
  "eventId": "f3ab8c5d-b8f8-4b19-9e35-4d83c57b02a2",
  "eventType": "DepartmentCreated",
  "occurredAt": "2026-06-01T04:45:00Z",
  "version": 1,
  "producer": "department-service",
  "correlationId": "7c0bd8c2-d29d-489f-8dbf-3f913f1fd89e",
  "payload": {}
}
```

## Topics

Initial topic naming:

```text
department.events
```

Organization, Reporting, and Notification services may consume department events from this topic.

## DepartmentCreated

Published when a department is successfully created.

```json
{
  "eventType": "DepartmentCreated",
  "payload": {
    "departmentId": "9b7d67f3-35a3-4c3f-bef4-f6e5d3d9b6e2",
    "name": "Engineering",
    "description": "Software Development Department",
    "parentDepartmentId": null,
    "managerUserId": null,
    "createdAt": "2026-06-01T04:45:00Z"
  }
}
```

## DepartmentManagerAssigned

Published when a manager is assigned to a department.

```json
{
  "eventType": "DepartmentManagerAssigned",
  "payload": {
    "departmentId": "9b7d67f3-35a3-4c3f-bef4-f6e5d3d9b6e2",
    "managerUserId": "a1d4f7f9-42b7-4fd2-90b6-8dcf2a7e8d51",
    "assignedAt": "2026-06-01T05:00:00Z"
  }
}
```

## Consumers

Expected consumers:

- Reporting Service updates department reporting projections.
- Notification Service creates department assignment notifications.

## Reliability Expectations

- Events are published only after the department database change succeeds.
- Consumers must be idempotent because Kafka events may be delivered more than once.
- Event payloads must include stable IDs so consumers can safely update projections.

```

```
