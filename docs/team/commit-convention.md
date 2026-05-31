# Commit Convention

This project uses Conventional Commits with optional service scope.

## Format

```text
<type>(<scope>): <message>
```

## Types

- `feat`: new feature
- `fix`: bug fix
- `test`: tests
- `docs`: documentation
- `ci`: CI/CD changes
- `chore`: repository maintenance
- `refactor`: code change without behavior change
- `build`: build system or dependency changes

## Scopes

Use the service or area as the scope:

```text
identity
employee
department
attendance
reporting
notification
gateway
discovery
common
team
architecture
jira
```

## Examples

```text
feat(identity): add login endpoint
fix(attendance): prevent duplicate check-in
test(employee): add employee creation integration test
docs(architecture): add monorepo ADR
ci: add GitHub Actions build workflow
chore(team): add pull request template
```
