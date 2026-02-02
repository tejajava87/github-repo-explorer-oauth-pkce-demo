# GITHUB-REPO-EXPLORER: OAuth 2.0 Authorization Code with PKCE – Full Stack Demo

This project is a full-stack implementation of OAuth 2.0 Authorization Code flow with PKCE, built to demonstrate secure third-party API integration using a modern frontend and backend architecture.
The application showcases how enterprise systems securely authenticate users, manage tokens, and consume protected APIs — without exposing secrets in the frontend.

---

## Tech Stack

### Frontend
- Angular / React
- OAuth 2.0 Authorization Code with PKCE
- Secure redirect-based authentication
- Token exchange initiated via backend

### Backend
- Java 17+
- Spring Boot 3.x
- Spring Security OAuth2
- REST APIs
- PostgreSQL (token persistence)

### Tools
- IntelliJ IDEA (Backend)
- VS Code (Frontend)
- Git

---

### Project Structure

```
github-repo-explorer-oauth-pkce-demo/
├── github-repo-explorer-oauth-pkce-frontend/ # UI + PKCE logic
├── github-repo-explorer-oauth-backend/ # Spring Boot API + token handling
├── docs/ # Architecture & flow documentation
├── .gitignore
├── README.md

```

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

