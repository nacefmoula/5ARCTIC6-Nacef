package tn.esprit.backend.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("doFilterInternal should authenticate user when valid Bearer token is provided")
    void testDoFilterInternalValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails userDetails = new User("admin", "pass", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(jwtUtils.validateToken("valid.jwt.token")).thenReturn(true);
        when(jwtUtils.extractUsername("valid.jwt.token")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal should not authenticate when no Authorization header is present")
    void testDoFilterInternalNoHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal should not authenticate when invalid Bearer token is provided")
    void testDoFilterInternalInvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateToken("invalid.jwt.token")).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("shouldNotFilter should return true for auth, actuator and swagger routes")
    void testShouldNotFilterPublicPaths() {
        MockHttpServletRequest reqAuth = new MockHttpServletRequest();
        reqAuth.setServletPath("/api/auth/login");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(reqAuth)).isTrue();

        MockHttpServletRequest reqAuthShort = new MockHttpServletRequest();
        reqAuthShort.setServletPath("/auth/register");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(reqAuthShort)).isTrue();

        MockHttpServletRequest reqActuator = new MockHttpServletRequest();
        reqActuator.setServletPath("/actuator/health");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(reqActuator)).isTrue();

        MockHttpServletRequest reqSwagger = new MockHttpServletRequest();
        reqSwagger.setServletPath("/swagger-ui/index.html");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(reqSwagger)).isTrue();
    }

    @Test
    @DisplayName("shouldNotFilter should return false for business routes and null path")
    void testShouldNotFilterProtectedPaths() {
        MockHttpServletRequest reqBusiness = new MockHttpServletRequest();
        reqBusiness.setServletPath("/api/entreprise/1");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(reqBusiness)).isFalse();

        MockHttpServletRequest reqNull = new MockHttpServletRequest();
        reqNull.setServletPath(null);
        assertThat(jwtAuthenticationFilter.shouldNotFilter(reqNull)).isFalse();
    }
}
