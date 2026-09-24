package tn.esprit.backend.mapper;

import tn.esprit.backend.dto.*;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.entity.ProjetDetaille;

public final class EntityMapper {

    private EntityMapper() {}

    // ==================== Entreprise ====================
    public static Entreprise toEntity(EntrepriseRequestDTO dto) {
        if (dto == null) return null;
        return Entreprise.builder()
                .id(dto.id())
                .nom(dto.nom())
                .adresse(dto.adresse())
                .build();
    }

    public static EntrepriseResponseDTO toResponse(Entreprise entity) {
        if (entity == null) return null;
        return new EntrepriseResponseDTO(
                entity.getId(),
                entity.getNom(),
                entity.getAdresse()
        );
    }

    // ==================== Equipe ====================
    public static Equipe toEntity(EquipeRequestDTO dto) {
        if (dto == null) return null;
        Entreprise entreprise = null;
        Long entId = dto.entrepriseId() != null ? dto.entrepriseId() :
                     (dto.entreprise() != null ? dto.entreprise().id() : null);
        if (entId != null) {
            entreprise = Entreprise.builder().id(entId).build();
        }
        return Equipe.builder()
                .id(dto.id())
                .nom(dto.nom())
                .specialite(dto.specialite())
                .entreprise(entreprise)
                .build();
    }

    public static EquipeResponseDTO toResponse(Equipe entity) {
        if (entity == null) return null;
        return new EquipeResponseDTO(
                entity.getId(),
                entity.getNom(),
                entity.getSpecialite(),
                toResponse(entity.getEntreprise())
        );
    }

    // ==================== Projet ====================
    public static Projet toEntity(ProjetRequestDTO dto) {
        if (dto == null) return null;
        return Projet.builder()
                .id(dto.id())
                .sujet(dto.sujet())
                .build();
    }

    public static ProjetResponseDTO toResponse(Projet entity) {
        if (entity == null) return null;
        return new ProjetResponseDTO(
                entity.getId(),
                entity.getSujet()
        );
    }

    // ==================== Projet Détaillé ====================
    public static ProjetDetaille toEntity(ProjetDetailleRequestDTO dto) {
        if (dto == null) return null;
        Projet projet = null;
        Long pId = dto.projetId() != null ? dto.projetId() :
                   (dto.projet() != null ? dto.projet().id() : null);
        if (pId != null) {
            projet = Projet.builder().id(pId).build();
        }
        return ProjetDetaille.builder()
                .id(dto.id())
                .description(dto.description())
                .technologie(dto.technologie())
                .coutProvisoire(dto.coutProvisoire())
                .dateDebut(dto.dateDebut())
                .projet(projet)
                .build();
    }

    public static ProjetDetailleResponseDTO toResponse(ProjetDetaille entity) {
        if (entity == null) return null;
        return new ProjetDetailleResponseDTO(
                entity.getId(),
                entity.getDescription(),
                entity.getTechnologie(),
                entity.getCoutProvisoire(),
                entity.getDateDebut(),
                toResponse(entity.getProjet())
        );
    }
}
