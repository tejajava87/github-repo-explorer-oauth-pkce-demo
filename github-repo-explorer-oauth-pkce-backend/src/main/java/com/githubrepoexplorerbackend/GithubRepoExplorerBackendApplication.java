package com.githubrepoexplorerbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GithubRepoExplorerBackendApplication {

    /**
     * Application entry point. Spring Boot initializes the application context
     * and starts the embedded servlet container.
     */
    public static void main(String[] args) {
        SpringApplication.run(GithubRepoExplorerBackendApplication.class, args);
    }

}
