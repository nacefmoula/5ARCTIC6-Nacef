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
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.service.impl.EntrepriseServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrepriseServiceTest {

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @InjectMocks
    private EntrepriseServiceImpl entrepriseService;

    private Entreprise entreprise;

    @BeforeEach
    void setUp() {
        entreprise = Entreprise.builder()
                .id(1L)
                .nom("ESPRIT Tech")
                .adresse("Ariana, Tunis")
                .build();
    }

    @Test
    @DisplayName("Should successfully add an entreprise")
    void testAddEntreprise() {
        when(entrepriseRepository.save(any(Entreprise.class))).thenReturn(entreprise);

        Entreprise saved = entrepriseService.addEntreprise(entreprise);

        assertThat(saved).isNotNull();
        assertThat(saved.getNom()).isEqualTo("ESPRIT Tech");
        verify(entrepriseRepository, times(1)).save(entreprise);
    }

    @Test
    @DisplayName("Should successfully update an entreprise")
    void testUpdateEntreprise() {
        when(entrepriseRepository.save(any(Entreprise.class))).thenReturn(entreprise);

        Entreprise updated = entrepriseService.updateEntreprise(entreprise);

        assertThat(updated).isNotNull();
        verify(entrepriseRepository, times(1)).save(entreprise);
    }

    @Test
    @DisplayName("Should return all entreprises")
    void testGetAllEntreprises() {
        Entreprise e2 = Entreprise.builder().id(2L).nom("Cloud Corp").adresse("Tunis").build();
        when(entrepriseRepository.findAll()).thenReturn(Arrays.asList(entreprise, e2));

        List<Entreprise> list = entrepriseService.getAllEntreprises();

        assertThat(list).hasSize(2);
        verify(entrepriseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return paged entreprises")
    void testGetAllEntreprisesPaged() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Entreprise> page = new PageImpl<>(List.of(entreprise), pageable, 1);
        when(entrepriseRepository.findAll(pageable)).thenReturn(page);

        Page<Entreprise> result = entrepriseService.getAllEntreprises(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(entrepriseRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find entreprise by ID when exists")
    void testGetEntrepriseById() {
        when(entrepriseRepository.findById(1L)).thenReturn(Optional.of(entreprise));

        Entreprise found = entrepriseService.getEntrepriseById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(entrepriseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when entreprise ID does not exist")
    void testGetEntrepriseByIdNotFound() {
        when(entrepriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrepriseService.getEntrepriseById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should delete entreprise by ID")
    void testDeleteEntreprise() {
        doNothing().when(entrepriseRepository).deleteById(1L);

        entrepriseService.deleteEntreprise(1L);

        verify(entrepriseRepository, times(1)).deleteById(1L);
    }
}
