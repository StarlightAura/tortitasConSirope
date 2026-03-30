package org.tfg.api.mysql.login.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("Refresh_token")
        String refreshToken
) {
}
