package com.githubrepoexplorerbackend.service;

import com.githubrepoexplorerbackend.entity.UserToken;
import com.githubrepoexplorerbackend.exception.OAuthExchangeException;
import com.githubrepoexplorerbackend.repository.UserTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Service
public class GitHubOAuthService {

    private static final Logger log = LoggerFactory.getLogger(GitHubOAuthService.class);

    @Value("${github.oauth.client-id}")
    private String clientId;

    @Value("${github.oauth.client-secret}")
    private String clientSecret;

    @Value("${github.oauth.token-url}")
    private String tokenUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final UserTokenRepository tokenRepo;

    public GitHubOAuthService(UserTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    /**
     * Exchange the authorization code for an access token at the OAuth provider.
     * Execution steps:
     * 1. Build a JSON POST request with client_id, client_secret, code, redirect_uri and code_verifier.
     * 2. POST to the configured tokenUrl and expect a JSON response (access_token, token_type, scope, ...).
     * 3. If the HTTP response is not 2xx or has no body, throw OAuthExchangeException.
     * 4. On network/client errors (RestClientException), translate to OAuthExchangeException.
     * Inputs: authorization code (code), PKCE code_verifier and redirectUri used during the original authorization request.
     * Outputs: Map of token response values (e.g., access_token). Throws OAuthExchangeException on failure.
     */
    public Map<String, Object> exchangeCode(String code, String codeVerifier, String redirectUri) {
        log.info("Exchanging code for token (redirectUri={})", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> body = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code,
                "redirect_uri", redirectUri,
                "code_verifier", codeVerifier
        );

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        try {
            ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<>() {};
            ResponseEntity<Map<String, Object>> res = restTemplate.exchange(tokenUrl, HttpMethod.POST, req, typeRef);

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                log.error("Token exchange failed: status={} body={}", res.getStatusCode(), res.getBody());
                throw new OAuthExchangeException("GitHub token exchange failed");
            }

            log.info("Token exchange succeeded for code (code length={})", code.length());
            return res.getBody();
        } catch (RestClientException e) {
            log.error("Token exchange request failed", e);
            throw new OAuthExchangeException("GitHub token exchange request failed", e);
        }
    }


    /**
     * Helper: fetch the GitHub user's login using the provided access token.
     * <p>
     * Execution steps:
     * 1. Make GET https://api.github.com/user with Bearer authorization.
     * 2. Return the `login` property from the response body.
     * <p>
     * Error modes:
     * - Throws RuntimeException if the request fails or the response is malformed.
     */
    public String fetchGitHubLogin(String accessToken) {
        var headers = new org.springframework.http.HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github+json");

        var entity = new org.springframework.http.HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(
                    "https://api.github.com/user",
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to fetch GitHub user: status={} body={}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to fetch GitHub user");
            }
            return (String) response.getBody().get("login");
        } catch (RestClientException e) {
            log.error("Error fetching GitHub user", e);
            throw new RuntimeException("Failed to fetch GitHub user", e);
        }
    }

    /**
     * Helper: persist or update the user's access token.
     * <p>
     * Execution:
     * - Look up UserToken by githubLogin; if found, update fields and save; otherwise create a new entity.
     * - Marked @Transactional to ensure DB operations are performed atomically.
     */
    @Transactional
    public void saveOrUpdate(
            String githubLogin,
            String accessToken,
            String tokenType,
            String scope
    ) {
        log.info("Saving/updating token for user={}", githubLogin);
        tokenRepo.findByGithubLogin(githubLogin)
                .map(existing -> {
                    existing.setAccessToken(accessToken);
                    existing.setTokenType(tokenType);
                    existing.setScope(scope);
                    existing.setCreatedAt(Instant.now());
                    return tokenRepo.save(existing);
                })
                .orElseGet(() -> {
                    UserToken token = new UserToken();
                    token.setGithubLogin(githubLogin);
                    token.setAccessToken(accessToken);
                    token.setTokenType(tokenType);
                    token.setScope(scope);
                    token.setCreatedAt(Instant.now());
                    return tokenRepo.save(token);
                });
    }

}
