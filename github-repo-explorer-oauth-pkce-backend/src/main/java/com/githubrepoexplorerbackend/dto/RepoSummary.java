package com.githubrepoexplorerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RepoSummary {

    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("html_url")
    private String htmlUrl;

    private String description;

    @JsonProperty("stargazers_count")
    private int stars;

    @JsonProperty("language")
    private String language;

}
