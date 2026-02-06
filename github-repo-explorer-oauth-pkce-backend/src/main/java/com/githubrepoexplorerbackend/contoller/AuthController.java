package com.githubrepoexplorerbackend.contoller;


import com.githubrepoexplorerbackend.dto.AuthExchangeRequest;
import com.githubrepoexplorerbackend.entity.UserToken;
import com.githubrepoexplorerbackend.exception.OAuthExchangeException;
import com.githubrepoexplorerbackend.repository.UserTokenRepository;
import com.githubrepoexplorerbackend.service.GitHubOAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final GitHubOAuthService oAuthService;

    public AuthController(GitHubOAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    /**
     * POST /api/auth/exchange
     * <p>
     * Execution flow:
     * 1. Receive authorization code, code_verifier and redirectUri from the frontend.
     * 2. Call GitHubOAuthService.exchangeCode(...) to exchange the code for tokens.
     * 3. Fetch the GitHub user's login using the obtained access_token.
     * 4. Upsert the user's access token into the database.
     * 5. Create a Spring Security Authentication object and store it in the user's HTTP session.
     * <p>
     * Error modes:
     * - If the token exchange fails, OAuthExchangeException is thrown (handled by GlobalExceptionHandler -> 502).
     * - If fetching the GitHub user fails, a RuntimeException is thrown (handled -> 500).
     */
    @PostMapping("/exchange")
    public ResponseEntity<Void> exchange(
            @Valid @RequestBody AuthExchangeRequest req,
            HttpServletRequest httpRequest
    ) {
        log.info("Starting token exchange for code (len={})", req.code().length());

        Map<String, Object> tokenRes;
        try {
            tokenRes = oAuthService.exchangeCode(
                    req.code(),
                    req.codeVerifier(),
                    req.redirectUri()
            );
        } catch (OAuthExchangeException e) {
            log.error("OAuth exchange failed", e);
            throw e; // handled by GlobalExceptionHandler
        }

        String accessToken = (String) tokenRes.get("access_token");
        String tokenType = (String) tokenRes.getOrDefault("token_type", "bearer");
        String scope = (String) tokenRes.getOrDefault("scope", "");

        // 1️ Fetch GitHub login
        String githubLogin = oAuthService.fetchGitHubLogin(accessToken);

        // 2 Upsert token
        oAuthService.saveOrUpdate(
                githubLogin,
                accessToken,
                tokenType,
                scope
        );

        // 3️ Create authentication
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        githubLogin,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        // 4 Persist SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );
        httpRequest.changeSessionId();

        log.info("Authentication created for user={}", githubLogin);

        // 5  Explicit success response
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/auth/userstatus
     * <p>
     * Returns basic information about the current authenticated user based on
     * the Spring Security Authentication injected by the framework.
     * If no authentication is present, returns 401 with authenticated=false.
     */
    @GetMapping("/userstatus")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }

        return ResponseEntity.ok(
                Map.of(
                        "authenticated", true,
                        "login", authentication.getName()
                )
        );
    }

    /**
     * POST /api/auth/logout
     * <p>
     * Invalidate the current HTTP session — this removes the SecurityContext and
     * effectively logs the user out of the server-side session.
     */
    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }




}
