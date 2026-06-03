# Attendance Database Schema

Attendance Service owns `attendance_db`.

No other service may read or write this database directly.

## Tables

### attendance_policies

Stores attendance policy settings used by Attendance Service.

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `id` | UUID | Primary key | Policy identifier |
| `name` | VARCHAR(120) | Not null | Human-readable policy name |
| `workday_start` | TIME | Not null | Expected workday start time |
| `late_after` | TIME | Not null | Check-in after this time is late |
| `early_leave_before` | TIME | Not null | Check-out before this time is early leave |
| `active` | BOOLEAN | Not null | Whether this policy is active |
| `created_at` | TIMESTAMP WITH TIME ZONE | Not null | Creation timestamp |
| `updated_at` | TIMESTAMP WITH TIME ZONE | Not null | Last update timestamp |
| `version` | BIGINT | Not null | Optimistic locking version |

### attendance_records

Stores one attendance record per employee per date.

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `id` | UUID | Primary key | Attendance record identifier |
| `employee_id` | UUID | Not null | Employee identifier from Employee Service |
| `attendance_date` | DATE | Not null | Work date |
| `check_in_at` | TIMESTAMP WITH TIME ZONE | Nullable | Check-in timestamp |
| `check_out_at` | TIMESTAMP WITH TIME ZONE | Nullable | Check-out timestamp |
| `status` | VARCHAR(80) | Not null | Attendance statuses for the date |
| `worked_minutes` | INTEGER | Not null, default 0 | Minutes worked after check-out |
| `created_at` | TIMESTAMP WITH TIME ZONE | Not null | Creation timestamp |
| `updated_at` | TIMESTAMP WITH TIME ZONE | Not null | Last update timestamp |
| `version` | BIGINT | Not null | Optimistic locking version |

## Constraints

```sql
CONSTRAINT uq_attendance_employee_date UNIQUE (employee_id, attendance_date)
```

This constraint enforces one attendance record per employee per date.

## Indexes

```sql
CREATE INDEX idx_attendance_employee ON attendance_records(employee_id);
CREATE INDEX idx_attendance_date ON attendance_records(attendance_date);
CREATE INDEX idx_attendance_status ON attendance_records(status);
```

## Initial Migration

```sql
CREATE TABLE attendance_policies (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    workday_start TIME NOT NULL,
    late_after TIME NOT NULL,
    early_leave_before TIME NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL
);

CREATE TABLE attendance_records (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_at TIMESTAMP WITH TIME ZONE,
    check_out_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(80) NOT NULL,
    worked_minutes INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT uq_attendance_employee_date UNIQUE (employee_id, attendance_date)
);

CREATE INDEX idx_attendance_employee ON attendance_records(employee_id);
CREATE INDEX idx_attendance_date ON attendance_records(attendance_date);
CREATE INDEX idx_attendance_status ON attendance_records(status);
```
