package org.example.cosmocats.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cosmocats.config.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!securityProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        // CRITICAL FIX: Check if JWT Bearer token is present - if so, skip API key validation
        // This ensures JWT takes precedence over API key authentication
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            // JWT token is present, let it be handled by oauth2ResourceServer
            filterChain.doFilter(request, response);
            return;
        }

        // No JWT token present, proceed with API key authentication
        String apiKey = request.getHeader(securityProperties.getApiKeyHeader());

        if (!StringUtils.hasText(apiKey)) {
            // No API key either, continue to next filter (will be rejected later if required)
            filterChain.doFilter(request, response);
            return;
        }

        // API key is present, validate it
        if (securityProperties.getApiKey().equals(apiKey)) {
            var authentication = new UsernamePasswordAuthenticationToken(
                    "api-user-" + apiKey.substring(0, Math.min(8, apiKey.length())),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_API_USER"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            // Invalid API key
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            String jsonResponse = String.format("""
                {
                    "status": %d,
                    "message": "Invalid API Key",
                    "timestamp": "%s"
                }
                """, HttpStatus.UNAUTHORIZED.value(),
                    LocalDateTime.now().toString());

            response.getWriter().write(jsonResponse);
        }
    }
}