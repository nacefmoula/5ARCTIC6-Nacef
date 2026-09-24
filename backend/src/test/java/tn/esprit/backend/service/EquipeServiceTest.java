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
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.repository.EquipeRepository;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.impl.EquipeServiceImpl;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    private Equipe equipe;
    private Entreprise entreprise;
    private Projet projet;

    @BeforeEach
    void setUp() {
        entreprise = Entreprise.builder().id(1L).nom("ESPRIT").adresse("Ariana").build();
        projet = Projet.builder().id(1L).sujet("DevOps Project").build();
        equipe = Equipe.builder()
                .id(1L)
                .nom("DevOps Squad")
                .specialite("Cloud & K8s")
                .projets(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should successfully add an equipe")
    void testAddEquipe() {
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe saved = equipeService.addEquipe(equipe);

        assertThat(saved).isNotNull();
        assertThat(saved.getNom()).isEqualTo("DevOps Squad");
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    @DisplayName("Should successfully update an equipe")
    void testUpdateEquipe() {
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe updated = equipeService.updateEquipe(equipe);

        assertThat(updated).isNotNull();
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    @DisplayName("Should delete an equipe by ID")
    void testDeleteEquipe() {
        doNothing().when(equipeRepository).deleteById(1L);

        equipeService.deleteEquipe(1L);

        verify(equipeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should find equipe by ID")
    void testGetEquipeByIdFound() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));

        Equipe found = equipeService.getEquipeById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(equipeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when equipe ID is not found")
    void testGetEquipeByIdNotFound() {
        when(equipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipeService.getEquipeById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should return all equipes")
    void testGetAllEquipes() {
        when(equipeRepository.findAll()).thenReturn(List.of(equipe));

        List<Equipe> list = equipeService.getAllEquipes();

        assertThat(list).hasSize(1);
        verify(equipeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return paged equipes")
    void testGetAllEquipesPaged() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Equipe> paged = new PageImpl<>(List.of(equipe), pageable, 1);
        when(equipeRepository.findAll(pageable)).thenReturn(paged);

        Page<Equipe> result = equipeService.getAllEquipes(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(equipeRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return equipes by entreprise ID")
    void testGetEquipesByEntreprise() {
        when(equipeRepository.findByEntrepriseId(1L)).thenReturn(List.of(equipe));

        List<Equipe> list = equipeService.getEquipesByEntreprise(1L);

        assertThat(list).hasSize(1);
        verify(equipeRepository, times(1)).findByEntrepriseId(1L);
    }

    @Test
    @DisplayName("Should assign equipe to entreprise successfully")
    void testAssignEquipeToEntrepriseSuccess() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(entrepriseRepository.findById(1L)).thenReturn(Optional.of(entreprise));
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe result = equipeService.assignEquipeToEntreprise(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(equipe.getEntreprise()).isEqualTo(entreprise);
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning non-existing equipe to entreprise")
    void testAssignEquipeToEntrepriseEquipeNotFound() {
        when(equipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipeService.assignEquipeToEntreprise(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning equipe to non-existing entreprise")
    void testAssignEquipeToEntrepriseEntrepriseNotFound() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(entrepriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipeService.assignEquipeToEntreprise(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should assign equipe to projet successfully")
    void testAssignEquipeToProjetSuccess() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe result = equipeService.assignEquipeToProjet(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(equipe.getProjets()).contains(projet);
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning non-existing equipe to projet")
    void testAssignEquipeToProjetEquipeNotFound() {
        when(equipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipeService.assignEquipeToProjet(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning equipe to non-existing projet")
    void testAssignEquipeToProjetProjetNotFound() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(projetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipeService.assignEquipeToProjet(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
