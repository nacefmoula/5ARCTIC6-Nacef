package tn.esprit.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
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
import tn.esprit.backend.dto.ProjetDetailleRequestDTO;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.entity.ProjetDetaille;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.service.IProjetDetailleService;

import java.time.LocalDate;
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

@WebMvcTest(ProjetDetailleController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
class ProjetDetailleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private IProjetDetailleService projetDetailleService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /projet-detaille/add should succeed with 201 Created when valid")
    void testAddProjetDetailleValid() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 24);
        ProjetDetailleRequestDTO request = new ProjetDetailleRequestDTO(
                null, "Specs", "K8s", 5000.0, date, null, null
        );
        ProjetDetaille created = ProjetDetaille.builder()
                .id(1L)
                .description("Specs")
                .technologie("K8s")
                .coutProvisoire(5000.0)
                .dateDebut(date)
                .build();

        when(projetDetailleService.addProjetDetaille(any(ProjetDetaille.class))).thenReturn(created);

        mockMvc.perform(post("/projet-detaille/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Specs"))
                .andExpect(jsonPath("$.technologie").value("K8s"));
    }

    @Test
    @DisplayName("POST /projet-detaille/add should return 400 when validation fails")
    void testAddProjetDetailleValidationFailure() throws Exception {
        ProjetDetailleRequestDTO invalid = new ProjetDetailleRequestDTO(
                null, "", "", -100.0, null, null, null
        );

        mockMvc.perform(post("/projet-detaille/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erreur de Validation"))
                .andExpect(jsonPath("$.invalidParams.description").exists())
                .andExpect(jsonPath("$.invalidParams.coutProvisoire").exists());
    }

    @Test
    @DisplayName("PUT /projet-detaille/update should return 200 OK")
    void testUpdateProjetDetaille() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 24);
        ProjetDetailleRequestDTO request = new ProjetDetailleRequestDTO(
                1L, "Updated Specs", "K8s", 6000.0, date, null, null
        );
        ProjetDetaille updated = ProjetDetaille.builder()
                .id(1L)
                .description("Updated Specs")
                .technologie("K8s")
                .coutProvisoire(6000.0)
                .dateDebut(date)
                .build();

        when(projetDetailleService.updateProjetDetaille(any(ProjetDetaille.class))).thenReturn(updated);

        mockMvc.perform(put("/projet-detaille/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Specs"));
    }

    @Test
    @DisplayName("DELETE /projet-detaille/delete/{id} should return 204 No Content")
    void testDeleteProjetDetaille() throws Exception {
        doNothing().when(projetDetailleService).deleteProjetDetaille(1L);

        mockMvc.perform(delete("/projet-detaille/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /projet-detaille/get/{id} should return 200 OK when found")
    void testGetProjetDetailleByIdFound() throws Exception {
        ProjetDetaille pd = ProjetDetaille.builder().id(1L).description("Specs").technologie("K8s").build();
        when(projetDetailleService.getProjetDetailleById(1L)).thenReturn(pd);

        mockMvc.perform(get("/projet-detaille/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /projet-detaille/get/{id} should return 404 ProblemDetail when not found")
    void testGetProjetDetailleByIdNotFound() throws Exception {
        when(projetDetailleService.getProjetDetailleById(99L)).thenThrow(new ResourceNotFoundException("ProjetDetaille", 99L));

        mockMvc.perform(get("/projet-detaille/get/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Ressource Introuvable"));
    }

    @Test
    @DisplayName("GET /projet-detaille/all should return list")
    void testGetAllProjetsDetailles() throws Exception {
        ProjetDetaille pd = ProjetDetaille.builder().id(1L).description("Specs").technologie("K8s").build();
        when(projetDetailleService.getAllProjetsDetailles()).thenReturn(List.of(pd));

        mockMvc.perform(get("/projet-detaille/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /projet-detaille/page should return page")
    void testGetAllProjetsDetaillesPaged() throws Exception {
        ProjetDetaille pd = ProjetDetaille.builder().id(1L).description("Specs").technologie("K8s").build();
        Page<ProjetDetaille> page = new PageImpl<>(List.of(pd));
        when(projetDetailleService.getAllProjetsDetailles(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/projet-detaille/page?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("GET /projet-detaille/by-projet/{id} should return details by project")
    void testGetProjetDetaillesByProjet() throws Exception {
        ProjetDetaille pd = ProjetDetaille.builder().id(1L).description("Specs").technologie("K8s").build();
        when(projetDetailleService.getProjetDetaillesByProjet(1L)).thenReturn(List.of(pd));

        mockMvc.perform(get("/projet-detaille/by-projet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /projet-detaille/assign-projet/{pdId}/{pId} should assign and return 200")
    void testAssignProjetDetailleToProjet() throws Exception {
        Projet p = Projet.builder().id(2L).sujet("Main").build();
        ProjetDetaille pd = ProjetDetaille.builder().id(1L).description("Specs").projet(p).build();
        when(projetDetailleService.assignProjetDetailleToProjet(1L, 2L)).thenReturn(pd);

        mockMvc.perform(put("/projet-detaille/assign-projet/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projet.id").value(2));
    }
}
