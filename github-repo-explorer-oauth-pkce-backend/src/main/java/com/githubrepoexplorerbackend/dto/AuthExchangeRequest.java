package com.githubrepoexplorerbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthExchangeRequest(
        @NotBlank String code,
        @NotBlank String codeVerifier,
        @NotBlank String redirectUri
) {}
