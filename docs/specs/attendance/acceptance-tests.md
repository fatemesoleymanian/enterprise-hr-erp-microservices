# Attendance Acceptance Tests

## Check-in succeeds

Given an authenticated active employee with no attendance record today  
When the employee checks in  
Then the API returns `201 Created`  
And the response contains the attendance record ID, employee ID, attendance date, check-in time, and statuses  
And `AttendanceCheckedIn` is published.

## Duplicate check-in fails

Given an employee has already checked in today  
When the employee checks in again  
Then the API returns `409 Conflict`  
And no duplicate attendance record is created  
And no second `AttendanceCheckedIn` event is published.

## Late check-in creates violation

Given workday starts at `09:00`  
And the late threshold is `09:15`  
When the employee checks in at `09:20`  
Then the attendance record includes `LATE`  
And `AttendanceCheckedIn` is published  
And `AttendanceViolationDetected` is published with violation type `LATE`.

## Check-in at late threshold is present

Given the late threshold is `09:15`  
When the employee checks in at `09:15`  
Then the attendance record includes `PRESENT`  
And the attendance record does not include `LATE`.

## Check-out succeeds

Given an employee checked in today  
When the employee checks out after `16:00`  
Then the API returns `200 OK`  
And the response contains check-out time and worked minutes  
And `AttendanceCheckedOut` is published.

## Check-out without check-in fails

Given an employee has no attendance record today  
When the employee checks out  
Then the API returns `404 Not Found`  
And no `AttendanceCheckedOut` event is published.

## Early check-out creates violation

Given early leave threshold is `16:00`  
And an employee checked in today  
When the employee checks out at `15:59`  
Then the attendance record includes `EARLY_LEAVE`  
And `AttendanceCheckedOut` is published  
And `AttendanceViolationDetected` is published with violation type `EARLY_LEAVE`.

## Check-out at early leave threshold is normal

Given early leave threshold is `16:00`  
And an employee checked in today  
When the employee checks out at `16:00`  
Then the attendance record does not include `EARLY_LEAVE`.

## Monthly summary returns aggregates

Given an employee has attendance records for a selected month  
When HR requests the monthly summary  
Then the API returns present days, late days, early leave days, absent days, and total worked minutes.

## Unauthenticated request is rejected

Given no valid JWT token  
When a client calls any attendance endpoint  
Then the API returns `401 Unauthorized`.
