# Employee Service Specification

## Overview

**Service Name:** employee-service  
**Owner:** HR Developer  
**Service Port:** 8083  
**Database Port:** 5435

### Responsibility

Employee Service is responsible for managing employee profiles, employment status, department assignments, and manager relationships within the organization.

---

# Business Rules

## Identity & Uniqueness

- `employee_number` must be **unique**
- `email` must be **unique**

If duplication occurs, the API must return:

409 Conflict

---

## Employee Status

Employee status must be one of the following values:
ACTIVE

ON_LEAVE

SUSPENDED

TERMINATED

Rules:

- Status is mandatory.
- Default status on creation: `ACTIVE`.
- Status changes must publish a domain event.

---

## Department Reference

An employee may reference a department using:

department_id

Rules:

- Must reference a valid department.
- Changing department must publish an event.

Event:

EmployeeDepartmentChanged

---

## Manager Reference

An employee may reference another employee as their manager:

manager_employee_id

Rules:

- Manager must exist.
- An employee cannot be their own manager.
- Circular management relationships should be prevented.

---

## Authorization

Only the following roles can create or update employees:

ADMIN

HR_MANAGER

If a user without the required role attempts to perform these actions:

403 Forbidden

---

# Database Schema

## Table: employees

Fields:

- id
- user_id
- employee_number
- first_name
- last_name
- email
- job_title
- department_id
- manager_employee_id
- status
- hire_date
- created_at
- updated_at
- version

---

## Example SQL Schema

````sql
CREATE TABLE employees (
id UUID PRIMARY KEY,
user_id UUID NOT NULL,
employee_number VARCHAR(50) UNIQUE NOT NULL,
first_name VARCHAR(100) NOT NULL,
last_name VARCHAR(100) NOT NULL,
email VARCHAR(150) UNIQUE NOT NULL,
job_title VARCHAR(150),
department_id UUID,
manager_employee_id UUID,
status VARCHAR(20) NOT NULL,
hire_date DATE NOT NULL,
created_at TIMESTAMP NOT NULL,
updated_at TIMESTAMP NOT NULL,
version INTEGER NOT NULL DEFAULT 0,
CONSTRAINT fk_manager
FOREIGN KEY (manager_employee_id)
REFERENCES employees(id)
);

API Specification

Base path:


/api/employees

Create Employee

POST

/api/employees

Request:

{
  "userId": "uuid",
  "employeeNumber": "EMP-1001",
  "firstName": "Ali",
  "lastName": "Rezaei",
  "email": "ali@example.com",
  "jobTitle": "Backend Developer",
  "departmentId": "uuid",
  "managerEmployeeId": "uuid",
  "hireDate": "2026-06-01"
}

Response:

201 Created


Example:

{
  "id": "uuid",
  "status": "ACTIVE"
}

Errors:

    Duplicate employee number → 409
    Duplicate email → 409
    Unauthorized → 403

Get Employees

GET /api/employees

/api/employees

Response: 200 OK
Event published: EmployeeDepartmentChanged
Change Employee Status

PATCH

/api/employees/{id}/status
Request:
{
  "status": "SUSPENDED"
}

Response:
200 OK

Event published: EmployeeStatusChanged

Domain Events

EmployeeCreated

{
  "eventType": "EmployeeCreated",
  "employeeId": "uuid",
  "employeeNumber": "EMP-1001",
  "departmentId": "uuid",
  "occurredAt": "timestamp"
}

EmployeeDepartmentChanged

{
  "eventType": "EmployeeDepartmentChanged",
  "employeeId": "uuid",
  "oldDepartmentId": "uuid",
  "newDepartmentId": "uuid",
  "occurredAt": "timestamp"
}

EmployeeStatusChanged

{
  "eventType": "EmployeeStatusChanged",
  "employeeId": "uuid",
  "oldStatus": "ACTIVE",
  "newStatus": "SUSPENDED",
  "occurredAt": "timestamp"
}


Test Specification

The following behaviors must be tested:

    Create employee returns 201
    Duplicate employee number returns 409
    Duplicate email returns 409
    Changing department publishes EmployeeDepartmentChanged
    Changing status publishes EmployeeStatusChanged
    Missing employee returns 404


Git Commit Commands
Task 4.1
git add docs/specs/employee
git commit -m "docs(employee): add service spec"

Task 4.2
git add employee-service
git commit -m "feat(employee): scaffold service and schema"

Task 4.3
git add employee-service docs/specs/employee
git commit -m "feat(employee): add employee management APIs"

### Get Employee By Id

GET

/api/employees/{id}

Response:

200 OK

Example:

```json
{
  "id": "uuid",
  "userId": "uuid",
  "employeeNumber": "EMP-1001",
  "firstName": "Ali",
  "lastName": "Rezaei",
  "email": "ali@example.com",
  "jobTitle": "Backend Developer",
  "departmentId": "uuid",
  "managerEmployeeId": "uuid",
  "status": "ACTIVE",
  "hireDate": "2026-06-01"
}
````

Errors:

- Employee not found → 404

---

### Update Employee

PUT

/api/employees/{id}

Request:

```json
{
  "firstName": "Ali",
  "lastName": "Rezaei",
  "email": "ali@example.com",
  "jobTitle": "Senior Backend Developer",
  "departmentId": "uuid",
  "managerEmployeeId": "uuid"
}
```

Response:

200 OK

Errors:

- Employee not found → 404
- Duplicate employee number → 409
- Duplicate email → 409
- Unauthorized → 403

---

### Change Employee Department

PATCH

/api/employees/{id}/department

Request:

```json
{
  "departmentId": "uuid"
}
```

Response:

200 OK

Event published: EmployeeDepartmentChanged

Errors:

- Employee not found → 404
- Invalid department → 400
- Unauthorized → 403

```

```
