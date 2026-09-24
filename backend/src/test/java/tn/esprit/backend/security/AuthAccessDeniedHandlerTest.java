package tn.esprit.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class AuthAccessDeniedHandlerTest {

    private final AuthAccessDeniedHandler accessDeniedHandler = new AuthAccessDeniedHandler();

    @Test
    @DisplayName("handle should write 403 Forbidden ProblemDetail to response")
    void testHandle() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException exception = new AccessDeniedException("Accès refusé");

        accessDeniedHandler.handle(request, response, exception);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).contains("application/problem+json");
        assertThat(response.getContentAsString()).contains("Accès Refusé");
        assertThat(response.getContentAsString()).contains("vous ne possédez pas les autorisations nécessaires");
    }
}
