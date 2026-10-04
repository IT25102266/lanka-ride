# Lanka Ride — 100% demo script

Database: Docker MySQL from `docker-compose.yml` (`lankaride` / `lankaride`, database `lanka_ride`).  
App: `docker compose up -d` then `./mvnw spring-boot:run`. Open http://localhost:8080.

## 1. Sakalasooriya — vehicle transfer

1. Login `fleet` / `fleet123`.
2. Open a vehicle (for example CAB-1001).
3. Transfer it to another branch and confirm the timestamped log on the same page.

## 2. De Silva — maintenance reminder

1. Stay as `fleet`.
2. Open Maintenance, add a record due today.
3. Click **Send due reminders**.
4. Login `admin` / `admin123` and open Alerts. The reminder email is in the log.

## 3. Samaranayake — booking and operations monitor

1. Login `customer` / `customer123`.
2. Book an available vehicle for tomorrow through the day after.
3. Login `supervisor` / `super123` and approve it.
4. Login `customer` again and pay the deposit and rental.
5. Login `fleet`, record pickup mileage and fuel `FULL`, then return with fuel `EMPTY`.
6. Login `operations` / `ops123` and open **Monitor**. The booking is flagged.

## 4. Kavindi — payment audit

1. Login `finance` / `finance123`.
2. Open Payments. Each row shows the account that recorded the charge (customer or staff).

## 5. Wickramasinghe — reports

1. Stay as `finance` or use `operations`.
2. Open Reports. Use **Print** and **Download CSV**.

## 6. Pahasara — notification retry

1. Login `admin`.
2. Open Alerts and use **Retry failed** if any row is `FAILED`.

Automated coverage: `./mvnw test` (H2). The running app keeps using Docker MySQL.
