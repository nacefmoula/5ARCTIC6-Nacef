package tn.esprit.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP_PROPERTY = "timestamp";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Ressource Introuvable");
        problemDetail.setType(URI.create("https://api.gestionprojets.tn/errors/not-found"));
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Échec de validation des données d'entrée."
        );
        problemDetail.setTitle("Erreur de Validation");
        problemDetail.setType(URI.create("https://api.gestionprojets.tn/errors/validation"));
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("invalidParams", errors);
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Requête Invalide");
        problemDetail.setType(URI.create("https://api.gestionprojets.tn/errors/bad-request"));
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage() != null ? ex.getMessage() : "Échec d'authentification."
        );
        problemDetail.setTitle("Non Authentifié");
        problemDetail.setType(URI.create("https://api.gestionprojets.tn/errors/unauthorized"));
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Accès refusé : vous n'avez pas les autorisations nécessaires."
        );
        problemDetail.setTitle("Accès Refusé");
        problemDetail.setType(URI.create("https://api.gestionprojets.tn/errors/forbidden"));
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur interne inattendue s'est produite."
        );
        problemDetail.setTitle("Erreur Interne du Serveur");
        problemDetail.setType(URI.create("https://api.gestionprojets.tn/errors/internal-server-error"));
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        return problemDetail;
    }
}
