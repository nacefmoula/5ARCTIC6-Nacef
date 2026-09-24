package tn.esprit.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjetRequestDTO(
    Long id,

    @NotBlank(message = "Le sujet du projet est obligatoire.")
    @Size(min = 2, max = 150, message = "Le sujet doit comporter entre 2 et 150 caractères.")
    String sujet
) {}
