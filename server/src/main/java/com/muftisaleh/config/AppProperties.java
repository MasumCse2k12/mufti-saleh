package com.muftisaleh.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Every tunable the service has, bound once and validated at startup.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Jwt jwt,
        Security security,
        Cors cors,
        Bootstrap bootstrap) {

    public record Jwt(
            @NotBlank String secret,
            @DefaultValue("mufti-saleh") String issuer,
            @DefaultValue("PT8H") Duration adminAccessTtl) {
    }

    public record Security(
            @DefaultValue("5") @Positive int maxFailedLogins,
            @DefaultValue("PT15M") Duration lockoutDuration) {
    }

    public record Cors(@DefaultValue("http://localhost:5173") List<String> allowedOrigins) {
    }

    public record Bootstrap(
            @DefaultValue("true") boolean enabled,
            @DefaultValue("admin") String username,
            String password,
            @DefaultValue("Super Admin") String displayName) {
    }
}
