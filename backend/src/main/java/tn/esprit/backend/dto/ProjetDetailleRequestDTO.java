package tn.esprit.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjetDetailleRequestDTO(
    Long id,

    @NotBlank(message = "La description est obligatoire.")
    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères.")
    String description,

    @NotBlank(message = "La technologie est obligatoire.")
    @Size(max = 100, message = "La technologie ne doit pas dépasser 100 caractères.")
    String technologie,

    @NotNull(message = "Le coût provisoire est obligatoire.")
    @PositiveOrZero(message = "Le coût provisoire doit être positif ou nul.")
    Double coutProvisoire,

    @NotNull(message = "La date de début est obligatoire.")
    LocalDate dateDebut,

    Long projetId,

    ProjetRefDTO projet
) {}
