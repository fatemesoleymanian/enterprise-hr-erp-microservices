# Definition Of Done

A ticket is done only when all required items are complete.

## Required For Every Ticket

- Requirements are documented in the ticket or related spec.
- Implementation matches the acceptance criteria.
- Pull request references the ticket ID.
- Pull request explains what changed.
- Pull request includes testing evidence.
- At least one teammate approved the pull request.
- CI checks pass before merge.
- Reviewer comments are resolved.

## Required When API Changes

- API behavior is documented.
- Request and response examples are updated.
- Error responses are documented.
- OpenAPI or Swagger documentation is updated when available.

## Required When Database Changes

- Database migration is included.
- Migration has a clear name.
- Required constraints and indexes are added.
- No service reads or writes another service's database.

## Required When Business Rules Change

- Unit tests cover the business rules.
- Edge cases are tested.
- Existing related tests still pass.

## Required When Kafka Events Change

- Event name and payload are documented.
- Producer behavior is tested where possible.
- Consumer behavior is tested where possible.
- Backward compatibility is considered.
