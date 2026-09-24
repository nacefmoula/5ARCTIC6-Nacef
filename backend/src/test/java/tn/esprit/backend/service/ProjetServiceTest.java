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
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.impl.ProjetServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetServiceTest {

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private ProjetServiceImpl projetService;

    private Projet projet;

    @BeforeEach
    void setUp() {
        projet = Projet.builder().id(1L).sujet("DevOps Transformation").build();
    }

    @Test
    @DisplayName("Should successfully add a projet")
    void testAddProjet() {
        when(projetRepository.save(any(Projet.class))).thenReturn(projet);

        Projet saved = projetService.addProjet(projet);

        assertThat(saved).isNotNull();
        assertThat(saved.getSujet()).isEqualTo("DevOps Transformation");
        verify(projetRepository, times(1)).save(projet);
    }

    @Test
    @DisplayName("Should successfully update a projet")
    void testUpdateProjet() {
        when(projetRepository.save(any(Projet.class))).thenReturn(projet);

        Projet updated = projetService.updateProjet(projet);

        assertThat(updated).isNotNull();
        verify(projetRepository, times(1)).save(projet);
    }

    @Test
    @DisplayName("Should delete a projet by ID")
    void testDeleteProjet() {
        doNothing().when(projetRepository).deleteById(1L);

        projetService.deleteProjet(1L);

        verify(projetRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should find projet by ID when found")
    void testGetProjetByIdFound() {
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));

        Projet found = projetService.getProjetById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(projetRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when projet ID does not exist")
    void testGetProjetByIdNotFound() {
        when(projetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projetService.getProjetById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should return all projets")
    void testGetAllProjets() {
        when(projetRepository.findAll()).thenReturn(List.of(projet));

        List<Projet> list = projetService.getAllProjets();

        assertThat(list).hasSize(1);
        verify(projetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return paged projets")
    void testGetAllProjetsPaged() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Projet> page = new PageImpl<>(List.of(projet), pageable, 1);
        when(projetRepository.findAll(pageable)).thenReturn(page);

        Page<Projet> result = projetService.getAllProjets(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(projetRepository, times(1)).findAll(pageable);
    }
}
