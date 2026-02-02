package com.githubrepoexplorerbackend.service;

import com.githubrepoexplorerbackend.exception.OAuthExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
}
