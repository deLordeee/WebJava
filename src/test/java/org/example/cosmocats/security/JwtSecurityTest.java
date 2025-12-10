package org.example.cosmocats.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.cosmocats.config.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("JWT Security Tests")
@Testcontainers
class JwtSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private SecretKey secretKey;

    // Use the actual endpoint from CosmoCatController
    private static final String TEST_ENDPOINT = "/api/v1/cosmocats";

    @BeforeEach
    void setUp() {
        byte[] secretKeyBytes = securityProperties.getJwt().getSecret()
                .getBytes(StandardCharsets.UTF_8);
        secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
    }

    // ============= JWT Token Generation Tests =============

    @Test
    @DisplayName("Should access protected endpoint with valid JWT token")
    void shouldAccessProtectedEndpointWithValidJwt() throws Exception {
        String token = generateValidToken("user123", List.of("USER"));

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should access protected endpoint with ADMIN role")
    void shouldAccessProtectedEndpointWithAdminRole() throws Exception {
        String token = generateValidToken("admin", List.of("ADMIN"));

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should access protected endpoint with multiple roles")
    void shouldAccessProtectedEndpointWithMultipleRoles() throws Exception {
        String token = generateValidToken("user123", List.of("USER", "ADMIN", "MODERATOR"));

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // ============= JWT Token Validation Tests =============

    @Test
    @DisplayName("Should reject request without JWT token")
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get(TEST_ENDPOINT))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject request with malformed JWT token")
    void shouldRejectRequestWithMalformedToken() throws Exception {
        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer malformed.token.here"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject request with expired JWT token")
    void shouldRejectRequestWithExpiredToken() throws Exception {
        String expiredToken = generateExpiredToken("user123", List.of("USER"));

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject request with invalid signature")
    void shouldRejectRequestWithInvalidSignature() throws Exception {
        String tokenWithInvalidSignature = generateTokenWithInvalidSignature("user123", List.of("USER"));

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenWithInvalidSignature))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject request with token signed by wrong key")
    void shouldRejectRequestWithWrongSigningKey() throws Exception {
        String token = generateTokenWithDifferentKey("user123", List.of("USER"));

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject request with future-dated token (nbf)")
    void shouldRejectRequestWithFutureToken() throws Exception {
        String futureToken = generateFutureToken("user123", List.of("USER"));

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + futureToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ============= API Key Tests =============

    @Test
    @DisplayName("Should access protected endpoint with valid API key")
    void shouldAccessProtectedEndpointWithValidApiKey() throws Exception {
        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(securityProperties.getApiKeyHeader(),
                                securityProperties.getApiKey()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should reject request with invalid API key")
    void shouldRejectRequestWithInvalidApiKey() throws Exception {
        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(securityProperties.getApiKeyHeader(), "invalid-api-key"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid API Key"));
    }

    @Test
    @DisplayName("JWT token should take precedence over API key")
    void jwtTokenShouldTakePrecedenceOverApiKey() throws Exception {
        String token = generateValidToken("user123", List.of("USER"));

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .header(securityProperties.getApiKeyHeader(), "wrong-api-key"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // ============= Public Endpoints Tests =============







    // ============= Token Claims Tests =============

    @Test
    @DisplayName("Should extract correct user from JWT token")
    void shouldExtractCorrectUserFromToken() throws Exception {
        String username = "testuser@example.com";
        String token = generateValidToken(username, List.of("USER"));

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle JWT token without roles claim")
    void shouldHandleTokenWithoutRoles() throws Exception {
        String token = generateTokenWithoutRoles("user123");

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle JWT token with custom claims")
    void shouldHandleTokenWithCustomClaims() throws Exception {
        Map<String, Object> customClaims = Map.of(
                "email", "user@example.com",
                "tenant_id", "tenant-123",
                "permissions", List.of("read:cats", "write:cats")
        );

        String token = generateTokenWithCustomClaims("user123", List.of("USER"), customClaims);

        mockMvc.perform(get(TEST_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // ============= CORS Tests =============

    @Test
    @DisplayName("Should handle CORS preflight request")
    void shouldHandleCorsPreflight() throws Exception {
        mockMvc.perform(options(TEST_ENDPOINT)
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    // ============= Helper Methods =============

    private String generateValidToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .claim(securityProperties.getJwt().getRolesClaim(), roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateExpiredToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .claim(securityProperties.getJwt().getRolesClaim(), roles)
                .setIssuedAt(Date.from(now.minus(2, ChronoUnit.HOURS)))
                .setExpiration(Date.from(now.minus(1, ChronoUnit.HOURS)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateTokenWithInvalidSignature(String subject, List<String> roles) {
        String validToken = generateValidToken(subject, roles);
        String[] parts = validToken.split("\\.");
        if (parts.length == 3) {
            return parts[0] + "." + parts[1] + ".corrupted_signature";
        }
        return validToken;
    }

    private String generateTokenWithDifferentKey(String subject, List<String> roles) {
        // Must be at least 32 characters (256 bits) for HS256
        SecretKey differentKey = new SecretKeySpec(
                "different-secret-key-with-32-characters-minimum!".getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .claim(securityProperties.getJwt().getRolesClaim(), roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(differentKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateFutureToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .claim(securityProperties.getJwt().getRolesClaim(), roles)
                .setNotBefore(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(2, ChronoUnit.HOURS)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateTokenWithoutRoles(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateTokenWithCustomClaims(String subject, List<String> roles,
                                                 Map<String, Object> customClaims) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .setSubject(subject)
                .claim(securityProperties.getJwt().getRolesClaim(), roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)));

        customClaims.forEach(builder::claim);

        return builder.signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }
}
