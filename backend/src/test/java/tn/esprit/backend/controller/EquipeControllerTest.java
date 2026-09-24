package tn.esprit.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.backend.dto.EquipeRequestDTO;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.service.IEquipeService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EquipeController.class)
class EquipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IEquipeService equipeService;

    @Test
    @DisplayName("POST /equipe/add should succeed with 201 Created when data is valid")
    void testAddEquipeValid() throws Exception {
        EquipeRequestDTO request = new EquipeRequestDTO(null, "DevOps Team", "Kubernetes", null, null);
        Equipe created = Equipe.builder().id(1L).nom("DevOps Team").specialite("Kubernetes").build();

        when(equipeService.addEquipe(any(Equipe.class))).thenReturn(created);

        mockMvc.perform(post("/equipe/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("DevOps Team"))
                .andExpect(jsonPath("$.specialite").value("Kubernetes"));
    }

    @Test
    @DisplayName("POST /equipe/add should return 400 Bad Request when validation fails")
    void testAddEquipeValidationFailure() throws Exception {
        EquipeRequestDTO invalidRequest = new EquipeRequestDTO(null, "", "", null, null);

        mockMvc.perform(post("/equipe/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erreur de Validation"))
                .andExpect(jsonPath("$.invalidParams.nom").exists())
                .andExpect(jsonPath("$.invalidParams.specialite").exists());
    }

    @Test
    @DisplayName("PUT /equipe/update should update and return 200 OK")
    void testUpdateEquipe() throws Exception {
        EquipeRequestDTO request = new EquipeRequestDTO(1L, "Updated Team", "Cloud", null, null);
        Equipe updated = Equipe.builder().id(1L).nom("Updated Team").specialite("Cloud").build();

        when(equipeService.updateEquipe(any(Equipe.class))).thenReturn(updated);

        mockMvc.perform(put("/equipe/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Updated Team"));
    }

    @Test
    @DisplayName("DELETE /equipe/delete/{id} should return 204 No Content")
    void testDeleteEquipe() throws Exception {
        doNothing().when(equipeService).deleteEquipe(1L);

        mockMvc.perform(delete("/equipe/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /equipe/get/{id} should return 200 OK when found")
    void testGetEquipeByIdFound() throws Exception {
        Equipe equipe = Equipe.builder().id(1L).nom("DevOps").specialite("K8s").build();
        when(equipeService.getEquipeById(1L)).thenReturn(equipe);

        mockMvc.perform(get("/equipe/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("DevOps"));
    }

    @Test
    @DisplayName("GET /equipe/get/{id} should return 404 ProblemDetail when not found")
    void testGetEquipeByIdNotFound() throws Exception {
        when(equipeService.getEquipeById(99L)).thenThrow(new ResourceNotFoundException("Equipe", 99L));

        mockMvc.perform(get("/equipe/get/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Ressource Introuvable"));
    }

    @Test
    @DisplayName("GET /equipe/all should return list of EquipeResponseDTOs")
    void testGetAllEquipes() throws Exception {
        Equipe eq = Equipe.builder().id(1L).nom("Team Alpha").specialite("Backend").build();
        when(equipeService.getAllEquipes()).thenReturn(List.of(eq));

        mockMvc.perform(get("/equipe/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /equipe/page should return page of EquipeResponseDTOs")
    void testGetAllEquipesPaged() throws Exception {
        Equipe eq = Equipe.builder().id(1L).nom("Team Alpha").specialite("Backend").build();
        Page<Equipe> page = new PageImpl<>(List.of(eq));
        when(equipeService.getAllEquipes(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/equipe/page?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("GET /equipe/by-entreprise/{id} should return list for entreprise")
    void testGetEquipesByEntreprise() throws Exception {
        Equipe eq = Equipe.builder().id(1L).nom("Team Alpha").specialite("Backend").build();
        when(equipeService.getEquipesByEntreprise(1L)).thenReturn(List.of(eq));

        mockMvc.perform(get("/equipe/by-entreprise/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /equipe/assign-entreprise/{equipeId}/{entrepriseId} should assign and return 200")
    void testAssignEquipeToEntreprise() throws Exception {
        Entreprise ent = Entreprise.builder().id(2L).nom("ESPRIT").adresse("Tunis").build();
        Equipe eq = Equipe.builder().id(1L).nom("Team Alpha").specialite("Backend").entreprise(ent).build();
        when(equipeService.assignEquipeToEntreprise(1L, 2L)).thenReturn(eq);

        mockMvc.perform(put("/equipe/assign-entreprise/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entreprise.id").value(2));
    }

    @Test
    @DisplayName("PUT /equipe/assign-projet/{equipeId}/{projetId} should assign and return 200")
    void testAssignEquipeToProjet() throws Exception {
        Equipe eq = Equipe.builder().id(1L).nom("Team Alpha").specialite("Backend").build();
        when(equipeService.assignEquipeToProjet(1L, 3L)).thenReturn(eq);

        mockMvc.perform(put("/equipe/assign-projet/1/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
