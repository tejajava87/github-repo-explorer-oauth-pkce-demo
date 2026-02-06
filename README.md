# GITHUB-REPO-EXPLORER: OAuth 2.0 Authorization Code with PKCE – Full Stack Demo

This project is a full-stack implementation of OAuth 2.0 Authorization Code flow with PKCE, built to demonstrate secure third-party API integration using a modern frontend and backend architecture.
The application showcases how enterprise systems securely authenticate users, manage tokens, and consume protected APIs — without exposing secrets in the frontend.

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

## 2) GitHub API Endpoints Used

### i. OAuth Authorization Endpoint

```http
GET https://github.com/login/oauth/authorize
POST https://github.com/login/oauth/access_token
GET https://api.github.com/user
```

**Purpose**

* Starts the OAuth Authorization Code flow
* Redirects the user to GitHub for consent
* GitHub redirects back to your site with a temporary code
* Exchange the code for an access token
* Retrieves authenticated user info with token

**Documentation**

```
https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps
```

---

### ii. List Authenticated User Repositories

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

## 3) How AI Tools Were Used

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
- 
### Why Manual Implementation Was Chosen

- **Security**: Authentication/authorization code requires careful review and should not rely solely on AI-generated code
- **Clarity**: Business logic flow benefits from explicit, readable code over optimized but opaque AI output
- **Project-Specific Logic**: The upsert pattern is specific to this application's needs and required understanding the full context

---

## 4) Known Limitations / Shortcuts

- No refresh token support
- Minimal UI styling (focus on functionality)
- No automated tests included


---

**More details can be found in github-repo-explorer-oauth-pkce-frontend & github-repo-explorer-oauth-backend  README.MD files**

