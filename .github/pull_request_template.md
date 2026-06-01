## Jira Ticket

ERP-

## Summary

Describe the change in 2-4 sentences.

## Type

- [ ] Feature
- [ ] Bug fix
- [ ] Test
- [ ] Documentation
- [ ] Infrastructure
- [ ] Refactor

## Service Or Area

- [ ] API Gateway
- [ ] Discovery Server
- [ ] Identity Service
- [ ] Department Service
- [ ] Employee Service
- [ ] Attendance Service
- [ ] Reporting Service
- [ ] Notification Service
- [ ] Common Library
- [ ] Documentation
- [ ] CI/CD

## Testing Evidence

Commands run:

```text
./mvnw test
```

Result:

```text
BUILD SUCCESS
```

## Checklist

- [ ] The change satisfies the ticket acceptance criteria.
- [ ] Tests were added or updated when needed.
- [ ] API documentation was updated when needed.
- [ ] Database migration was added when needed.
- [ ] Kafka event documentation was updated when needed.
- [ ] No service reads or writes another service's database.
- [ ] I reviewed my own changes before requesting review.
