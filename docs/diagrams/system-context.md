# System Context

```mermaid
flowchart LR
    Client["API Client"] --> Gateway["API Gateway"]
    Gateway --> Identity["Identity Service"]
    Gateway --> Department["Department Service"]
    Gateway --> Employee["Employee Service"]
    Gateway --> Attendance["Attendance Service"]
    Gateway --> Reporting["Reporting Service"]
    Gateway --> Notification["Notification Service"]

    Identity --> Kafka["Kafka"]
    Department --> Kafka
    Employee --> Kafka
    Attendance --> Kafka

    Kafka --> Reporting
    Kafka --> Notification
```

## Notes

- External requests enter through the API Gateway.
- Identity handles authentication and user administration.
- Department and Employee publish domain events.
- Attendance publishes check-in, check-out, and violation events.
- Reporting and Notification consume Kafka events and build local read models.
