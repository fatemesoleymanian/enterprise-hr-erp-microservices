# Code Review Guidelines

Code review protects architecture, correctness, and team learning.

## What Reviewers Check

- Business behavior matches the ticket and acceptance criteria.
- Service boundaries are respected.
- No service directly accesses another service's database.
- API contracts are clear and stable.
- Error handling is explicit and consistent.
- Security rules are enforced for protected operations.
- Database migrations are safe and reviewed.
- Kafka event names and payloads are documented.
- Tests cover important behavior.
- Naming and structure are readable.

## Review Style

- Ask questions before requesting large rewrites.
- Explain the reason behind requested changes.
- Prefer small, actionable comments.
- Separate blocking issues from optional suggestions.
- Approve only when the Definition of Done is satisfied.

## Pull Request Size

Prefer small pull requests. A good PR usually changes one feature, one service setup task, or one documentation area.

Large pull requests should be split when they combine unrelated concerns.
