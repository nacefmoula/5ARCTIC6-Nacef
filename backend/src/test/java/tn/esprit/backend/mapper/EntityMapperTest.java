package tn.esprit.backend.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tn.esprit.backend.dto.*;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.entity.ProjetDetaille;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMapperTest {

    // ==================== Entreprise ====================
    @Test
    @DisplayName("Should map EntrepriseRequestDTO to Entreprise entity and handle null")
    void testEntrepriseToEntity() {
        assertThat(EntityMapper.toEntity((EntrepriseRequestDTO) null)).isNull();

        EntrepriseRequestDTO dto = new EntrepriseRequestDTO(1L, "ESPRIT", "Tunis");
        Entreprise entity = EntityMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNom()).isEqualTo("ESPRIT");
        assertThat(entity.getAdresse()).isEqualTo("Tunis");
    }

    @Test
    @DisplayName("Should map Entreprise entity to EntrepriseResponseDTO and handle null")
    void testEntrepriseToResponse() {
        assertThat(EntityMapper.toResponse((Entreprise) null)).isNull();

        Entreprise entity = Entreprise.builder().id(2L).nom("Cloud Tech").adresse("Ariana").build();
        EntrepriseResponseDTO response = EntityMapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.nom()).isEqualTo("Cloud Tech");
        assertThat(response.adresse()).isEqualTo("Ariana");
    }

    // ==================== Equipe ====================
    @Test
    @DisplayName("Should map EquipeRequestDTO to Equipe entity with entrepriseId")
    void testEquipeToEntityWithEntrepriseId() {
        assertThat(EntityMapper.toEntity((EquipeRequestDTO) null)).isNull();

        EquipeRequestDTO dto = new EquipeRequestDTO(10L, "DevOps Team", "Cloud", 5L, null);
        Equipe entity = EntityMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getNom()).isEqualTo("DevOps Team");
        assertThat(entity.getSpecialite()).isEqualTo("Cloud");
        assertThat(entity.getEntreprise()).isNotNull();
        assertThat(entity.getEntreprise().getId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should map EquipeRequestDTO to Equipe entity with EntrepriseRefDTO")
    void testEquipeToEntityWithEntrepriseRef() {
        EntrepriseRefDTO ref = new EntrepriseRefDTO(7L, "Telecom");
        EquipeRequestDTO dto = new EquipeRequestDTO(11L, "QA Team", "Testing", null, ref);
        Equipe entity = EntityMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getEntreprise()).isNotNull();
        assertThat(entity.getEntreprise().getId()).isEqualTo(7L);
    }

    @Test
    @DisplayName("Should map EquipeRequestDTO to Equipe entity without entreprise")
    void testEquipeToEntityWithoutEntreprise() {
        EquipeRequestDTO dto = new EquipeRequestDTO(12L, "Solo Team", "R&D", null, null);
        Equipe entity = EntityMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getEntreprise()).isNull();
    }

    @Test
    @DisplayName("Should map Equipe entity to EquipeResponseDTO and handle null")
    void testEquipeToResponse() {
        assertThat(EntityMapper.toResponse((Equipe) null)).isNull();

        Entreprise ent = Entreprise.builder().id(1L).nom("ESPRIT").adresse("Tunis").build();
        Equipe equipeWithEnt = Equipe.builder().id(20L).nom("Backend").specialite("Java").entreprise(ent).build();
        EquipeResponseDTO res1 = EntityMapper.toResponse(equipeWithEnt);

        assertThat(res1).isNotNull();
        assertThat(res1.id()).isEqualTo(20L);
        assertThat(res1.entreprise()).isNotNull();
        assertThat(res1.entreprise().id()).isEqualTo(1L);

        Equipe equipeNoEnt = Equipe.builder().id(21L).nom("Frontend").specialite("Angular").entreprise(null).build();
        EquipeResponseDTO res2 = EntityMapper.toResponse(equipeNoEnt);
        assertThat(res2).isNotNull();
        assertThat(res2.entreprise()).isNull();
    }

    // ==================== Projet ====================
    @Test
    @DisplayName("Should map ProjetRequestDTO to Projet entity and handle null")
    void testProjetToEntity() {
        assertThat(EntityMapper.toEntity((ProjetRequestDTO) null)).isNull();

        ProjetRequestDTO dto = new ProjetRequestDTO(100L, "Plateforme Kubernetes");
        Projet entity = EntityMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(100L);
        assertThat(entity.getSujet()).isEqualTo("Plateforme Kubernetes");
    }

    @Test
    @DisplayName("Should map Projet entity to ProjetResponseDTO and handle null")
    void testProjetToResponse() {
        assertThat(EntityMapper.toResponse((Projet) null)).isNull();

        Projet entity = Projet.builder().id(101L).sujet("Migration Cloud").build();
        ProjetResponseDTO res = EntityMapper.toResponse(entity);

        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(101L);
        assertThat(res.sujet()).isEqualTo("Migration Cloud");
    }

    // ==================== Projet Détaillé ====================
    @Test
    @DisplayName("Should map ProjetDetailleRequestDTO to entity with projetId")
    void testProjetDetailleToEntityWithProjetId() {
        assertThat(EntityMapper.toEntity((ProjetDetailleRequestDTO) null)).isNull();

        LocalDate now = LocalDate.now();
        ProjetDetailleRequestDTO dto = new ProjetDetailleRequestDTO(
                1L, "CI/CD Pipeline", "Jenkins & Docker", 15000.0, now, 50L, null
        );
        ProjetDetaille entity = EntityMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getDescription()).isEqualTo("CI/CD Pipeline");
        assertThat(entity.getTechnologie()).isEqualTo("Jenkins & Docker");
        assertThat(entity.getCoutProvisoire()).isEqualTo(15000.0);
        assertThat(entity.getDateDebut()).isEqualTo(now);
        assertThat(entity.getProjet()).isNotNull();
        assertThat(entity.getProjet().getId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Should map ProjetDetailleRequestDTO to entity with ProjetRefDTO")
    void testProjetDetailleToEntityWithProjetRef() {
        ProjetRefDTO ref = new ProjetRefDTO(55L, "Ref Project");
        ProjetDetailleRequestDTO dto = new ProjetDetailleRequestDTO(
                2L, "IaC Setup", "Terraform", 8000.0, LocalDate.now(), null, ref
        );
        ProjetDetaille entity = EntityMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getProjet()).isNotNull();
        assertThat(entity.getProjet().getId()).isEqualTo(55L);
    }

    @Test
    @DisplayName("Should map ProjetDetailleRequestDTO to entity without projet")
    void testProjetDetailleToEntityWithoutProjet() {
        ProjetDetailleRequestDTO dto = new ProjetDetailleRequestDTO(
                3L, "Audit", "SonarQube", 4000.0, LocalDate.now(), null, null
        );
        ProjetDetaille entity = EntityMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getProjet()).isNull();
    }

    @Test
    @DisplayName("Should map ProjetDetaille entity to response and handle null")
    void testProjetDetailleToResponse() {
        assertThat(EntityMapper.toResponse((ProjetDetaille) null)).isNull();

        Projet p = Projet.builder().id(99L).sujet("Main Project").build();
        ProjetDetaille pdWithProj = ProjetDetaille.builder()
                .id(1L)
                .description("Detail description")
                .technologie("Spring Boot")
                .coutProvisoire(12000.0)
                .dateDebut(LocalDate.of(2026, 1, 1))
                .projet(p)
                .build();
        ProjetDetailleResponseDTO res1 = EntityMapper.toResponse(pdWithProj);

        assertThat(res1).isNotNull();
        assertThat(res1.id()).isEqualTo(1L);
        assertThat(res1.projet()).isNotNull();
        assertThat(res1.projet().id()).isEqualTo(99L);

        ProjetDetaille pdNoProj = ProjetDetaille.builder()
                .id(2L)
                .description("No project")
                .technologie("None")
                .coutProvisoire(0.0)
                .dateDebut(LocalDate.now())
                .projet(null)
                .build();
        ProjetDetailleResponseDTO res2 = EntityMapper.toResponse(pdNoProj);

        assertThat(res2).isNotNull();
        assertThat(res2.projet()).isNull();
    }
}
