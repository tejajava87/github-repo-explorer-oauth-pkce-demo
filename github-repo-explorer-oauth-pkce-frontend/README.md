```md
## Getting started — run the app locally

Prerequisites

- Node.js v18+ and npm
- Angular CLI (optional for local dev): `npm i -g @angular/cli`

```
Install dependencies  - bash
```
npm install
```
---
Environment configuration

1. Open `src/environments/environment.ts` and configure the following values:

```ts
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080', // backend that exposes /api/* endpoints
  githubClientId: '<YOUR_CLIENT_ID>',
  redirectUri: 'http://localhost:4200/auth/callback',
  githubScopes: 'read:user repo'
};
```

### Configuration Notes

* `githubClientId`
  Obtained from the GitHub OAuth App page.

* `redirectUri`
  Must exactly match the callback URL configured in GitHub.

* `githubScopes`
  Determines which GitHub permissions are requested.

2. Register a GitHub OAuth App (see backend Readme.md) and paste the **Client ID** into `githubClientId`.
3. Ensure the `redirectUri` you add to the OAuth app exactly matches `redirectUri` above.

Start the frontend

```bash
npm start        # runs `ng serve` (defaults to port 4200)
# or explicitly: ng serve --port 4200 --open
```
---

## GitHub API Endpoints Used

### 1. OAuth Authorization Endpoint

```http
GET https://github.com/login/oauth/authorize
```

**Purpose**

* Starts the OAuth Authorization Code flow
* Redirects the user to GitHub for consent

**Documentation**

```
https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps
```

---

### 2. List Authenticated User Repositories

```http
GET https://api.github.com/user/repos
```

**Purpose**

* Retrieves repositories owned by or accessible to the authenticated user
* Used for the repository list screen

**Documentation**

```
https://docs.github.com/en/rest/repos/repos#list-repositories-for-the-authenticated-user
```

---

### 3. Get Repository Details

```http
GET https://api.github.com/repos/{owner}/{repo}
```

**Purpose**

* Retrieves detailed information for a selected repository
* Used for the repository detail screen

**Documentation**

```
https://docs.github.com/en/rest/repos/repos#get-a-repository
```

---

## OAuth + PKCE Flow (Implementation Summary)

1. Generate PKCE verifier and challenge in the browser
2. Store verifier and OAuth state in session storage
3. Redirect user to GitHub authorization endpoint
4. Handle redirect in `/auth/callback`
5. Validate:

  * Authorization code
  * OAuth state
  * PKCE verifier
6. Send code + verifier to backend for token exchange
7. On success, navigate to `/repos`

---

## Use of AI Tools During Development

**Tools used**

- **GitHub Copilot (Raptor mini (Preview))** — used for:
  - **Code generation & refactoring**: e.g., refactoring `RepoListComponent` to use Signals and converting the template bindings.
  - **Debugging assistance**: locating where loading/rendering logic failed and suggesting robust parsing for API responses.
  - **Documentation**: drafting README content and run instructions.

- **Repository search & static analysis tools** (local): used to find references, imports, and ensure consistency across the codebase.

**Why and how**

- AI suggestions were applied where they improved clarity or correctness (component refactor, guard fixes, template bindings).
- All security-sensitive logic (OAuth PKCE flow, token exchange boundaries, auth guard validation) was **manually reviewed and implemented** by a human to ensure correctness against GitHub's official documentation.

**Manual-only work (no AI assistance)**

- OAuth flow verification and final integration with the backend expectations.
- Security-sensitive checks and explicit error messages.
- Final testing and local validation (running `ng serve`, verifying network responses).

---

## Known Limitations & Shortcuts

* Backend token exchange is abstracted
  The frontend assumes a backend endpoint exists for exchanging the authorization code.

* No refresh token handling
  Access token refresh logic is not implemented.

* Limited UI polish
  The UI focuses on functionality rather than design.

* No automated tests
  Unit and end-to-end tests were omitted to prioritize OAuth correctness and integration clarity.

* Single OAuth provider
  Only GitHub is supported, though the architecture allows extension.

---

## Summary

This project demonstrates:

* Correct OAuth 2.0 Authorization Code Flow with PKCE
* Secure browser-based authentication
* Third-party API integration using GitHub REST APIs
* Clean Angular architecture using standalone components

```
