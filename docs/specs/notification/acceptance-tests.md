# Notification Acceptance Tests

## Attendance violation creates unread HR notification

Given Notification Service consumes an `AttendanceViolationDetected` event  
When the event is processed  
Then a `notifications` row is created  
And `recipient_role` is `HR`  
And `type` is `ATTENDANCE_VIOLATION`  
And `read` is `false`.

## Employee status change creates unread HR notification

Given Notification Service consumes an `EmployeeStatusChanged` event  
When the event is processed  
Then a `notifications` row is created  
And `recipient_role` is `HR`  
And `type` is `EMPLOYEE_STATUS_CHANGED`  
And `read` is `false`.

## Duplicate event does not create duplicate notification

Given Notification Service has already processed an event  
When the same event is delivered again  
Then no second notification is created.

## Notifications can be listed

Given Notification Service has notification records  
When HR requests `GET /api/notifications`  
Then the API returns matching notifications  
And unread notifications include `read` as `false`.

## Notifications can be filtered by read state

Given Notification Service has read and unread notification records  
When HR requests `GET /api/notifications?read=false`  
Then the API returns unread notifications only.

## Mark notification read

Given an unread notification exists  
When HR requests `PATCH /api/notifications/{id}/read`  
Then the notification `read` flag is set to `true`  
And `read_at` is populated.

## Mark read is idempotent

Given a notification is already read  
When HR requests `PATCH /api/notifications/{id}/read`  
Then the API returns the notification  
And the notification remains read.

## Missing notification returns not found

Given no notification exists for the requested ID  
When HR requests `PATCH /api/notifications/{id}/read`  
Then the API returns `404 Not Found`.

## Unauthenticated request is rejected

Given no valid JWT token  
When a client calls any notification endpoint  
Then the API returns `401 Unauthorized`.
