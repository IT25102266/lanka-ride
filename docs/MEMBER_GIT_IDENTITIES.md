# Member Git Identities & Credentials

**Purpose:** Before every `git add` / `git commit` for a member’s work, switch to **that member’s** Git identity so GitHub Contributors shows them — not the laptop owner (`udithsandaruwan2`).

**Status:** `sakala` commit identity saved; others still TBD.  
**Related plan:** [`DEVELOPMENT_PLAN.md`](./DEVELOPMENT_PLAN.md)

---

## Security rules

| Store here (OK to commit) | Never commit (use local file) |
|---------------------------|-------------------------------|
| GitHub username | Passwords |
| Display name for commits | Personal access tokens (PAT) |
| GitHub-verified / noreply **email** | 2FA backup codes |
| Branch name | SSH private keys |

Secrets go only in **`docs/MEMBER_CREDENTIALS.local.md`** (gitignored).  
Copy from `MEMBER_CREDENTIALS.local.example.md` when ready.

---

## Mandatory check before every member commit

Run this **every time** before `git add` + `git commit` for that person:

```bash
# 1) Correct branch?
git branch --show-current
# expect: feat/<their-branch>

# 2) Correct author? (must match the member row below)
git config user.name
git config user.email

# 3) Or commit in one shot (does not rely on local config):
git -c user.name="NAME_FROM_TABLE" -c user.email="EMAIL_FROM_TABLE" commit -m "..."
```

If name/email still show **Udith Sandaruwan** / `developer.udithsandaruwan@gmail.com`, **stop** and switch identity first.

### Switch identity for this repo only (recommended)

```bash
git config --local user.name  "NAME_FROM_TABLE"
git config --local user.email "EMAIL_FROM_TABLE"
```

Do **not** change `--global` for member work (keep Udith as your personal default elsewhere).

### After commit, verify author

```bash
git log -1 --format='%h %an <%ae> | %s'
```

---

## Member registry

| Key | Member | Student ID | Branch | GitHub username | Commit `user.name` | Commit `user.email` | Creds filled? |
|-----|--------|------------|--------|-----------------|--------------------|---------------------|---------------|
| `sakala` | SAKALASOORIYA S.A.A.A | IT25102266 | `feat/sakalasuriya-vehicle` | `IT25102266` | `Ashini Sakalasooriya` | `it25102266@my.sliit.lk` | ☑ name/email |
| `desilva` | DE SILVA D.L.K.C | IT25102264 | `feat/desilva-fleet` | `TBD` | `TBD` | `TBD` | ☐ |
| `samara` | SAMARANAYAKE P.I.S | IT24101349 | `feat/samaranayake-booking` | `TBD` | `TBD` | `TBD` | ☐ |
| `kavindi` | KAVINDI P.D.N | IT25102265 | `feat/kavindi-payment` | `TBD` | `TBD` | `TBD` | ☐ |
| `wickra` | WICKRAMASINGHE R.D.W.K.G.S | IT24101008 | `feat/wickramasinghe-dashboard` | `TBD` | `TBD` | `TBD` | ☐ |
| `pahasara` | PAHASARA Y.A.P | IT24102871 | `feat/pahasara-support` | `TBD` | `TBD` | `TBD` | ☐ |

### Ready-to-use: `sakala` (Ashini)

```bash
git add .
git -c user.name="Ashini Sakalasooriya" -c user.email="it25102266@my.sliit.lk" \
  commit -m "YOUR MESSAGE"
git log -1 --format='%an <%ae> | %s'   # must show Ashini, not Udith
```

Or set once for this repo while working as her:

```bash
git config --local user.name  "Ashini Sakalasooriya"
git config --local user.email "it25102266@my.sliit.lk"
```

Push as: `gh auth switch` → **IT25102266**.

**Email tip:** Prefer each account’s GitHub **noreply** address (Settings → Emails), e.g.  
`ID+USERNAME@users.noreply.github.com` — must be the one GitHub attributes to that profile.  
Ashini’s SLIIT email is saved as provided; keep it **verified** on the `IT25102266` GitHub account.

**Laptop owner (do not use for member feature commits):**

| Role | GitHub | Global git (leave as-is) |
|------|--------|--------------------------|
| Integrator / fixer on `main` | `udithsandaruwan2` | Udith Sandaruwan \<developer.udithsandaruwan@gmail.com\> |

---

## Agent / developer workflow (this laptop)

When implementing or committing for a member:

1. Read this file → pick the **Key** (e.g. `sakala`).
2. `git checkout` their **Branch**.
3. Set `--local` `user.name` / `user.email` from the table (or use `git -c ... commit`).
4. **Re-check** `git config user.name` and `user.email`.
5. Only then `git add` → `git commit`.
6. Push later: switch `gh auth` / account to that member (credentials in local file), then `git push -u origin <branch>`.

Until credentials are filled (`Creds filled?` = ☐), **commits can still use correct name/email** once you provide emails; **push** waits on account login/PAT.

---

## What to send later (checklist)

For each of the 6 members:

- [ ] GitHub username  
- [ ] Exact display name for commits  
- [ ] Email verified on that GitHub account (or noreply)  
- [ ] How they will authenticate push: password+2FA / PAT / SSH (store secrets only in local file)  
- [ ] Confirm collaborator access on `IT25102266/lanka-ride`

Paste into chat or fill `docs/MEMBER_CREDENTIALS.local.md`.
