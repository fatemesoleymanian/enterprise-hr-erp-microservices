# Reporting Database Schema

Reporting Service owns `reporting_db`.

No other service may read or write this database directly.

## Tables

### employee_report_views

Stores employee data needed for reporting. Rows are created and updated from employee events.

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `employee_id` | UUID | Primary key | Employee identifier from Employee Service |
| `department_id` | UUID | Nullable | Current department identifier |
| `status` | VARCHAR(30) | Not null | Current employee status |
| `job_title` | VARCHAR(150) | Nullable | Current job title |
| `updated_at` | TIMESTAMP WITH TIME ZONE | Not null | Last projection update timestamp |

### department_report_views

Stores department data needed for reporting. Rows are created and updated from department events.

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `department_id` | UUID | Primary key | Department identifier from Department Service |
| `name` | VARCHAR(150) | Not null | Department name |
| `manager_user_id` | UUID | Nullable | Current department manager user identifier |
| `updated_at` | TIMESTAMP WITH TIME ZONE | Not null | Last projection update timestamp |

### attendance_monthly_report_views

Stores monthly attendance aggregates per employee. Rows are updated from attendance events.

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `id` | UUID | Primary key | Projection row identifier |
| `employee_id` | UUID | Not null | Employee identifier from Attendance Service event |
| `report_year` | INTEGER | Not null | Calendar year |
| `report_month` | INTEGER | Not null | Calendar month from 1 to 12 |
| `present_days` | INTEGER | Not null | Present day count |
| `late_days` | INTEGER | Not null | Late day count |
| `early_leave_days` | INTEGER | Not null | Early leave day count |
| `absent_days` | INTEGER | Not null | Absent day count |
| `total_worked_minutes` | INTEGER | Not null | Total worked minutes for the month |
| `updated_at` | TIMESTAMP WITH TIME ZONE | Not null | Last projection update timestamp |

### processed_reporting_events

Stores consumed event IDs so redelivered Kafka events do not update projections twice.

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `event_id` | UUID | Primary key | Stable event envelope identifier |
| `event_type` | VARCHAR(100) | Not null | Consumed event type |
| `processed_at` | TIMESTAMP WITH TIME ZONE | Not null | Time the event was applied |

### attendance_report_contributions

Stores the current contribution of each attendance record to its monthly aggregate. This allows check-in, check-out, and violation events for the same record to update totals without double-counting.

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `attendance_record_id` | UUID | Primary key | Attendance record identifier from the event payload |
| `employee_id` | UUID | Not null | Employee identifier from the event payload |
| `attendance_date` | DATE | Not null | Date used to select the monthly aggregate |
| `present` | BOOLEAN | Not null | Whether the record contributes one present day |
| `late` | BOOLEAN | Not null | Whether the record contributes one late day |
| `early_leave` | BOOLEAN | Not null | Whether the record contributes one early-leave day |
| `absent` | BOOLEAN | Not null | Whether the record contributes one absent day |
| `worked_minutes` | INTEGER | Not null | Worked minutes contributed by the record |
| `updated_at` | TIMESTAMP WITH TIME ZONE | Not null | Last event time applied to the contribution |

## Constraints

```sql
CONSTRAINT uq_attendance_monthly_employee_month UNIQUE (employee_id, report_year, report_month)
```

This constraint enforces one monthly attendance projection per employee per month.

## Indexes

```sql
CREATE INDEX idx_employee_report_department ON employee_report_views(department_id);
CREATE INDEX idx_employee_report_status ON employee_report_views(status);
CREATE INDEX idx_department_report_manager ON department_report_views(manager_user_id);
CREATE INDEX idx_attendance_monthly_employee ON attendance_monthly_report_views(employee_id);
CREATE INDEX idx_attendance_monthly_year_month ON attendance_monthly_report_views(report_year, report_month);
CREATE INDEX idx_attendance_contribution_employee_date ON attendance_report_contributions(employee_id, attendance_date);
```

## Initial Migration

```sql
CREATE TABLE employee_report_views (
    employee_id UUID PRIMARY KEY,
    department_id UUID,
    status VARCHAR(30) NOT NULL,
    job_title VARCHAR(150),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE department_report_views (
    department_id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    manager_user_id UUID,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE attendance_monthly_report_views (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    report_year INTEGER NOT NULL,
    report_month INTEGER NOT NULL,
    present_days INTEGER NOT NULL,
    late_days INTEGER NOT NULL,
    early_leave_days INTEGER NOT NULL,
    absent_days INTEGER NOT NULL,
    total_worked_minutes INTEGER NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_attendance_monthly_employee_month UNIQUE (employee_id, report_year, report_month)
);

CREATE INDEX idx_employee_report_department ON employee_report_views(department_id);
CREATE INDEX idx_employee_report_status ON employee_report_views(status);
CREATE INDEX idx_department_report_manager ON department_report_views(manager_user_id);
CREATE INDEX idx_attendance_monthly_employee ON attendance_monthly_report_views(employee_id);
CREATE INDEX idx_attendance_monthly_year_month ON attendance_monthly_report_views(report_year, report_month);
```

## Idempotency Migration

`V2__add_reporting_event_idempotency.sql` creates `processed_reporting_events` and `attendance_report_contributions` after the initial projection tables.
