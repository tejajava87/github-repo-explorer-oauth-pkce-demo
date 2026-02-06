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
