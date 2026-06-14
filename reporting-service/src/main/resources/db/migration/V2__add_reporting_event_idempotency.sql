CREATE TABLE processed_reporting_events (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE attendance_report_contributions (
    attendance_record_id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    attendance_date DATE NOT NULL,
    present BOOLEAN NOT NULL,
    late BOOLEAN NOT NULL,
    early_leave BOOLEAN NOT NULL,
    absent BOOLEAN NOT NULL,
    worked_minutes INTEGER NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_attendance_contribution_employee_date
    ON attendance_report_contributions(employee_id, attendance_date);
