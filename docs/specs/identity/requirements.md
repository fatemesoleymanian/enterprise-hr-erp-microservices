# Identity Service Requirements

Identity Service owns users, credentials, roles, and authentication.

Roles:

- ADMIN
- HR_MANAGER
- DEPARTMENT_MANAGER
- EMPLOYEE

Business rules:

- Passwords are stored only as BCrypt hashes.
- Disabled users cannot log in.
- Login returns a JWT access token.
- A user can have one or more roles.
- Only ADMIN can create users and change roles.
