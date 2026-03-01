package com.jobflow.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ClerkJwtFilter extends OncePerRequestFilter {

    @Value("${clerk.jwks-uri}")
    private String jwksUri;

    @Value("${clerk.issuer}")
    private String expectedIssuer;

    private final ObjectMapper objectMapper;
    private Key cachedPublicKey;
    private long lastJwksFetch = 0;
    private static final long JWKS_CACHE_DURATION = 3600000; // 1 hour

    public ClerkJwtFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = validateToken(token);

            // Basic issuer verification
            String issuer = claims.getIssuer();
            if (expectedIssuer != null && !expectedIssuer.equals(issuer)) {
                log.warn("JWT Issuer mismatch. Expected: {}, Got: {}", expectedIssuer, issuer);
                throw new IllegalStateException("Invalid issuer");
            }

            String clerkId = claims.getSubject();
            String role = (String) claims.getOrDefault("role", "FREE");

            var auth = new UsernamePasswordAuthenticationToken(
                    clerkId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Invalid or expired token"));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Claims validateToken(String token) {
        try {
            if (cachedPublicKey == null || System.currentTimeMillis() - lastJwksFetch > JWKS_CACHE_DURATION) {
                log.info("Fetching fresh JWKS from Clerk...");
                URL jwksUrl = URI.create(jwksUri).toURL();
                Map<?, ?> jwks = objectMapper.readValue(jwksUrl, Map.class);
                List<?> keys = (List<?>) jwks.get("keys");
                Map<?, ?> jwk = (Map<?, ?>) keys.get(0);

                String n = (String) jwk.get("n");
                String e = (String) jwk.get("e");

                java.math.BigInteger modulus = new java.math.BigInteger(1, Base64.getUrlDecoder().decode(n));
                java.math.BigInteger exponent = new java.math.BigInteger(1, Base64.getUrlDecoder().decode(e));
                java.security.spec.RSAPublicKeySpec keySpec = new java.security.spec.RSAPublicKeySpec(modulus,
                        exponent);
                cachedPublicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
                lastJwksFetch = System.currentTimeMillis();
            }

            return Jwts.parser()
                    .verifyWith((java.security.PublicKey) cachedPublicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to validate JWT token", e);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/actuator");
    }
}
