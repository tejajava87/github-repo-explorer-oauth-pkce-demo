# GitHub Repo Explorer Backend

A Spring Boot REST API backend for exploring GitHub repositories using GitHub OAuth 2.0 authentication with PKCE flow. The application stores user tokens and fetches GitHub repository data securely.


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


## Running the Application

1. Clone the repository:
```bash
git clone https://github.com/your-username/Github-repo-explorer-backend.git
cd Github-repo-explorer-backend
```

2. Set the following environment variables in your system:
```
GITHUB_CLIENT_ID=your-client-id-here
GITHUB_CLIENT_SECRET=your-client-secret-here
```

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
