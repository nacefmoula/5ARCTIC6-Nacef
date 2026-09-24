package tn.esprit.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backend.dto.ProjetRequestDTO;
import tn.esprit.backend.dto.ProjetResponseDTO;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.mapper.EntityMapper;
import tn.esprit.backend.service.IProjetService;

import java.util.List;

@RestController
@RequestMapping("/projet")
@AllArgsConstructor
@CrossOrigin("*")
@Tag(name = "Projets", description = "Gestion des projets transverses de l'organisation")
public class ProjetController {

    private final IProjetService projetService;

    @PostMapping("/add")
    @Operation(summary = "Ajouter un projet", description = "Crée un nouveau projet.")
    @ApiResponse(responseCode = "201", description = "Projet créé avec succès")
    @ApiResponse(responseCode = "400", description = "Sujet de projet invalide")
    public ResponseEntity<ProjetResponseDTO> addProjet(@Valid @RequestBody ProjetRequestDTO request) {
        Projet saved = projetService.addProjet(EntityMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityMapper.toResponse(saved));
    }

    @PutMapping("/update")
    @Operation(summary = "Modifier un projet", description = "Met à jour un projet existant.")
    @ApiResponse(responseCode = "200", description = "Projet mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Projet introuvable")
    public ResponseEntity<ProjetResponseDTO> updateProjet(@Valid @RequestBody ProjetRequestDTO request) {
        Projet updated = projetService.updateProjet(EntityMapper.toEntity(request));
        return ResponseEntity.ok(EntityMapper.toResponse(updated));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Supprimer un projet", description = "Supprime un projet par son identifiant.")
    @ApiResponse(responseCode = "204", description = "Projet supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Projet introuvable")
    public ResponseEntity<Void> deleteProjet(@PathVariable Long id) {
        projetService.deleteProjet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Consulter un projet", description = "Récupère les informations d'un projet par son identifiant.")
    @ApiResponse(responseCode = "200", description = "Projet trouvé")
    @ApiResponse(responseCode = "404", description = "Projet introuvable")
    public ResponseEntity<ProjetResponseDTO> getProjetById(@PathVariable Long id) {
        Projet projet = projetService.getProjetById(id);
        return ResponseEntity.ok(EntityMapper.toResponse(projet));
    }

    @GetMapping("/all")
    @Operation(summary = "Lister tous les projets", description = "Retourne la liste complète des projets.")
    @ApiResponse(responseCode = "200", description = "Liste des projets")
    public ResponseEntity<List<ProjetResponseDTO>> getAllProjets() {
        List<ProjetResponseDTO> list = projetService.getAllProjets()
                .stream()
                .map(EntityMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/page")
    @Operation(summary = "Lister les projets avec pagination", description = "Permet de paginer et trier les projets.")
    @ApiResponse(responseCode = "200", description = "Page de projets")
    public ResponseEntity<Page<ProjetResponseDTO>> getAllProjetsPaged(Pageable pageable) {
        Page<ProjetResponseDTO> page = projetService.getAllProjets(pageable)
                .map(EntityMapper::toResponse);
        return ResponseEntity.ok(page);
    }
}
