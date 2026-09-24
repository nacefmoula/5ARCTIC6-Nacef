package tn.esprit.backend.dto;

import java.time.LocalDate;

public record ProjetDetailleResponseDTO(
    Long id,
    String description,
    String technologie,
    Double coutProvisoire,
    LocalDate dateDebut,
    ProjetResponseDTO projet
) {}
