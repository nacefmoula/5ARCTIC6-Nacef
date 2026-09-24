package tn.esprit.backend.dto;

public record EquipeResponseDTO(
    Long id,
    String nom,
    String specialite,
    EntrepriseResponseDTO entreprise
) {}
