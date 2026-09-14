# Customer Support & Notifications (UC-06)

**Owner:** PAHASARA Y.A.P (IT24102871)  
**Branch:** `feat/pahasara-support`  
**Package:** `com.lankaride.support`

## Scope

- Support tickets: Open → In Progress → Resolved → Closed
- Customer submit + staff respond workflow
- Notification service stub (email log for booking/payment events)
- Password reset / account recovery
- Admin add branch / user (scalability)

## Main types

| Class | Role |
|-------|------|
| `SupportTicket` | Ticket entity |
| `SupportController` | `/support` ticket UI |
| `NotificationService` / `NotificationLog` | Sandbox email log |
| `PasswordResetController` | Forgot / reset password |
| `AdminController` | `/admin` branches & users |

## Views (frontend owned on this branch)

- `views/support/list.jsp`, `form.jsp`, `detail.jsp` — tickets  
- `views/support/notifications.jsp` — notification log  
- `views/support/admin.jsp` — add branch/user  
- `views/auth/forgot-password.jsp`, `reset-password.jsp` — recovery  

## Demo flow

1. Customer opens Support → new ticket  
2. Staff responds and updates status  
3. Admin views notification log / adds branch or user  
4. Forgot password → token in notification log → reset  
