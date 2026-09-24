package tn.esprit.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${jwt.secret:defaultSecretKeyWithAtLeast256BitsLengthForHMACSHA256AlgorithmTesting123456}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @SuppressWarnings("java:S2143") // Justification: io.jsonwebtoken API requires java.util.Date for issuedAt and expiration
    public String generateToken(String username, List<String> roles) {
        Instant now = Instant.now();
        Instant expiration = now.plus(Duration.ofMillis(jwtExpirationMs));

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateTokenFromAuthorities(String username, java.util.Collection<? extends GrantedAuthority> authorities) {
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return generateToken(username, roles);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Object rolesClaim = extractAllClaims(token).get("roles");
        if (rolesClaim instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return Collections.emptyList();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
