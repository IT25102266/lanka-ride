# Member Git Identities & Credentials

**Purpose:** Before every `git add` / `git commit` for a member’s work, switch to **that member’s** Git identity so GitHub Contributors shows them — not the laptop owner (`udithsandaruwan2`).

**Status:** All 6 members — GitHub username + commit name/email filled. Push via `gh auth switch` (tokens in keyring, not in this repo).  
**Related plan:** [`DEVELOPMENT_PLAN.md`](./DEVELOPMENT_PLAN.md)

---

## Security rules

| Store here (OK to commit) | Never commit (use local file) |
|---------------------------|-------------------------------|
| GitHub username | Passwords |
| Display name for commits | Personal access tokens (PAT) / `gho_` tokens |
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

### Switch GitHub account before push

```bash
gh auth switch
# pick the member’s GitHub username from the table
gh api user --jq .login   # confirm
git push -u origin HEAD
```

---

## Member registry

| Key | Member | Student ID | Branch | GitHub username | Commit `user.name` | Commit `user.email` | Creds filled? |
|-----|--------|------------|--------|-----------------|--------------------|---------------------|---------------|
| `sakala` | SAKALASOORIYA S.A.A.A | IT25102266 | `feat/sakalasuriya-vehicle` | `IT25102266` | `Ashini Sakalasooriya` | `it25102266@my.sliit.lk` | ☑ |
| `desilva` | DE SILVA D.L.K.C | IT25102264 | `feat/desilva-fleet` | `IT25102264` | `D.L.K.C. De Silva` | `it25102264@my.sliit.lk` | ☑ |
| `samara` | SAMARANAYAKE P.I.S | IT24101349 | `feat/samaranayake-booking` | `it24101349` | `P.I.S. Samaranayake` | `it24101349@my.sliit.lk` | ☑ |
| `kavindi` | KAVINDI P.D.N | IT25102265 | `feat/kavindi-payment` | `IT25102265` | `P.D.N. Kavindi` | `it25102265@my.sliit.lk` | ☑ |
| `wickra` | WICKRAMASINGHE R.D.W.K.G.S | IT24101008 | `feat/wickramasinghe-dashboard` | `IT241021008` | `R.D.W.K.G.S. Wickramasinghe` | `it24101008@my.sliit.lk` | ☑ |
| `pahasara` | PAHASARA Y.A.P | IT24102871 | `feat/pahasara-support` | `it24102871` | `Y.A.P. Pahasara` | `it24102871@my.sliit.lk` | ☑ |

**Notes**
- Wickramasinghe GitHub login is `IT241021008` (as on `gh auth`); student ID / email use `IT24101008` / `it24101008@…`. Confirm the SLIIT email is verified on that GitHub account.
- Emails normalized to lowercase `…@my.sliit.lk`.
- Commit `user.name` values are readable forms of the official names; change locally if a member prefers a different display name.

---

## Ready-to-use one-shots

### `sakala` → `feat/sakalasuriya-vehicle` · push as `IT25102266`

```bash
git config --local user.name  "Ashini Sakalasooriya"
git config --local user.email "it25102266@my.sliit.lk"
git -c user.name="Ashini Sakalasooriya" -c user.email="it25102266@my.sliit.lk" commit -m "YOUR MESSAGE"
```

### `desilva` → `feat/desilva-fleet` · push as `IT25102264`

```bash
git config --local user.name  "D.L.K.C. De Silva"
git config --local user.email "it25102264@my.sliit.lk"
git -c user.name="D.L.K.C. De Silva" -c user.email="it25102264@my.sliit.lk" commit -m "YOUR MESSAGE"
```

### `samara` → `feat/samaranayake-booking` · push as `it24101349`

```bash
git config --local user.name  "P.I.S. Samaranayake"
git config --local user.email "it24101349@my.sliit.lk"
git -c user.name="P.I.S. Samaranayake" -c user.email="it24101349@my.sliit.lk" commit -m "YOUR MESSAGE"
```

### `kavindi` → `feat/kavindi-payment` · push as `IT25102265`

```bash
git config --local user.name  "P.D.N. Kavindi"
git config --local user.email "it25102265@my.sliit.lk"
git -c user.name="P.D.N. Kavindi" -c user.email="it25102265@my.sliit.lk" commit -m "YOUR MESSAGE"
```

### `wickra` → `feat/wickramasinghe-dashboard` · push as `IT241021008`

```bash
git config --local user.name  "R.D.W.K.G.S. Wickramasinghe"
git config --local user.email "it24101008@my.sliit.lk"
git -c user.name="R.D.W.K.G.S. Wickramasinghe" -c user.email="it24101008@my.sliit.lk" commit -m "YOUR MESSAGE"
```

### `pahasara` → `feat/pahasara-support` · push as `it24102871`

```bash
git config --local user.name  "Y.A.P. Pahasara"
git config --local user.email "it24102871@my.sliit.lk"
git -c user.name="Y.A.P. Pahasara" -c user.email="it24102871@my.sliit.lk" commit -m "YOUR MESSAGE"
```

**Email tip:** Prefer each account’s GitHub **noreply** if the SLIIT address is not verified on GitHub. Keep SLIIT emails verified for contributor attribution.

**Laptop owner (do not use for member feature commits):**

| Role | GitHub | Global git (leave as-is) |
|------|--------|--------------------------|
| Integrator / fixer on `main` | `udithsandaruwan2` | Udith Sandaruwan \<developer.udithsandaruwan@gmail.com\> |

---

## Agent / developer workflow (this laptop)

When implementing or committing for a member:

1. Read this file → pick the **Key** (e.g. `samara`).
2. `git checkout` their **Branch** (create from `main` / foundation if needed).
3. Set `--local` `user.name` / `user.email` from the table (or use `git -c ... commit`).
4. **Re-check** `git config user.name` and `git config user.email`.
5. Only then `git add` → `git commit`.
6. Push: `gh auth switch` → that member’s GitHub username → `git push -u origin <branch>`.

All six rows are filled for commit identity. Auth for push is already on this machine via `gh` keyring — do **not** paste tokens into gitignored or committed files.

---

## Checklist (filled)

For each of the 6 members:

- [x] GitHub username  
- [x] Commit display name  
- [x] Commit email (SLIIT)  
- [x] Push auth: `gh auth` HTTPS + keyring (`gh auth switch`)  
- [ ] Confirm collaborator access on `IT25102266/lanka-ride` (verify when pushing)

Optional: fill `docs/MEMBER_CREDENTIALS.local.md` with notes only (never tokens).
