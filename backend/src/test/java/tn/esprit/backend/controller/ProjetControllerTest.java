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
import tn.esprit.backend.dto.ProjetRequestDTO;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.service.IProjetService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import tn.esprit.backend.security.JwtUtils;
import tn.esprit.backend.security.UserDetailsServiceImpl;

@WebMvcTest(ProjetController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
class ProjetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IProjetService projetService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("POST /projet/add should succeed with 201 Created when data is valid")
    void testAddProjetValid() throws Exception {
        ProjetRequestDTO request = new ProjetRequestDTO(null, "Kubernetes Migration");
        Projet created = Projet.builder().id(1L).sujet("Kubernetes Migration").build();

        when(projetService.addProjet(any(Projet.class))).thenReturn(created);

        mockMvc.perform(post("/projet/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sujet").value("Kubernetes Migration"));
    }

    @Test
    @DisplayName("POST /projet/add should return 400 Bad Request when sujet is blank")
    void testAddProjetValidationFailure() throws Exception {
        ProjetRequestDTO invalid = new ProjetRequestDTO(null, "");

        mockMvc.perform(post("/projet/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erreur de Validation"))
                .andExpect(jsonPath("$.invalidParams.sujet").exists());
    }

    @Test
    @DisplayName("PUT /projet/update should update and return 200 OK")
    void testUpdateProjet() throws Exception {
        ProjetRequestDTO request = new ProjetRequestDTO(1L, "Updated Sujet");
        Projet updated = Projet.builder().id(1L).sujet("Updated Sujet").build();

        when(projetService.updateProjet(any(Projet.class))).thenReturn(updated);

        mockMvc.perform(put("/projet/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sujet").value("Updated Sujet"));
    }

    @Test
    @DisplayName("DELETE /projet/delete/{id} should return 204 No Content")
    void testDeleteProjet() throws Exception {
        doNothing().when(projetService).deleteProjet(1L);

        mockMvc.perform(delete("/projet/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /projet/get/{id} should return 200 OK when found")
    void testGetProjetByIdFound() throws Exception {
        Projet projet = Projet.builder().id(1L).sujet("DevOps").build();
        when(projetService.getProjetById(1L)).thenReturn(projet);

        mockMvc.perform(get("/projet/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sujet").value("DevOps"));
    }

    @Test
    @DisplayName("GET /projet/get/{id} should return 404 ProblemDetail when not found")
    void testGetProjetByIdNotFound() throws Exception {
        when(projetService.getProjetById(99L)).thenThrow(new ResourceNotFoundException("Projet", 99L));

        mockMvc.perform(get("/projet/get/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Ressource Introuvable"));
    }

    @Test
    @DisplayName("GET /projet/all should return list of ProjetResponseDTOs")
    void testGetAllProjets() throws Exception {
        Projet p = Projet.builder().id(1L).sujet("Projet 1").build();
        when(projetService.getAllProjets()).thenReturn(List.of(p));

        mockMvc.perform(get("/projet/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /projet/page should return page of ProjetResponseDTOs")
    void testGetAllProjetsPaged() throws Exception {
        Projet p = Projet.builder().id(1L).sujet("Projet 1").build();
        Page<Projet> page = new PageImpl<>(List.of(p));
        when(projetService.getAllProjets(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/projet/page?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}
