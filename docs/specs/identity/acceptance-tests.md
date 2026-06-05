# Identity Acceptance Tests

## Login succeeds

- Given an active user with a valid password
- When the user logs in
- Then the API returns 200
- And the response contains a JWT access token.

## Login rejects disabled user

- Given a disabled user
- When the user logs in with a valid password
- Then the API returns 403.

## Admin creates user

- Given an ADMIN token
- When the admin creates a user
- Then the API returns 201
- And a UserCreated event is published.