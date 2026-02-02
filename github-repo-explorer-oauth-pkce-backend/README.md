# GitHub Repo Explorer Backend

A Spring Boot REST API backend for exploring GitHub repositories using GitHub OAuth 2.0 authentication with PKCE flow. The application stores user tokens and fetches GitHub repository data securely.

## Table of Contents

- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [GitHub OAuth Setup](#github-oauth-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Building and Deployment](#building-and-deployment)
- [API Endpoints](#api-endpoints)
- [Database](#database)
- [Development Notes](#development-notes)
- [AI Tools Used](#ai-tools-used)

## Technology Stack

- **Java 21**
- **Spring Boot 4.0.2**
- **Spring Security 7.0.2**
- **Spring Data JPA**
- **H2 Database** (in-file storage)
- **Maven** for dependency management

## Prerequisites

Before running the application, ensure you have:

- Java 21 or later installed
- Maven 3.6+ installed
- A GitHub account
- Git for cloning the repository

## GitHub OAuth Setup

### Step 1: Register a GitHub OAuth Application

1. Navigate to GitHub Developer Settings: [https://github.com/settings/developers](https://github.com/settings/developers)
2. Click on **"OAuth Apps"** in the left sidebar
3. Click the **"New OAuth App"** button
4. Fill in the application details:
   - **Application name**: `GitHub Repo Explorer` (or your preferred name)
   - **Homepage URL**: `http://localhost:4200` (for local development)
   - **Authorization callback URL**: `http://localhost:4200/callback` (frontend redirect)
5. Click **"Register application"**
6. You will be provided with:
   - **Client ID**
   - **Client Secret** (keep this secure!)

### Step 2: Configure Environment Variables

The backend requires GitHub OAuth credentials as environment variables:

Set the following environment variables in your system:
```
GITHUB_CLIENT_ID=your-client-id-here
GITHUB_CLIENT_SECRET=your-client-secret-here
```

## Running the Application

### Using Maven

1. Clone the repository:
```bash
git clone https://github.com/your-username/Github-repo-explorer-backend.git
cd Github-repo-explorer-backend
```

2. Set environment variables (see [GitHub OAuth Setup](#github-oauth-setup))

3. Run the application:
```bash
mvn spring-boot:run
```
## API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/exchange` | Exchange GitHub authorization code for access token |
| `GET` | `/api/auth/userstatus` | Get current authenticated user info |

### Repository Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/repos` | Get repositories for authenticated user |

## Database

The application uses **H2 Database** with PostgreSQL compatibility mode.

### Database Location
- File-based storage at: `./data/githubdb.mv.db`
- Trace file: `./data/githubdb.trace.db`

### Automatic Schema Creation
The application uses JPA with `hibernate.ddl-auto: update`, which automatically creates/updates database tables on startup.

### User Tokens Table

The `USER_TOKENS` table stores OAuth tokens:

```sql
CREATE TABLE USER_TOKENS (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    github_login VARCHAR(255) UNIQUE NOT NULL,
    access_token TEXT NOT NULL,
    token_type VARCHAR(50),
    scope VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);
```

## Development Notes

### Project Structure

```
src/
├── main/
│   ├── java/com/githubrepoexplorerbackend/
│   │   ├── controller/          # REST endpoints
│   │   ├── service/             # Business logic (OAuth, GitHub API)
│   │   ├── entity/              # JPA entities (UserToken)
│   │   ├── repository/          # Data access (JPA repositories)
│   │   ├── dto/                 # Data transfer objects
│   │   └── config/              # Security & application config
│   └── resources/
│       └── application.yaml     # Configuration
└── test/                        # Unit tests
```

### Session Management

- Sessions are stored via HTTP cookies
- SameSite policy is set to `LAX` for CSRF protection
- Secure flag is disabled for local development (enable in production)

### CORS Configuration

The application allows requests from the configured `frontend-origin` to enable cross-origin communication with the frontend.

## AI Tools Used

### GitHub Copilot

**What was used for:**

1. **OAuth Service Implementation** (`GitHubOAuthService.java`)
   - Used for code generation of the token exchange logic and HTTP request construction
   - Generated the RestTemplate setup and response parsing pattern

2. **Entity and DTO Creation** (`UserToken.java`, `AuthExchangeRequest.java`, `AuthMeResponse.java`)
   - Used for boilerplate code generation (getters, setters, Lombok annotations)
   - Generated field definitions and validation annotations

3. **Documentation** (This README)
   - Used for structure and formatting suggestions
   - Generated configuration examples and API endpoint documentation
   - Provided setup instructions templates

**How it was used:**
- Autocomplete suggestions while typing method signatures
- Full method/class generation from context comments
- Problem-solving suggestions for error resolution

### Intentionally Written/Refactored Without AI

1. **SecurityConfig.java** (`config/SecurityConfig.java`)
   - Manually written to ensure security best practices
   - Custom authentication flow setup required careful consideration of CORS, CSRF, and session management
   - Deliberately kept simple without unnecessary middleware

2. **AuthController.exchange() Method Logic** 
   - The upsert pattern (`saveOrUpdate()` method) was manually implemented after AI suggested the concept
   - The flow coordination between GitHub OAuth exchange → fetch user → save token → create auth → persist session was manually structured for clarity
   - Error handling and validation logic was intentionally kept explicit for maintainability

3. **Database Schema Decisions**
   - Unique constraint on `github_login` field was a manual decision to prevent duplicate tokens
   - The migration strategy from INSERT-only to UPSERT pattern was manually designed after understanding the constraint

### Why Manual Implementation Was Chosen

- **Security**: Authentication/authorization code requires careful review and should not rely solely on AI-generated code
- **Clarity**: Business logic flow benefits from explicit, readable code over optimized but opaque AI output
- **Project-Specific Logic**: The upsert pattern is specific to this application's needs and required understanding the full context

## Troubleshooting

### "Unique index or primary key violation" Error

**Problem**: Attempting to log in with the same GitHub account twice fails with a unique constraint violation on `github_login`.

**Solution**: The application now implements an upsert pattern. If a `UserToken` for a `github_login` already exists, the token is updated instead of inserting a duplicate. This is handled automatically in `AuthController.exchange()`.

### Environment Variables Not Recognized

**Problem**: `GITHUB_CLIENT_ID` or `GITHUB_CLIENT_SECRET` are null.

**Solution**:
1. Restart the IDE/terminal after setting environment variables
2. Verify variables are set: 
   - PowerShell: `$env:GITHUB_CLIENT_ID`
   - Cmd: `echo %GITHUB_CLIENT_ID%`
   - Linux/Mac: `echo $GITHUB_CLIENT_ID`
3. Pass as system properties: `mvn spring-boot:run -Dspring-boot.run.arguments="--github.oauth.client-id=xxx"`

### H2 Database Lock Issues

**Problem**: "Database is locked" or database file corruption.

**Solution**:
1. Stop the application
2. Delete the database files:
   ```bash
   rm -rf data/githubdb.*  # On Unix/Mac
   rmdir /s data            # On Windows
   ```
3. Restart the application (database will be recreated)
