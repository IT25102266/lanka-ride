# Fleet Maintenance & Servicing (UC-01)

**Owner:** DE SILVA D.L.K.C (IT25102264)  
**Branch:** `feat/desilva-fleet`  
**Package:** `com.lankaride.fleet`

## Scope

- Maintenance records: service type, dates, cost, description, mechanics, status
- CRUD create / update / close
- Open maintenance → vehicle status `MAINTENANCE` (unavailable for booking)
- Close maintenance → vehicle reinstated `AVAILABLE`
- Warn when future bookings exist for that vehicle
- Service history view per record / vehicle

## Main types

| Class | Role |
|-------|------|
| `MaintenanceRecord` | Entity — service fields + status + vehicle link |
| `MaintenanceRecordRepository` | Persistence |
| `MaintenanceService` | Create/update/close + vehicle status sync |
| `MaintenanceController` | `/maintenance` UI routes |

## Views

- `views/fleet/list.jsp` — records list  
- `views/fleet/form.jsp` — create / edit  
- `views/fleet/detail.jsp` — history + close  

## Demo flow

1. `fleet` / admin opens Maintenance → add record for a vehicle  
2. Vehicle becomes unavailable for new bookings  
3. Close record → vehicle available again  
