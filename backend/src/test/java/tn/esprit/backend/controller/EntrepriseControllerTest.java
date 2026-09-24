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
import tn.esprit.backend.dto.EntrepriseRequestDTO;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.service.IEntrepriseService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EntrepriseController.class)
class EntrepriseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IEntrepriseService entrepriseService;

    @Test
    @DisplayName("POST /entreprise/add should succeed with 201 Created when data is valid")
    void testAddEntrepriseValid() throws Exception {
        EntrepriseRequestDTO request = new EntrepriseRequestDTO(null, "Orange", "Tunis");
        Entreprise created = Entreprise.builder().id(1L).nom("Orange").adresse("Tunis").build();

        when(entrepriseService.addEntreprise(any(Entreprise.class))).thenReturn(created);

        mockMvc.perform(post("/entreprise/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Orange"))
                .andExpect(jsonPath("$.adresse").value("Tunis"));
    }

    @Test
    @DisplayName("POST /entreprise/add should fail with 400 Bad Request when nom is blank (RFC 7807)")
    void testAddEntrepriseValidationFailure() throws Exception {
        EntrepriseRequestDTO invalidRequest = new EntrepriseRequestDTO(null, "", "");

        mockMvc.perform(post("/entreprise/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erreur de Validation"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.invalidParams.nom").exists())
                .andExpect(jsonPath("$.invalidParams.adresse").exists());
    }

    @Test
    @DisplayName("PUT /entreprise/update should succeed with 200 OK")
    void testUpdateEntreprise() throws Exception {
        EntrepriseRequestDTO request = new EntrepriseRequestDTO(1L, "Orange Updated", "Tunis Nord");
        Entreprise updated = Entreprise.builder().id(1L).nom("Orange Updated").adresse("Tunis Nord").build();

        when(entrepriseService.updateEntreprise(any(Entreprise.class))).thenReturn(updated);

        mockMvc.perform(put("/entreprise/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Orange Updated"));
    }

    @Test
    @DisplayName("DELETE /entreprise/delete/{id} should return 204 No Content")
    void testDeleteEntreprise() throws Exception {
        doNothing().when(entrepriseService).deleteEntreprise(1L);

        mockMvc.perform(delete("/entreprise/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /entreprise/get/{id} should return 200 OK when found")
    void testGetEntrepriseByIdFound() throws Exception {
        Entreprise e = Entreprise.builder().id(1L).nom("Orange").adresse("Tunis").build();
        when(entrepriseService.getEntrepriseById(1L)).thenReturn(e);

        mockMvc.perform(get("/entreprise/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Orange"));
    }

    @Test
    @DisplayName("GET /entreprise/get/{id} should return 404 ProblemDetail when not found")
    void testGetEntrepriseNotFound() throws Exception {
        when(entrepriseService.getEntrepriseById(999L))
                .thenThrow(new ResourceNotFoundException("Entreprise", 999L));

        mockMvc.perform(get("/entreprise/get/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Ressource Introuvable"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /entreprise/all should return list of DTOs")
    void testGetAllEntreprises() throws Exception {
        Entreprise e1 = Entreprise.builder().id(1L).nom("Entreprise 1").adresse("Adresse 1").build();
        when(entrepriseService.getAllEntreprises()).thenReturn(List.of(e1));

        mockMvc.perform(get("/entreprise/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nom").value("Entreprise 1"));
    }

    @Test
    @DisplayName("GET /entreprise/page should return page of DTOs")
    void testGetAllEntreprisesPaged() throws Exception {
        Entreprise e1 = Entreprise.builder().id(1L).nom("Entreprise 1").adresse("Adresse 1").build();
        Page<Entreprise> page = new PageImpl<>(List.of(e1));
        when(entrepriseService.getAllEntreprises(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/entreprise/page?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}
