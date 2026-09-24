package tn.esprit.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipeRequestDTO(
    Long id,

    @NotBlank(message = "Le nom de l'équipe est obligatoire.")
    @Size(min = 2, max = 100, message = "Le nom doit comporter entre 2 et 100 caractères.")
    String nom,

    @NotBlank(message = "La spécialité de l'équipe est obligatoire.")
    @Size(min = 2, max = 100, message = "La spécialité doit comporter entre 2 et 100 caractères.")
    String specialite,

    Long entrepriseId,

    EntrepriseRefDTO entreprise
) {}
