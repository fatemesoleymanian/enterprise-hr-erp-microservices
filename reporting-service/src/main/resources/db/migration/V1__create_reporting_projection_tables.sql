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
