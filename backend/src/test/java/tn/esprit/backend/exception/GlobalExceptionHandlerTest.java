package tn.esprit.backend.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException and return 404 ProblemDetail")
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex1 = new ResourceNotFoundException("Entreprise", 123L);
        ProblemDetail pd1 = exceptionHandler.handleResourceNotFoundException(ex1);

        assertThat(pd1.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(pd1.getTitle()).isEqualTo("Ressource Introuvable");
        assertThat(pd1.getDetail()).contains("Entreprise").contains("123");
        assertThat(pd1.getProperties()).containsKey("timestamp");

        ResourceNotFoundException ex2 = new ResourceNotFoundException("Direct message not found");
        ProblemDetail pd2 = exceptionHandler.handleResourceNotFoundException(ex2);
        assertThat(pd2.getDetail()).isEqualTo("Direct message not found");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return 400 ProblemDetail with invalidParams")
    void testHandleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "nom", "Le nom est obligatoire");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ProblemDetail pd = exceptionHandler.handleValidationException(ex);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getTitle()).isEqualTo("Erreur de Validation");
        assertThat(pd.getProperties()).containsKey("invalidParams");
        assertThat(pd.getProperties().get("invalidParams")).isNotNull();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException and return 400 ProblemDetail")
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Paramètre invalide");
        ProblemDetail pd = exceptionHandler.handleIllegalArgumentException(ex);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getTitle()).isEqualTo("Requête Invalide");
        assertThat(pd.getDetail()).isEqualTo("Paramètre invalide");
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500 ProblemDetail")
    void testHandleGlobalException() {
        Exception ex = new RuntimeException("Unexpected error");
        ProblemDetail pd = exceptionHandler.handleGlobalException(ex);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(pd.getTitle()).isEqualTo("Erreur Interne du Serveur");
        assertThat(pd.getDetail()).isEqualTo("Une erreur interne inattendue s'est produite.");
    }

    @Test
    @DisplayName("Should handle AuthenticationException and return 401 ProblemDetail")
    void testHandleAuthenticationException() {
        org.springframework.security.authentication.BadCredentialsException ex =
                new org.springframework.security.authentication.BadCredentialsException("Mauvais identifiants");
        ProblemDetail pd = exceptionHandler.handleAuthenticationException(ex);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(pd.getTitle()).isEqualTo("Non Authentifié");
        assertThat(pd.getDetail()).isEqualTo("Mauvais identifiants");
        assertThat(pd.getProperties()).containsKey("timestamp");
    }

    @Test
    @DisplayName("Should handle AccessDeniedException and return 403 ProblemDetail")
    void testHandleAccessDeniedException() {
        org.springframework.security.access.AccessDeniedException ex =
                new org.springframework.security.access.AccessDeniedException("Accès refusé");
        ProblemDetail pd = exceptionHandler.handleAccessDeniedException(ex);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(pd.getTitle()).isEqualTo("Accès Refusé");
        assertThat(pd.getDetail()).contains("Accès refusé");
        assertThat(pd.getProperties()).containsKey("timestamp");
    }
}
