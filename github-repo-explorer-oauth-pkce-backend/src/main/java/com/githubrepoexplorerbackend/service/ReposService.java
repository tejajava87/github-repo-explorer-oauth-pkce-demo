package com.githubrepoexplorerbackend.service;

import com.githubrepoexplorerbackend.contoller.ReposController;
import com.githubrepoexplorerbackend.dto.RepoSummary;
import com.githubrepoexplorerbackend.entity.UserToken;
import com.githubrepoexplorerbackend.exception.TokenNotFoundException;
import com.githubrepoexplorerbackend.repository.UserTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ReposService {
    private static final Logger log = LoggerFactory.getLogger(ReposService.class);
    private final UserTokenRepository tokenRepo;

    public ReposService(UserTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetch repositories for a given GitHub login.
     *
     * Steps:
     * 1. Query the database for the access token associated with githubLogin.
     * 2. If not found, throw NotFoundException -> handled as 404.
     * 3. Call GitHub API /user/repos using the stored access token and return the parsed list.
     */
    public List<RepoSummary> getMyRepos(String githubLogin) {

        UserToken token = tokenRepo.findByGithubLogin(githubLogin)
                .orElseThrow(() -> new TokenNotFoundException("Token not found for user: " + githubLogin));

        log.info("Fetching repos for user={}", githubLogin);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());
        headers.set("Accept", "application/vnd.github+json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<RepoSummary>> response =
                restTemplate.exchange(
                        "https://api.github.com/user/repos",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {
                        }
                );

        return response.getBody();
    }
}
