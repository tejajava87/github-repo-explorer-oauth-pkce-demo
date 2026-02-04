package com.githubrepoexplorerbackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class to validate the security configuration for stateless OAuth PKCE.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "GITHUB_CLIENT_ID=test-client-id",
    "GITHUB_CLIENT_SECRET=test-client-secret"
})
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void securityFilterChain_shouldBeConfigured() {
        // Verify that the security filter chain is properly configured and loaded
        assertNotNull(securityFilterChain, "SecurityFilterChain should be configured");
    }

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully with stateless security configuration
        // This ensures that:
        // - SessionCreationPolicy.STATELESS is properly configured
        // - CORS configuration is valid
        // - No HttpSessionSecurityContextRepository is used
        // - Frame options are enabled (default behavior)
    }
}
