package tn.esprit.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntrepriseRequestDTO(
    Long id,

    @NotBlank(message = "Le nom de l'entreprise est obligatoire.")
    @Size(min = 2, max = 100, message = "Le nom doit comporter entre 2 et 100 caractères.")
    String nom,

    @NotBlank(message = "L'adresse de l'entreprise est obligatoire.")
    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères.")
    String adresse
) {}
