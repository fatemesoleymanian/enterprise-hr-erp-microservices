# Project Walkthrough

## Problem

The project simulates an enterprise HR ERP backend for users, departments, employees, attendance, reporting, and notifications.

## Architecture

The system uses Spring Boot microservices, Spring Cloud Gateway, Eureka, PostgreSQL per service, Kafka events, Docker Compose, and GitHub Actions.

## Strong Interview Topics

- Service boundaries and database ownership.
- JWT authentication and role-based authorization.
- Kafka event design.
- Reporting through projections.
- Attendance business rules.
- Testcontainers integration testing.
- PR-based team workflow.
- CI/CD pipeline design.

## Demo Flow

1. Start the system with Docker Compose.
2. Log in as admin.
3. Create a department.
4. Create an employee.
5. Assign the employee to a department.
6. Check in late.
7. Show the attendance violation notification.
8. Show the monthly attendance report.
