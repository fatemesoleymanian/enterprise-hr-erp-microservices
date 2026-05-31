# Branching Strategy

This repository uses Git Flow for collaborative development.

## Branches

- `main`: stable production-ready code only.
- `develop`: integration branch for completed and reviewed features.
- `feature/ERP-123-short-name`: feature branches created from `develop`.
- `release/x.y.z`: release stabilization branches.
- `hotfix/x.y.z`: urgent fixes created from `main`.

## Rules

- Do not commit directly to `main`.
- Do not commit directly to `develop`.
- Every change must go through a pull request.
- Feature branches must be created from `develop`.
- Pull requests must reference a Jira ticket or documented GitHub issue.
- Pull requests require at least one approval before merge.
- CI checks must pass before merge.

## Branch Naming

Use this format:

```text
feature/ERP-123-short-description
fix/ERP-123-short-description
docs/ERP-123-short-description
ci/ERP-123-short-description
```

Examples:

```text
feature/ERP-101-scaffold-discovery-server
feature/ERP-201-add-identity-service-spec
fix/ERP-504-prevent-duplicate-check-in
docs/ERP-001-add-team-workflow
```
