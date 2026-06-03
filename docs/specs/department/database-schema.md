# Department Database Schema

Department Service owns `department_db`.

No other service may read or write this database directly.

## Tables

### departments

Stores organizational department information and department hierarchy.

| Column                 | Type                     | Constraints      | Description                           |
| ---------------------- | ------------------------ | ---------------- | ------------------------------------- |
| `id`                   | UUID                     | Primary key      | Department identifier                 |
| `name`                 | VARCHAR(255)             | Not null, unique | Department name                       |
| `description`          | TEXT                     | Nullable         | Department description                |
| `parent_department_id` | BIGINT                   | Nullable         | Parent department identifier          |
| `manager_user_id`      | BIGINT                   | Nullable         | User identifier of department manager |
| `created_at`           | TIMESTAMP WITH TIME ZONE | Not null         | Creation timestamp                    |
| `updated_at`           | TIMESTAMP WITH TIME ZONE | Not null         | Last update timestamp                 |
| `version`              | BIGINT                   | Not null         | Optimistic locking version            |

## Constraints

```sql
CONSTRAINT uq_department_name UNIQUE (name),
CONSTRAINT fk_department_parent
    FOREIGN KEY (parent_department_id)
    REFERENCES departments(id)
```

The unique constraint ensures department names remain unique across the organization.

The foreign key constraint enforces valid department hierarchy relationships.

## Indexes

```sql
CREATE INDEX idx_department_name ON departments(name);
CREATE INDEX idx_department_parent ON departments(parent_department_id);
CREATE INDEX idx_department_manager ON departments(manager_user_id);
```

## Initial Migration

```sql
CREATE TABLE departments (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_department_id UUID,
    manager_user_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL,

    CONSTRAINT uq_department_name UNIQUE (name),

    CONSTRAINT fk_department_parent
        FOREIGN KEY (parent_department_id)
        REFERENCES departments(id)
);

CREATE INDEX idx_department_name
    ON departments(name);

CREATE INDEX idx_department_parent
    ON departments(parent_department_id);

CREATE INDEX idx_department_manager
    ON departments(manager_user_id);
```
