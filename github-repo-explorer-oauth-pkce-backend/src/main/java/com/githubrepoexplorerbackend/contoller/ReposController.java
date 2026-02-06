package com.githubrepoexplorerbackend.contoller;

import com.githubrepoexplorerbackend.dto.RepoSummary;
import com.githubrepoexplorerbackend.entity.UserToken;
import com.githubrepoexplorerbackend.exception.TokenNotFoundException;
import com.githubrepoexplorerbackend.repository.UserTokenRepository;
import com.githubrepoexplorerbackend.service.ReposService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/repos")
public class ReposController {

    @Autowired
    ReposService reposService;
    /**
     * GET /api/repos
     *
     * Execution flow:
     * 1. Ensure the incoming request has an authenticated principal (Spring injects Authentication).
     * 2. Use the principal name as the githubLogin and call getMyRepos(githubLogin).
     */
    @GetMapping
    public List<RepoSummary> myRepos(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Not authenticated");
        }

        return reposService.getMyRepos(authentication.getName());
    }

}
