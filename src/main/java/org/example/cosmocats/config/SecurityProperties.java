package org.example.cosmocats.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private boolean enabled = true;
    private String apiKeyHeader = "X-API-KEY";
    private String apiKey = "cosmo-cats-api-key";
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Jwt {
        private String secret = "your-256-bit-secret-for-cosmo-cats-marketplace-jwt-signing";
        private String rolesClaim = "roles";
    }
}