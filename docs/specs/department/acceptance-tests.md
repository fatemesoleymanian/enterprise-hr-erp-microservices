# Department Acceptance Tests

## Create department succeeds

Given an authenticated user with role `ADMIN` or `HR_MANAGER`
And the department name does not already exist
When the user creates a department
Then the API returns `201 Created`
And the response contains the department ID, name, and metadata
And `DepartmentCreated` is published.

## Duplicate department name fails

Given a department with the same name already exists
When the user creates another department with that name
Then the API returns `409 Conflict`
And no duplicate department is created
And no second `DepartmentCreated` event is published.

## Get department succeeds

Given a department exists
When an authenticated user requests the department by ID
Then the API returns `200 OK`
And the response contains the department information.

## Missing department returns not found

Given a department does not exist
When a client requests the department by ID
Then the API returns `404 Not Found`.

## Update department succeeds

Given a department exists
And the user has role `ADMIN` or `HR_MANAGER`
When the user updates the department information
Then the API returns `200 OK`
And the department data is updated.

## Assign manager succeeds

Given a department exists
And the user has role `ADMIN` or `HR_MANAGER`
When the user assigns a manager user ID to the department
Then the API returns `200 OK`
And the response contains the updated manager user ID
And `DepartmentManagerAssigned` is published.

## Assign manager to missing department fails

Given a department does not exist
When the user assigns a manager
Then the API returns `404 Not Found`
And no `DepartmentManagerAssigned` event is published.

## Unauthorized user cannot create department

Given an authenticated user without role `ADMIN` or `HR_MANAGER`
When the user creates a department
Then the API returns `403 Forbidden`.

## Unauthorized user cannot update department

Given an authenticated user without role `ADMIN` or `HR_MANAGER`
When the user updates a department
Then the API returns `403 Forbidden`.

## Unauthenticated request is rejected

Given no valid JWT token
When a client calls any department endpoint
Then the API returns `401 Unauthorized`.
