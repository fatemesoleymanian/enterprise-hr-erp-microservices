CREATE TABLE employees (
                           id UUID PRIMARY KEY,
                           user_id UUID NOT NULL,
                           employee_number VARCHAR(50) UNIQUE NOT NULL,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email VARCHAR(150) UNIQUE NOT NULL,
                           job_title VARCHAR(150),
                           department_id UUID,
                           manager_employee_id UUID,
                           status VARCHAR(20) NOT NULL,
                           hire_date TIMESTAMP WITH TIME ZONE NOT NULL,
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           version INTEGER NOT NULL DEFAULT 0,

                           CONSTRAINT fk_manager
                               FOREIGN KEY (manager_employee_id)
                                   REFERENCES employees(id)
);

CREATE INDEX idx_employee_number
    ON employees(employee_number);
