# Booking & Reservation (UC-03)

**Owner:** SAMARANAYAKE P.I.S (IT24101349)  
**Branch:** `feat/samaranayake-booking`  
**Package:** `com.lankaride.booking`

## Scope

- Customer booking requests (vehicle, branch, pickup/return dates)
- Status workflow: Pending → Approved / Denied → Ongoing → Completed / Cancelled
- Staff single approval screen (approve / deny + reason)
- Cancel by customer or staff while Pending/Approved
- Block bookings when vehicle is under maintenance or dates overlap
- Return path integration: pickup / return mileage & fuel (via payments invoice screens)

## Main types

| Class | Role |
|-------|------|
| `Booking` | Entity — dates, status, payment status, mileage/fuel, invoice link fields |
| `BookingRepository` | Overlap queries + list/pending helpers |
| `BookingService` | Create, approve, deny, cancel, availability checks |
| `BookingController` | `/bookings` UI routes |

## Views

- `views/booking/list.jsp` — customer & staff lists  
- `views/booking/form.jsp` — new request  
- `views/booking/detail.jsp` — approval screen + pay CTA  

## Demo flow

1. Customer searches → books vehicle  
2. `supervisor` / admin opens pending → approve or deny  
3. Customer pays on invoice → staff records pickup/return  
