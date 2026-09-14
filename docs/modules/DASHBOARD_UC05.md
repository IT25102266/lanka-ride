# Admin Dashboard & Reporting (UC-05)

**Owner:** WICKRAMASINGHE R.D.W.K.G.S (IT24101008)  
**GitHub:** `IT241021008`  
**Branch:** `feat/wickramasinghe-dashboard`  
**Package:** `com.lankaride.dashboard`

## Scope

- Staff operations dashboard with live fleet / booking stats
- Role-gated workspace quick links
- Daily / monthly / annual revenue reports from live DB
- Branch comparison (Colombo / Kandy / Galle) + utilization
- Vehicle location overview board
- Print / export via browser print

## Main types

| Class | Role |
|-------|------|
| `DashboardController` | `/dashboard` staff home + `/app` customer trips shell |
| `ReportService` | Period metrics, branch rows, locations |
| `ReportsController` | `/reports` filters and view model |

## Views (frontend owned on this branch)

- `views/dashboard/index.jsp` — staff operations dashboard  
- `views/dashboard/reports.jsp` — filters, KPIs, branch table, locations  
- `views/dashboard/customer.jsp` — customer My trips home  

## Demo flow

1. Login as `finance` / `operations` / `admin` → `/dashboard`  
2. Open Reports → switch daily / monthly / annual  
3. Filter by branch; print if needed  
