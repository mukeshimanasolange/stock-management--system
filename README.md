# Stockwise — Stock Management System

A role-based stock management application with a React/Vite frontend, Spring Boot REST API, and MySQL 8 database.

## Project layout

- `backend/src/main/java`: Spring Boot entities, repositories, services, controllers, JWT security, and API error handling.
- `backend/src/main/resources/db/schema.sql`: MySQL schema, roles, categories, and demo products.
- `frontend/src`: React screens, role-aware routes, reusable management pages, and Axios API client.
- `docker-compose.yml`: MySQL, API, and web application.
- `.github/workflows/ci.yml`: backend/frontend verification and success-gated Jira transition.

## Quick start with Docker

Install Docker Desktop with Compose. To start immediately without creating a `.env` file:

```powershell
docker compose up -d --build
docker compose ps
```

For local development only, the database connects as MySQL `root` with a blank password, as requested. Compose allows an empty MySQL root password and binds the database port to localhost. The backend/API runs on port `8082`. To bootstrap an administrator, copy `.env.example` to `.env`, set `ADMIN_EMAIL` and a unique `ADMIN_PASSWORD` of at least 12 characters, then run Compose again; otherwise, users can register as STAFF. The JWT signing key must remain non-empty and is not a database password. **A blank MySQL root password is unsafe; do not expose this setup to a network or use it for production.** `.env` is ignored by Git.

Open the frontend at `http://localhost:3000`; the API is at `http://localhost:8082`. MySQL persists in the `mysql_data` volume. The first database startup runs the schema script and inserts sample categories/products. The optional administrator bootstrap runs only if the users table is empty.

To stop the application without removing the database:

```powershell
docker compose down
```

To destroy the local database and all its data, explicitly remove the volume with `docker compose down -v`.

## Local development

### Backend

Requirements: Java 21, Maven 3.9+, and MySQL 8. Execute `backend/src/main/resources/db/schema.sql` in MySQL Workbench, then set the backend environment variables from `.env.example` in your shell. For example:

```powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "stock_management_db"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = ""
$env:SERVER_PORT = "8082"
$env:JWT_SECRET = "your-random-secret-with-at-least-32-bytes"
$env:APP_CORS_ORIGINS = "http://localhost:5173"
$env:ADMIN_EMAIL = "admin@example.com"
$env:ADMIN_PASSWORD = "your-unique-password-of-at-least-12-characters"
mvn spring-boot:run
```

The root `pom.xml` maps Maven sources to `backend/src`. `ddl-auto` is `validate`; import the schema before starting the API.

### Frontend

Requirements: Node.js 20 or newer. If PowerShell says `npm` or `npm.cmd` is not recognized, install the current Node.js LTS release from [nodejs.org](https://nodejs.org/) with the default options (including npm), then close and reopen PowerShell so the updated PATH is loaded. Verify the installation:

```powershell
node --version
npm --version
```

Run the frontend commands from the `frontend` folder (not the project root):

```powershell
Set-Location D:\Stock_Management_System\frontend
npm install
$env:VITE_API_URL = "http://localhost:8082/api"
npm run dev
```

Open the Vite URL shown in the terminal (default `http://localhost:5173`). Public registration creates STAFF accounts; an ADMIN or STOREKEEPER can manage users, products, suppliers, and stock.

Alternatively, run the full application with Docker Desktop from the project root; local Node.js/npm is not required:

```powershell
Set-Location D:\Stock_Management_System
docker compose up -d --build
```

## Tests and builds

From the project root:

```powershell
mvn test
```

For frontend tests and a production build:

```powershell
Set-Location frontend
npm install
npm test
npm run build
```

The backend tests include successful incoming/outgoing stock operations and verify that an insufficient outgoing-stock request changes neither inventory nor transaction history. The frontend test verifies unauthenticated route protection.

## Access and API overview

All authenticated API calls use `Authorization: Bearer <JWT>`.

| Area | Routes | Access |
|---|---|---|
| Auth | `/api/auth/register`, `/api/auth/login`, `/api/auth/me` | Register/login public; profile read authenticated |
| Users | `/api/users` | ADMIN |
| Products/categories | `/api/products`, `/api/categories` | Catalog reads authenticated; write ADMIN/STOREKEEPER |
| Suppliers | `/api/suppliers` | ADMIN/STOREKEEPER |
| Stock | `/api/stock/incoming`, `/api/stock/outgoing`, `/api/stock/transactions`, `/api/stock/low-stock` | Mutations and transaction history ADMIN/STOREKEEPER |
| Requests | `/api/requests` | Staff submit and view their own; ADMIN/STOREKEEPER review and issue |
| Reports | `/api/reports/stock`, `/api/reports/incoming`, `/api/reports/outgoing`, `/api/reports/low-stock` | ADMIN/STOREKEEPER |
| Dashboard | `/api/dashboard/summary`, `/api/dashboard/stock-movements`, `/api/dashboard/recent-transactions`, `/api/dashboard/low-stock` | ADMIN/STOREKEEPER |

Product and supplier list endpoints accept Spring pagination parameters such as `?page=0&size=20`; both accept a `search` query. Report date parameters accept ISO-8601 instants. The UI exports report data as CSV (Excel-compatible) and supports browser print/save-as-PDF.

Inventory changes and stock-request issues run in database transactions and lock products while checking/updating quantities. Product codes are unique, inventory cannot be negative, and deletes deactivate records to retain transaction history.

## GitHub Actions / Jira

The workflow is `.github/workflows/ci.yml`. Backend/frontend verification runs for pull requests and pushes to `main`. Jira automation runs **only on pushes to `main`** and only after verification succeeds. It finds the Jira key in the pushed commit/merge message, reads that issue's current status, and changes **To Do/Todo → Done only**. It leaves an already-Done issue unchanged and fails without changing the status if it is in any other state. A failing test/build cannot mark an issue Done. Jira secrets are required for a matching issue-keyed push; if missing or invalid, the Jira job fails with a message.

### Part 1 — connect this project to GitHub

The GitHub repository for this project is `https://github.com/mukeshimanasolange/Stock_Management_JIra`.

Open PowerShell in `D:\Stock_Management_System` and check whether this folder already has Git history:

```powershell
git status
git remote -v
```

If `git status` says this is not a Git repository, first check whether the GitHub repository already has files/commits. If it does, clone that repository into a different folder and copy this project into the clone; do not start an unrelated history. If the GitHub repository is empty, initialize this project:

```powershell
git init -b main
git remote add origin https://github.com/mukeshimanasolange/Stock_Management_JIra.git
git status
```

If Git says the remote already exists, inspect it with `git remote -v` rather than adding it again. If `origin` points to a different URL, correct it with:

```powershell
git remote set-url origin https://github.com/mukeshimanasolange/Stock_Management_JIra.git
```

If the GitHub repository already contains commits/files, first fetch and inspect its default branch before pushing. Prefer cloning the existing repository as described below rather than trying to combine unrelated histories:

```powershell
git fetch origin
git branch -r
```

The project already has a `README.md`, so **do not run** `echo "# Stock_Management_JIra" >> README.md` in this folder: it would append a duplicate heading. That command is only for creating a README when one does not exist. To make an initial commit containing this complete project (not just README), use:

```powershell
git branch -M main
git add -A
git status
git commit -m "Initial Stock Management System setup"
git push -u origin main
```

If you are creating a genuinely new README in a different, empty project, the requested setup sequence is:

```powershell
echo "# Stock_Management_JIra" >> README.md
git init
git add README.md
git commit -m "first commit"
git branch -M main
git remote add origin https://github.com/mukeshimanasolange/Stock_Management_JIra.git
git push -u origin main
```

For this existing application, use the complete-project initial-commit commands above instead of that README-only sequence. This initial commit has no Jira key, so it only publishes the baseline and does not transition any Jira task. Work on SMSD-8, SMSD-5, and SMSD-6 separately after the baseline.

Do not force-push. If this folder is not based on the existing GitHub history, the safer option is to clone the repository into a **different, empty folder**, then copy this project's files (excluding `.git`) into that clone and continue there.

If Git has no author configured on this computer, set the name/email you want recorded on commits:

```powershell
git config user.name "Your Name"
git config user.email "your-github-email@example.com"
```

When Git asks you to authenticate during `git push`, complete the Git Credential Manager/browser sign-in. Do not put a GitHub password, personal access token, Jira token, or any secret in a commit, URL, or source file.

### Part 2 — add Jira credentials to GitHub Actions

Use an Atlassian API token, **not your Atlassian account password**:

1. Sign in to the Atlassian account that can view and transition the `SMSD` project issues. Create an API token in Atlassian account security settings.
2. Open the GitHub repository in your browser: `https://github.com/mukeshimanasolange/Stock_Management_JIra`.
3. Go to **Settings → Secrets and variables → Actions → New repository secret**.
4. Create these three repository secrets (the names must match exactly):

   | Secret name | Value |
   |---|---|
   | `JIRA_BASE_URL` | Your Jira site URL only, e.g. `https://your-company.atlassian.net` (no trailing slash and no `/browse/...`) |
   | `JIRA_EMAIL` | The Atlassian account email that created the API token |
   | `JIRA_API_TOKEN` | The API token value from Atlassian |

5. Confirm the Atlassian account has permission to browse and transition issues `SMSD-5`, `SMSD-6`, and `SMSD-8`. Confirm each issue is currently in Jira status **To Do** or **Todo**, and its workflow offers a transition whose displayed name is exactly **Done**.

GitHub masks Actions secrets in logs. Never add their values to `.env.example`, README text, or committed files. To test the GitHub-to-Jira automation, all three secrets must be valid. The Jira job intentionally fails when a Jira-keyed push has missing credentials, the issue cannot be read, the issue is not To Do/Todo, or Jira does not offer a Done transition from To Do/Todo.

### Part 3 — work on and push one Jira task at a time

Do not combine all three tasks into one branch or commit if you want to check each Jira task separately. For each task, make a branch whose name starts with its issue key, run the relevant checks, make an issue-keyed commit, push that branch, and open a pull request into `main`. Branch pushes and pull requests run verification; Jira is updated only when the change is merged and pushed to `main`.

Before starting the next task, finish/check the previous push in GitHub **Actions** and Jira. Then create a pull request from that issue branch into `main`, review it, and merge it before updating local `main` and starting the next task. This keeps completed task code in the base branch. Do not start the next branch with uncommitted changes from the previous task.

For **each** of the three tasks, check Jira before starting: open that task and verify its current status is **To Do** (some Jira projects display this as **Todo**). After the pull request is merged into `main`, the successful main-branch workflow will update that issue alone to **Done**. Wait for the Jira job to finish and refresh the issue page to verify. If the task starts in another status, move it to To Do in Jira first (if your project workflow allows it). Do not expect this automation to advance intermediate statuses such as In Progress or Review.

#### Task A — SMSD-8, User Login

```powershell
Set-Location D:\Stock_Management_System
git switch main
git pull --ff-only origin main
git switch -c SMSD-8-user-login
```

Implement/check the login screen and API authentication, then run the frontend tests/build and backend tests from the project root:

```powershell
mvn test
Set-Location frontend
npm test
npm run build
Set-Location ..
```

Review the changes, stage the login-related files you changed, commit with the issue key, then push the feature branch. This push runs checks but does not update Jira yet:

```powershell
git status
git add frontend/src/pages/LoginPage.jsx frontend/src/context/AuthContext.jsx frontend/src/api/client.js frontend/src/App.jsx
git commit -m "SMSD-8 Implement user login"
git push -u origin SMSD-8-user-login
```

If your implementation changed other files (for example protected routes), explicitly add those files too. If a listed path did not change, Git simply reports it as unchanged; check `git status` before committing.

#### Task B — SMSD-5, User Registration

Only after you have checked the SMSD-8 branch checks and merged its pull request into `main`:

```powershell
git switch main
git pull --ff-only origin main
git switch -c SMSD-5-user-registration
```

Implement/check account registration (including the backend registration API if it is part of the task), then run the relevant backend and frontend tests. Stage and push this task separately:

```powershell
git status
git add frontend/src/pages/RegisterPage.jsx frontend/src/context/AuthContext.jsx frontend/src/App.jsx
git commit -m "SMSD-5 Implement user registration"
git push -u origin SMSD-5-user-registration
```

Add any additional changed registration files explicitly. This feature-branch push runs checks, but Jira changes only when its PR is merged into `main`. Check GitHub Actions and confirm `SMSD-5` becomes Done after merging.

#### Task C — SMSD-6, Design User Registration Form

Only after you have checked the SMSD-5 branch checks and merged its pull request into `main`:

```powershell
git switch main
git pull --ff-only origin main
git switch -c SMSD-6-registration-form-design
```

Implement/check only the registration form design for this task, run the frontend tests/build, and stage the design changes separately:

```powershell
Set-Location frontend
npm test
npm run build
Set-Location ..
git status
git add frontend/src/pages/RegisterPage.jsx frontend/src/styles.css
git commit -m "SMSD-6 Design user registration form"
git push -u origin SMSD-6-registration-form-design
```

#### Check every push

After each feature-branch `git push`:

1. In GitHub, open **Actions** and select **Build, test, and update Jira** for the pushed branch.
2. Confirm **Backend and frontend verification** is green.
3. The Jira job should not run on the feature branch. If verification fails, fix it, commit the fix with the same issue key, and push again.
4. Open a PR into `main`. Use a title containing exactly the task's issue key, for example `SMSD-8 Implement user login`, then merge it after review.
5. Check the Actions run for the new push to `main`. After verification passes, confirm **Mark associated Jira issue done** succeeded and refresh that Jira issue to verify it changed from To Do/Todo to Done.

The automation does not run from a local `git commit` alone. It transitions Jira only after a successful push to `main`; a push to an issue branch or a pull-request-only run will not mark the issue Done. Use **Squash and merge** and ensure the squash commit/PR title contains exactly that task's issue key, for example `SMSD-8 Implement user login`. The Jira status must be named `To Do` or `Todo`, and the available Jira transition must be named exactly `Done`; if your workflow uses different names or disallows this transition, ask the Jira project administrator to configure it.

If you prefer the GitHub website instead of the terminal, upload/commit changes to a branch named with the matching issue key (for example `SMSD-8-user-login`) and include the same key in the commit message. The same Actions and Jira rules apply.
