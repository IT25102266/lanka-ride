# Payment & Billing (UC-04)

**Owner:** KAVINDI P.D.N (IT25102265)  
**Branch:** `feat/kavindi-payment`  
**Package:** `com.lankaride.payment`

## Scope

- Mock sandbox gateway: pay deposit + rental after approval
- Mark Paid; store payment history; HTML invoice
- Refunds for authorized cancellations (finance / admin)
- Late fee / damage charges on return
- Pickup & return mileage/fuel checklist (fees posted as transactions)

## Main types

| Class | Role |
|-------|------|
| `PaymentTransaction` | Entity — type, amount, gateway ref, success |
| `PaymentTransactionRepository` | Ledger queries |
| `PaymentService` | Pay, pickup, return fees, refund |
| `PaymentController` | `/payments` routes |

## Views (frontend owned on this branch)

- `views/payment/list.jsp` — staff transaction ledger  
- `views/payment/invoice.jsp` — pay / pickup / return / refund  

## Demo flow

1. Approved booking → customer pays on invoice  
2. Staff records pickup mileage/fuel  
3. Staff completes return (optional late/damage)  
4. Finance can refund when allowed  
