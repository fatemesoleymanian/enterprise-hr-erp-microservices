# Reporting Acceptance Tests

## Employee projection is created

Given Reporting Service consumes an `EmployeeCreated` event  
When the event is processed  
Then an `employee_report_views` row is created  
And the row contains employee ID, department ID, status, job title, and update time.

## Employee department projection is updated

Given an employee projection exists  
When Reporting Service consumes an `EmployeeDepartmentChanged` event  
Then the employee projection department ID is updated  
And no operational Employee Service database is queried.

## Employee status summary returns counts

Given Reporting Service has employee projections with multiple statuses  
When HR requests `GET /api/reports/employees/status-summary`  
Then the API returns total employees and counts per status.

## Department projection is created

Given Reporting Service consumes a `DepartmentCreated` event  
When the event is processed  
Then a `department_report_views` row is created  
And the row contains department ID, name, manager user ID, and update time.

## Department manager projection is updated

Given a department projection exists  
When Reporting Service consumes a `DepartmentManagerAssigned` event  
Then the department projection manager user ID is updated.

## Department headcount report counts active employees

Given Reporting Service has department projections and employee projections  
When HR requests `GET /api/reports/departments/headcount`  
Then the API returns active employee counts grouped by department.

## Attendance monthly projection is updated

Given Reporting Service consumes an `AttendanceCheckedOut` event  
When the event is processed  
Then an `attendance_monthly_report_views` row is created or updated  
And the row includes present days, late days, early leave days, absent days, and total worked minutes.

## Monthly attendance report returns rows

Given Reporting Service has monthly attendance projections for a selected month  
When HR requests `GET /api/reports/attendance/monthly`  
Then the API returns monthly attendance rows for matching employees.

## Department attendance report returns totals

Given Reporting Service has employee and attendance projections for a department  
When HR requests `GET /api/reports/departments/{departmentId}/attendance`  
Then the API returns employee attendance rows and department-level totals.

## Duplicate event does not double count

Given Reporting Service has already processed an attendance event  
When the same event is delivered again  
Then the monthly attendance projection is not double-counted.

## Unauthenticated request is rejected

Given no valid JWT token  
When a client calls any reporting endpoint  
Then the API returns `401 Unauthorized`.
