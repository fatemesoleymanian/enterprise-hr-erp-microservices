# ADR-002: Use Eureka For Service Discovery

## Status

Accepted

## Date

2026-06-01

## Context

The ERP system is built as a set of Spring Boot microservices. External requests enter through the API Gateway, and backend services need a simple way to register themselves and discover each other during local development and in the project MVP.

The team wants a solution that is easy to understand, easy to configure in a mono-repo, and familiar enough to support learning service-to-service communication patterns.

## Decision

Use Spring Cloud Netflix Eureka as the service discovery mechanism.

- `discovery-server` will run as the Eureka server.
- Backend services will register as Eureka clients.
- `api-gateway` will use service names such as `lb://identity-service` instead of hard-coded host and port values.

## Consequences

### Positive

- Services can be started independently and still find each other.
- The gateway can route to backend services by logical name.
- Local development is simpler than manually managing host and port mappings for every service.
- The architecture matches common Spring Cloud microservice patterns.

### Negative

- Eureka adds one more runtime component to the system.
- Services depend on discovery being available before they can fully register.
- The project must keep Eureka configuration consistent across services.

## Rules

- Discovery Server runs on port `8761`.
- Backend services register with Eureka using the shared service name configured in each module.
- The API Gateway routes to services by discovery name, not by hard-coded hostnames.
- Discovery metadata and route names must stay aligned with the service artifact names used in the repository.
