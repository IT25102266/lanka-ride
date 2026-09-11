# Lanka Ride Rentals

Web-based vehicle rental system (SE2030) — **~75% + redesigned UI**.

Stack: **Java 17 · Spring Boot 3 · JSP/HTML (Bootstrap 5) · MySQL 8 (Docker)**

UI: ROFI-inspired full-bleed home (forest + gold), role-split shells (`/app` for customers, `/dashboard` for staff).

## Quick start

### 0. Prerequisites

- Docker (for MySQL)
- JDK 17+
- Maven 3.9+

```bash
export JAVA_HOME=$HOME/.local/jdk/jdk-17.0.20.1+1
export PATH="$JAVA_HOME/bin:$HOME/.local/maven/apache-maven-3.9.9/bin:$PATH"
```

### 1. Start MySQL

```bash
docker compose up -d
```

### 2. Run the app

```bash
mvn spring-boot:run
```

Open http://localhost:8080

### 3. Demo logins

| User | Password | Lands on | Access |
|------|----------|----------|--------|
| `customer` | `customer123` | `/app` My trips | Search, book, pay, support |
| `supervisor` | `super123` | `/dashboard` | Approvals, pickup/return |
| `fleet` | `fleet123` | `/dashboard` | Vehicles + maintenance |
| `finance` | `finance123` | `/dashboard` | Payments, refunds, reports |
| `operations` | `ops123` | `/dashboard` | Approvals + reports |
| `admin` | `admin123` | `/dashboard` | Full admin |

Public **Register** creates **CUSTOMER** only. Staff accounts come from seed or `/admin`.

## What works

- Marketing home with live search (branch + dates → available vehicles)
- Login / register / logout / password reset
- Role-gated dashboards (customers cannot open staff dashboard or `/admin`)
- Vehicle browse/CRUD, maintenance, bookings, payments, reports, support

See [docs/DEVELOPMENT_PLAN.md](docs/DEVELOPMENT_PLAN.md).
