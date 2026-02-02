package com.githubrepoexplorerbackend.repository;


import com.githubrepoexplorerbackend.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    /**
     * Find a user token by the GitHub login name. Used by controllers to locate
     * the stored access token for API calls on behalf of the user.
     */
    Optional<UserToken> findByGithubLogin(String githubLogin);
}
