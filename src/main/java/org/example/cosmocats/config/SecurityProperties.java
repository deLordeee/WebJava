package org.example.cosmocats.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private boolean enabled = true;
    private String apiKeyHeader = "X-API-KEY";
    private String apiKey;
    private Jwt jwt = new Jwt();



    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private String rolesClaim = "roles";
        private String issuer;
        private String audience;
    }
}
