package tn.esprit.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.entity.ProjetDetaille;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.repository.ProjetDetailleRepository;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.impl.ProjetDetailleServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetDetailleServiceTest {

    @Mock
    private ProjetDetailleRepository projetDetailleRepository;

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private ProjetDetailleServiceImpl projetDetailleService;

    private ProjetDetaille projetDetaille;
    private Projet projet;

    @BeforeEach
    void setUp() {
        projet = Projet.builder().id(1L).sujet("Main Project").build();
        projetDetaille = ProjetDetaille.builder()
                .id(1L)
                .description("Detail technical specification")
                .technologie("Spring Boot & Angular")
                .coutProvisoire(25000.0)
                .dateDebut(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully add a projet detaille")
    void testAddProjetDetaille() {
        when(projetDetailleRepository.save(any(ProjetDetaille.class))).thenReturn(projetDetaille);

        ProjetDetaille saved = projetDetailleService.addProjetDetaille(projetDetaille);

        assertThat(saved).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Detail technical specification");
        verify(projetDetailleRepository, times(1)).save(projetDetaille);
    }

    @Test
    @DisplayName("Should successfully update a projet detaille")
    void testUpdateProjetDetaille() {
        when(projetDetailleRepository.save(any(ProjetDetaille.class))).thenReturn(projetDetaille);

        ProjetDetaille updated = projetDetailleService.updateProjetDetaille(projetDetaille);

        assertThat(updated).isNotNull();
        verify(projetDetailleRepository, times(1)).save(projetDetaille);
    }

    @Test
    @DisplayName("Should delete a projet detaille by ID")
    void testDeleteProjetDetaille() {
        doNothing().when(projetDetailleRepository).deleteById(1L);

        projetDetailleService.deleteProjetDetaille(1L);

        verify(projetDetailleRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should find projet detaille by ID when exists")
    void testGetProjetDetailleByIdFound() {
        when(projetDetailleRepository.findById(1L)).thenReturn(Optional.of(projetDetaille));

        ProjetDetaille found = projetDetailleService.getProjetDetailleById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(projetDetailleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when projet detaille does not exist")
    void testGetProjetDetailleByIdNotFound() {
        when(projetDetailleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetDetailleService.getProjetDetailleById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should return all projet detailles")
    void testGetAllProjetsDetailles() {
        when(projetDetailleRepository.findAll()).thenReturn(List.of(projetDetaille));

        List<ProjetDetaille> list = projetDetailleService.getAllProjetsDetailles();

        assertThat(list).hasSize(1);
        verify(projetDetailleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return paged projet detailles")
    void testGetAllProjetsDetaillesPaged() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProjetDetaille> page = new PageImpl<>(List.of(projetDetaille), pageable, 1);
        when(projetDetailleRepository.findAll(pageable)).thenReturn(page);

        Page<ProjetDetaille> result = projetDetailleService.getAllProjetsDetailles(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(projetDetailleRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return projet detailles by projet ID")
    void testGetProjetDetaillesByProjet() {
        when(projetDetailleRepository.findByProjetId(1L)).thenReturn(List.of(projetDetaille));

        List<ProjetDetaille> list = projetDetailleService.getProjetDetaillesByProjet(1L);

        assertThat(list).hasSize(1);
        verify(projetDetailleRepository, times(1)).findByProjetId(1L);
    }

    @Test
    @DisplayName("Should assign projet detaille to projet successfully")
    void testAssignProjetDetailleToProjetSuccess() {
        when(projetDetailleRepository.findById(1L)).thenReturn(Optional.of(projetDetaille));
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));
        when(projetDetailleRepository.save(any(ProjetDetaille.class))).thenReturn(projetDetaille);

        ProjetDetaille result = projetDetailleService.assignProjetDetailleToProjet(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(projetDetaille.getProjet()).isEqualTo(projet);
        verify(projetDetailleRepository, times(1)).save(projetDetaille);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning non-existing projet detaille")
    void testAssignProjetDetailleToProjetDetailleNotFound() {
        when(projetDetailleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetDetailleService.assignProjetDetailleToProjet(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning to non-existing projet")
    void testAssignProjetDetailleToProjetNotFound() {
        when(projetDetailleRepository.findById(1L)).thenReturn(Optional.of(projetDetaille));
        when(projetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetDetailleService.assignProjetDetailleToProjet(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
