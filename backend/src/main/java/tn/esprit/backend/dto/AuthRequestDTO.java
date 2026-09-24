package tn.esprit.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
    @NotBlank(message = "Le nom d'utilisateur est obligatoire.")
    String username,

    @NotBlank(message = "Le mot de passe est obligatoire.")
    String password
) {}
