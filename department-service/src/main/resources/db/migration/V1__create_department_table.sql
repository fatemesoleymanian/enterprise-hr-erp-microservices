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