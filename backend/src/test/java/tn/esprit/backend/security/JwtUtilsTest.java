package tn.esprit.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String secret = "TestSecretKeyWithAtLeast256BitsLengthForHMACSHA256DevOpsAppGestionProjets2026";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L); // 1 hour
    }

    @Test
    @DisplayName("generateToken and extract claims should correctly extract username and roles")
    void testGenerateAndExtractClaims() {
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");
        String token = jwtUtils.generateToken("admin", roles);

        assertNotNull(token);
        assertFalse(token.isBlank());

        String username = jwtUtils.extractUsername(token);
        assertEquals("admin", username);

        List<String> extractedRoles = jwtUtils.extractRoles(token);
        assertEquals(2, extractedRoles.size());
        assertTrue(extractedRoles.contains("ROLE_ADMIN"));
        assertTrue(extractedRoles.contains("ROLE_USER"));
    }

    @Test
    @DisplayName("generateTokenFromAuthorities should map GrantedAuthorities properly")
    void testGenerateTokenFromAuthorities() {
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        String token = jwtUtils.generateTokenFromAuthorities("testuser", authorities);
        assertNotNull(token);

        assertEquals("testuser", jwtUtils.extractUsername(token));
        List<String> extractedRoles = jwtUtils.extractRoles(token);
        assertEquals(List.of("ROLE_USER"), extractedRoles);
    }

    @Test
    @DisplayName("validateToken should return true for valid token")
    void testValidateTokenValid() {
        String token = jwtUtils.generateToken("validuser", List.of("ROLE_USER"));
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    @DisplayName("validateToken should return false for invalid or malformed token")
    void testValidateTokenInvalid() {
        assertFalse(jwtUtils.validateToken("invalid.token.structure"));
        assertFalse(jwtUtils.validateToken(""));
    }

    @Test
    @DisplayName("validateToken should return false for expired token")
    void testValidateTokenExpired() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000L); // already expired
        String expiredToken = jwtUtils.generateToken("expiredUser", List.of("ROLE_USER"));

        assertFalse(jwtUtils.validateToken(expiredToken));
    }
}
