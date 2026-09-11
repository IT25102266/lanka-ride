# Lanka Ride Rentals — Multi-Member Development Plan

**Project:** Web-Based Vehicle Rental System  
**Group:** 2026-Y2-S1-MLB-B13G2-05  
**Repo (current origin):** `https://github.com/IT25102266/lanka-ride.git`  
**Status:** **0% — scaffolding only** (README + docs; no application code yet)

This plan maps **one major function → one member → one feature branch**, with milestones at **0% / 25% / 50% / 75% / 100%**. You develop on this laptop; each member’s commits live on their branch; you switch GitHub accounts only when **pushing**.

**Identity & credentials:** see [`MEMBER_GIT_IDENTITIES.md`](./MEMBER_GIT_IDENTITIES.md).  
Credentials come later; until then use placeholders. **Before every member `add`/`commit`, switch/check that member’s Git name+email** (never leave Udith’s global identity on feature commits).

---

## 1. Team & Ownership (from proposal + use cases)

| # | Member | Student ID | Major Function | Use Case | Scrum Role |
|---|--------|------------|----------------|----------|------------|
| 1 | **SAKALASOORIYA S.A.A.A** | IT25102266 | Vehicle Management (+ Sprint-1 auth share) | UC-02 | Dev — Vehicle & SysAdmin |
| 2 | **DE SILVA D.L.K.C** | IT25102264 | Fleet Maintenance & Servicing | UC-01 | Dev — Fleet Maintenance |
| 3 | **SAMARANAYAKE P.I.S** | IT24101349 | Booking & Reservation (+ return mileage) | UC-03 | Scrum Master + Booking |
| 4 | **KAVINDI P.D.N** | IT25102265 | Payment & Billing | UC-04 | Dev — Payment |
| 5 | **WICKRAMASINGHE R.D.W.K.G.S** | IT24101008 | Admin Dashboard & Reporting | UC-05 | Product Owner + Reporting |
| 6 | **PAHASARA Y.A.P** | IT24102871 | Customer Support & Notifications | UC-06 | Dev — Support / UI-QA |

**Shared / minor functions (not sole-owned):** login/logout, password reset, customer profiles, search/filter (owned under Vehicle but used by Booking), RBAC, system-wide alerts hooks.

---

## 2. Technology (from docs + what you must confirm)

### Confirmed in documents
- **Java web application** (browser clients)
- Central DB shared across **Colombo / Kandy / Galle**
- Payment gateway (**sandbox/test**)
- Email/SMS notification service
- Role-based access (Customer, Booking Supervisor, Fleet Coordinator, Finance/Admin, Operations Manager, System Admin)
- Agile / 4 sprints

### Not specified in docs — **you must decide / tell me**
See **Section 8 — What you need to give me**.

**Recommended default stack** (if lecturer has no mandatory stack):

| Layer | Suggestion |
|-------|------------|
| Backend | Java 17+, Spring Boot 3, Spring Security, Spring Data JPA |
| Frontend | Thymeleaf + Bootstrap **or** React/Vite SPA calling REST |
| DB | MySQL 8 |
| Build | Maven |
| Payments | Stripe / PayHere / mock sandbox adapter |
| Email | JavaMail + Mailtrap (dev) |
| SMS | Twilio mock / log-to-console in demo |
| Hosting (demo) | Local + optional Render/Railway later |

---

## 3. Git branch strategy (one person = one long-lived feature branch)

```
main                          ← integration / demos / final merge
├── feat/sakalasuriya-vehicle
├── feat/desilva-fleet
├── feat/samaranayake-booking
├── feat/kavindi-payment
├── feat/wickramasinghe-dashboard
└── feat/pahasara-support
```

### Rules
1. **Never push `main` as another person’s work.** Member evidence = commits on **their** branch (and PRs into `main` if required).
2. Work order on this laptop: check out their branch → develop → commit **as that member’s git identity** → you switch GitHub login → `git push`.
3. Shared foundation (project skeleton, DB schema core, auth) lands first on `feat/sakalasuriya-vehicle` (or a short `chore/foundation` merged to `main`), then others branch from updated `main`.
4. Integration merges into `main` at **50%**, **75%**, and **100%** checkpoints.

### Commit identity (per member, on this machine)

**Always** follow [`MEMBER_GIT_IDENTITIES.md`](./MEMBER_GIT_IDENTITIES.md) before `git add` / `git commit`:

1. Checkout their branch.  
2. Set **local** `user.name` / `user.email` from the registry (or one-shot `-c`).  
3. Verify with `git config user.name` && `git config user.email`.  
4. Commit; confirm `git log -1` shows **them**, not Udith.

```bash
git config --local user.name  "Exact Name As On GitHub"
git config --local user.email "their-github-verified-or-noreply@..."
# verify, then:
git add ... && git commit -m "..."
git log -1 --format='%an <%ae> | %s'
```

One-shot (no lasting local config change):

```bash
git -c user.name="..." -c user.email="..." commit -m "..."
```

Do **not** use `--global` for member commits. Fill emails/usernames when credentials are provided; store secrets only in gitignored `docs/MEMBER_CREDENTIALS.local.md`.

### Push guidance (you switch accounts manually)

1. Finish commits on their branch.
2. Sign out of GitHub in browser / clear cached credentials if needed:
   - HTTPS: Credential Manager / `gh auth logout` then `gh auth login` as that user  
   - Or SSH: use that member’s SSH key via `GIT_SSH_COMMAND` / `~/.ssh/config` Host aliases
3. Confirm identity: `gh api user` or `git ls-remote` succeeds as them.
4. Push: `git push -u origin feat/<member-branch>`
5. Optional: open PR from their account into `main`.

**Important:** The current remote owner is **IT25102266**. Either:
- all members are collaborators on that repo, **or**
- each forks and you push to forks (harder for one laptop), **or**
- you use one org repo with 6 collaborators (best).

---

## 4. Overall project progress (all six modules)

| Milestone | Meaning (whole product) | Target (proposal weeks) |
|-----------|-------------------------|-------------------------|
| **0%** | Docs + empty repo; plan agreed; stack chosen | Now |
| **25%** | Skeleton + auth + Vehicle CRUD + basic search; maintenance schema started | ~Week 6–7 / Sprint 1 |
| **50%** | Booking flow + approve/deny + maintenance unavailable flag; modules talk to each other | ~Week 8–9 / Sprint 2 |
| **75%** | Payments/invoices/refunds + return mileage; dashboards & notifications started; design docs | ~Week 10–11 |
| **100%** | All 6 UCs demoable, integration tests, design patterns, UI polish, final report | ~Week 12–14 |

```
Overall:  [░░░░░░░░░░░░░░░░░░░░]  0%
Member:   each starts at 0% on their branch (see Section 5)
```

---

## 5. Per-member development phases

Progress % below is **that member’s module**, not the whole repo.

---

### 5.1 SAKALASOORIYA — Vehicle Management (+ auth foundation)

**Branch:** `feat/sakalasuriya-vehicle`  
**UC-02 / PBIs:** PBI-01–05 (auth shared with Pahasara in Sprint 1)

| % | Deliverables | Done when |
|---|--------------|-----------|
| **0%** | Branch created; no vehicle code | Plan only |
| **25%** | DB: users/roles + vehicles; register/login stub; Vehicle entity fields (category, brand, model, seats, gearbox, fuel, features, photos, price/day, deposit, branch, status, location) | Can insert a vehicle in DB / simple form |
| **50%** | Full vehicle CRUD UI/API; list fleet; unique registration; branch location field | Staff can add/edit/remove (archive) vehicles |
| **75%** | Search & filter (type, price, gearbox, fuel, branch, date); real-time availability flag; inter-branch transfer with timestamp | Customer-facing search works; transfer updates location |
| **100%** | Integration with Booking + Maintenance availability; tests; demo script for UC-02 | Passes UC-02 demo end-to-end |

**Feature checklist**
- [ ] Vehicle CRUD  
- [ ] Photos / features  
- [ ] Branch transfer log  
- [ ] Availability sync  
- [ ] Search & filter  
- [ ] Auth/RBAC foundation (with Pahasara)

---

### 5.2 DE SILVA — Fleet Maintenance & Servicing

**Branch:** `feat/desilva-fleet`  
**UC-01 / PBIs:** PBI-08, 09, 14 (related), 18, 19

| % | Deliverables | Done when |
|---|--------------|-----------|
| **0%** | Branch from `main` after vehicle entity exists | Depends on Vehicle 25%+ |
| **25%** | Maintenance schema: service type, dates, cost, description, mechanics, status | Can create empty record |
| **50%** | CRUD: create / update / close records; view service history | Fleet Coordinator can manage records |
| **75%** | Auto-flag vehicle **Unavailable** on open maintenance; reinstate **Available** on close; warn if future bookings exist | Booking module cannot offer under-maintenance vehicles |
| **100%** | Mileage/fuel discrepancy link from return; maintenance reminders hooks; tests; UC-01 demo | Full UC-01 + no double-book under service |

**Feature checklist**
- [ ] Maintenance CRUD  
- [ ] Full history view  
- [ ] Auto unavailable / available  
- [ ] Booking conflict warning  
- [ ] Reminder hooks (with Pahasara)

---

### 5.3 SAMARANAYAKE — Booking & Reservation (+ return processing)

**Branch:** `feat/samaranayake-booking`  
**UC-03 / PBIs:** PBI-06, 07, 12, 14

| % | Deliverables | Done when |
|---|--------------|-----------|
| **0%** | Branch ready; booking states defined | Pending / Approved / Ongoing / Completed / Cancelled |
| **25%** | Booking request create; date + branch pickup; pending status | Customer can submit a pending booking |
| **50%** | Supervisor **single approval screen** (vehicle + customer + history + payment status); approve/deny + reason | Staff can decide on one screen |
| **75%** | Status workflow; real-time availability check; integration with maintenance unavailable; return checklist UI (mileage/fuel) | No double booking across branches |
| **100%** | Ops read-only booking monitor; discrepancy flags; tests; UC-03 demo | Full booking lifecycle demo |

**Feature checklist**
- [ ] Search → reserve  
- [ ] Approval screen  
- [ ] Approve / deny + reason  
- [ ] Status tracking  
- [ ] Return mileage/fuel  
- [ ] Availability integration  

---

### 5.4 KAVINDI — Payment & Billing

**Branch:** `feat/kavindi-payment`  
**UC-04 / PBIs:** PBI-10, 11, 15, 16

| % | Deliverables | Done when |
|---|--------------|-----------|
| **0%** | Branch; payment entities designed | Invoice, Payment, Refund models |
| **25%** | Sandbox gateway adapter (mock OK); pay deposit/fee after approval | Fake success/fail payments |
| **50%** | Mark Paid; store payment history; auto invoice PDF/HTML | Customer sees receipt + invoice |
| **75%** | Refunds for authorized cancellations; late fee / damage charges on return | Finance can refund / add charges |
| **100%** | Audit trail; notification hooks; tests; UC-04 demo | Full money flow demo |

**Feature checklist**
- [ ] Deposit + rental payment  
- [ ] Invoices  
- [ ] Refunds  
- [ ] Late fees / damage  
- [ ] Payment history  

---

### 5.5 WICKRAMASINGHE — Admin Dashboard & Reporting

**Branch:** `feat/wickramasinghe-dashboard`  
**UC-05 / PBIs:** PBI-13, 21, 22, 23

| % | Deliverables | Done when |
|---|--------------|-----------|
| **0%** | Branch; metric list agreed | Depends on booking/payment data |
| **25%** | Dashboard shell + daily metrics placeholders | Page loads with empty/sample data |
| **50%** | Daily + monthly reports from live DB (revenue, bookings, deposits, refunds) | Managers see real numbers |
| **75%** | Annual reports; branch comparison (Colombo/Kandy/Galle); utilization; filters | Charts + filters work |
| **100%** | Real-time location overview; export/print; tests; UC-05 demo | Full management demo |

**Feature checklist**
- [ ] Daily / monthly / annual reports  
- [ ] Branch comparison  
- [ ] Utilization  
- [ ] Location visibility board  
- [ ] Export  

---

### 5.6 PAHASARA — Customer Support & Notifications (+ auth polish / QA)

**Branch:** `feat/pahasara-support`  
**UC-06 / PBIs:** PBI-17, 18, 19, 20, 24 (+ Sprint-1 auth share)

| % | Deliverables | Done when |
|---|--------------|-----------|
| **0%** | Branch; notification event list defined | Booking / payment / maintenance events |
| **25%** | Notification service stub (log/email); support ticket model | Events can enqueue a message |
| **50%** | Email/SMS (or sandbox) for booking + payment; ticket submit UI | Customer gets confirmation emails (or Mailtrap) |
| **75%** | Staff ticket workflow (Open → In Progress → Resolved → Closed); password reset / account recovery; admin add branch/user | Support + recovery usable |
| **100%** | Maintenance reminders; retry queue; QA pass across modules; UC-06 demo | Full support + alerts demo |

**Feature checklist**
- [ ] Booking / payment notifications  
- [ ] Support tickets  
- [ ] Password reset  
- [ ] Add branch / user (scalability)  
- [ ] Cross-module QA  

---

## 6. Recommended build order on this laptop

Do **not** develop all six in parallel from empty repo. Sequence:

| Step | Who | Why |
|------|-----|-----|
| 1 | **Sakala** (foundation + Vehicle) → 25–50% | Everyone depends on users + vehicles |
| 2 | **De Silva** Maintenance → 25–50% | Needs vehicle IDs |
| 3 | **Samaranayake** Booking → 25–50% | Needs vehicles + availability |
| 4 | **Kavindi** Payment → 25–50% | Needs approved bookings |
| 5 | **Wickramasinghe** Dashboard → 25%+ | Needs transactional data |
| 6 | **Pahasara** Notifications/Support → parallel from step 2 | Hooks into events |
| 7 | Raise each to 75% then 100% with integration merges |

**Sprint alignment (docs):**
1. Sprint 1 — Auth + Vehicle  
2. Sprint 2 — Booking + Maintenance  
3. Sprint 3 — Payment + Return  
4. Sprint 4 — Dashboard + Support  

---

## 7. Progress tracker (update as you go)

| Member | Branch | 0% | 25% | 50% | 75% | 100% |
|--------|--------|----|-----|-----|-----|------|
| Sakalasuriya | `feat/sakalasuriya-vehicle` | ● | ○ | ○ | ○ | ○ |
| De Silva | `feat/desilva-fleet` | ● | ○ | ○ | ○ | ○ |
| Samaranayake | `feat/samaranayake-booking` | ● | ○ | ○ | ○ | ○ |
| Kavindi | `feat/kavindi-payment` | ● | ○ | ○ | ○ | ○ |
| Wickramasinghe | `feat/wickramasinghe-dashboard` | ● | ○ | ○ | ○ | ○ |
| Pahasara | `feat/pahasara-support` | ● | ○ | ○ | ○ | ○ |
| **Overall product** | `main` | **● 0%** | ○ | ○ | ○ | ○ |

Legend: ● reached · ○ not yet

---

## 8. What you need to give me (essentials)

Reply with these so implementation can start cleanly:

### A. Accounts & GitHub (required for push workflow)
1. For **each of the 6 members**: GitHub username + email used for commits  
2. Confirm: are all 6 **collaborators** on `IT25102266/lanka-ride`? (Yes/No)  
3. How you push: HTTPS + `gh auth` **or** SSH keys  

### B. Technology choices (required)
1. Backend: Spring Boot? Jakarta EE? Plain Servlets?  
2. Frontend: Thymeleaf / JSP / React / other?  
3. Database: MySQL / PostgreSQL / other?  
4. Payment sandbox preference (or “mock gateway is OK”)  
5. Email/SMS: real sandbox or console mock for viva?  

### C. Your role on this laptop
1. Which member are **you**? (so we prioritize your branch first if needed)  
2. Do you want me to **implement all six** modules here (you only switch accounts to push), or only some?  

### D. Lecturer / module constraints (if any)
1. Mandatory package structure / design patterns?  
2. Must use a specific IDE / Tomcat / NetBeans template?  
3. Deadline for next checkpoint (25% / 50% / 75%)?  

### E. Optional but useful
1. Figma / wireframes if any  
2. Preferred UI theme / brand colors for Lanka Ride  
3. Sample vehicle data for Colombo / Kandy / Galle  

---

## 9. Next actions (after you reply)

1. Lock stack from your answers in Section 8.  
2. Create the 6 feature branches from `main`.  
3. Scaffold the Java project once on foundation → merge to `main`.  
4. Implement member-by-member in the order in Section 6, stopping at each % milestone for commits under that member’s identity.  
5. You switch GitHub account → push that branch.  
6. Update the tracker in Section 7 after every milestone.

---

## 10. Quick reference — “what each person is developing”

1. **Sakala** — Cars in the system: add/edit/remove, search, where each car is, availability.  
2. **De Silva** — Service/repair logs; cars under service cannot be booked.  
3. **Samaranayake** — Customer books; staff approve/deny; booking statuses; return mileage/fuel.  
4. **Kavindi** — Pay deposit/fee; invoices; refunds; late/damage charges.  
5. **Wickramasinghe** — Management dashboard; daily/monthly/annual reports; branch comparison.  
6. **Pahasara** — Email/SMS alerts; support tickets; password reset; admin add users/branches; QA.

---

*Derived from: Project Proposal, Scrum Roles Assignment, Use Case Diagram Assignment (Group 2026-Y2-S1-MLB-B13G2-05).*
