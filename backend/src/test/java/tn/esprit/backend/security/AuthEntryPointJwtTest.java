package tn.esprit.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class AuthEntryPointJwtTest {

    private final AuthEntryPointJwt entryPoint = new AuthEntryPointJwt();

    @Test
    @DisplayName("commence should write 401 Unauthorized ProblemDetail to response")
    void testCommence() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        BadCredentialsException exception = new BadCredentialsException("Mauvais mot de passe");

        entryPoint.commence(request, response, exception);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).contains("application/problem+json");
        assertThat(response.getContentAsString()).contains("Non Authentifié");
        assertThat(response.getContentAsString()).contains("Mauvais mot de passe");
    }
}
