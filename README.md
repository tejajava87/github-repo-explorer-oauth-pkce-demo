# GITHUB-REPO-EXPLORER: OAuth 2.0 Authorization Code with PKCE – Full Stack Demo

This project is a full-stack implementation of OAuth 2.0 Authorization Code flow with PKCE, built to demonstrate secure third-party API integration using a modern frontend and backend architecture.
The application showcases how enterprise systems securely authenticate users, manage tokens, and consume protected APIs — without exposing secrets in the frontend.

---

## Project Structure

```
github-repo-explorer-oauth-pkce-demo/
├── github-repo-explorer-oauth-pkce-frontend/   # Angular UI + PKCE flow
├── github-repo-explorer-oauth-pkce-backend/    # Spring Boot API + token exchange
└── README.md
```

---

## 1) Register GitHub OAuth App (Client ID + Redirect URI)

1. Go to **GitHub Developer Settings**  
   https://github.com/settings/developers

2. Choose **OAuth Apps → New OAuth App**

3. Fill in:
   - **Application name:** GitHub Repo Explorer
   - **Homepage URL:** `http://localhost:4200`
   - **Authorization callback URL:** `http://localhost:4200/auth/callback`

4. Click **Register application**

5. Copy the **Client ID** (and Client Secret for backend).

---

## 2) Configure Environment & Variables

### Backend (Spring Boot)
Set these environment variables:

```
GITHUB_CLIENT_ID=your-client-id-here
GITHUB_CLIENT_SECRET=your-client-secret-here
```

### Frontend (Angular)
Edit:

`github-repo-explorer-oauth-pkce-frontend/src/environments/environment.ts`

Example:

```ts
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080',
  githubClientId: '<YOUR_CLIENT_ID>',
  redirectUri: 'http://localhost:4200/auth/callback',
  githubScopes: 'read:user repo'
};
```

---

## 3) How to Run the App

### Backend (Spring Boot)
```bash
cd github-repo-explorer-oauth-pkce-backend
mvn spring-boot:run
```

Runs on: `http://localhost:8080`

---

### Frontend (Angular)
```bash
cd github-repo-explorer-oauth-pkce-frontend
npm install
npm start
```

Runs on: `http://localhost:4200`

---

## 4) GitHub API Endpoints Used

### OAuth Authorization
`GET https://github.com/login/oauth/authorize`  
Starts the OAuth login/consent flow.  
Docs: https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps

### List Authenticated User Repositories
`GET https://api.github.com/user/repos`  
Fetches repos the user owns or can access.  
Docs: https://docs.github.com/en/rest/repos/repos#list-repositories-for-the-authenticated-user

### Get Repository Details
`GET https://api.github.com/repos/{owner}/{repo}`  
Fetches details for a single repo.  
Docs: https://docs.github.com/en/rest/repos/repos#get-a-repository

---

## 5) Backend API Endpoints (App)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/exchange` | Exchange GitHub code for access token |
| GET | `/api/auth/userstatus` | Get authenticated user info |
| GET | `/api/repos` | List GitHub repositories |

---

## 6) How AI Tools Were Used

### GitHub Copilot (Frontend + Backend)
Used for:
- Generating boilerplate (DTOs, entities, service helpers)
- Refactoring UI components and template bindings
- Drafting documentation sections

Manual (no AI):
- OAuth security flow validation
- Session/token handling logic
- upsert logic to update the user's access token.
- Security configuration and sensitive auth logic

---

## 7) Known Limitations / Shortcuts

- No refresh token support
- Minimal UI styling (focus on functionality)
- No automated tests included
- Single OAuth provider (GitHub only)
- Token exchange assumes backend is available and trusted


## OAuth 2.0 PKCE Flow (High Level)

1. **Frontend**
   - Generates `code_verifier` and `code_challenge`
   - Redirects user to OAuth provider login
   - Receives authorization code

2. **Backend**
   - Exchanges authorization code for access token
   - Stores tokens securely
   - Calls protected third-party APIs

> PKCE ensures authorization security without exposing client secrets in the browser.

---

**More details can be found in github-repo-explorer-oauth-pkce-frontend & github-repo-explorer-oauth-backend  README.MD files**

