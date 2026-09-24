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
import tn.esprit.backend.dto.ProjetDetailleRequestDTO;
import tn.esprit.backend.dto.ProjetDetailleResponseDTO;
import tn.esprit.backend.entity.ProjetDetaille;
import tn.esprit.backend.mapper.EntityMapper;
import tn.esprit.backend.service.IProjetDetailleService;

import java.util.List;

@RestController
@RequestMapping("/projet-detaille")
@AllArgsConstructor
@Tag(name = "Projets Détaillés", description = "Gestion des spécifications techniques et budgétaires des projets")
public class ProjetDetailleController {

    private final IProjetDetailleService projetDetailleService;

    @PostMapping("/add")
    @Operation(summary = "Ajouter un projet détaillé", description = "Crée les détails techniques et financiers d'un projet.")
    @ApiResponse(responseCode = "201", description = "Détails de projet créés avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<ProjetDetailleResponseDTO> addProjetDetaille(@Valid @RequestBody ProjetDetailleRequestDTO request) {
        ProjetDetaille saved = projetDetailleService.addProjetDetaille(EntityMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityMapper.toResponse(saved));
    }

    @PutMapping("/update")
    @Operation(summary = "Modifier un projet détaillé", description = "Met à jour les spécifications techniques d'un projet.")
    @ApiResponse(responseCode = "200", description = "Détails de projet mis à jour")
    @ApiResponse(responseCode = "404", description = "Projet détaillé non trouvé")
    public ResponseEntity<ProjetDetailleResponseDTO> updateProjetDetaille(@Valid @RequestBody ProjetDetailleRequestDTO request) {
        ProjetDetaille updated = projetDetailleService.updateProjetDetaille(EntityMapper.toEntity(request));
        return ResponseEntity.ok(EntityMapper.toResponse(updated));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Supprimer un projet détaillé", description = "Supprime les détails d'un projet par son identifiant.")
    @ApiResponse(responseCode = "204", description = "Détails supprimés avec succès")
    @ApiResponse(responseCode = "404", description = "Projet détaillé non trouvé")
    public ResponseEntity<Void> deleteProjetDetaille(@PathVariable Long id) {
        projetDetailleService.deleteProjetDetaille(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Consulter un projet détaillé", description = "Récupère les détails d'un projet par son identifiant.")
    @ApiResponse(responseCode = "200", description = "Projet détaillé trouvé")
    @ApiResponse(responseCode = "404", description = "Projet détaillé non trouvé")
    public ResponseEntity<ProjetDetailleResponseDTO> getProjetDetailleById(@PathVariable Long id) {
        ProjetDetaille projetDetaille = projetDetailleService.getProjetDetailleById(id);
        return ResponseEntity.ok(EntityMapper.toResponse(projetDetaille));
    }

    @GetMapping("/all")
    @Operation(summary = "Lister tous les projets détaillés", description = "Retourne la liste complète des projets détaillés.")
    @ApiResponse(responseCode = "200", description = "Liste des projets détaillés")
    public ResponseEntity<List<ProjetDetailleResponseDTO>> getAllProjetsDetailles() {
        List<ProjetDetailleResponseDTO> list = projetDetailleService.getAllProjetsDetailles()
                .stream()
                .map(EntityMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/page")
    @Operation(summary = "Lister les projets détaillés avec pagination", description = "Permet de paginer et trier les projets détaillés.")
    @ApiResponse(responseCode = "200", description = "Page de projets détaillés")
    public ResponseEntity<Page<ProjetDetailleResponseDTO>> getAllProjetsDetaillesPaged(Pageable pageable) {
        Page<ProjetDetailleResponseDTO> page = projetDetailleService.getAllProjetsDetailles(pageable)
                .map(EntityMapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/by-projet/{projetId}")
    @Operation(summary = "Lister les détails par projet", description = "Filtre les détails associés à un projet spécifique.")
    @ApiResponse(responseCode = "200", description = "Détails associés au projet")
    public ResponseEntity<List<ProjetDetailleResponseDTO>> getProjetDetaillesByProjet(@PathVariable Long projetId) {
        List<ProjetDetailleResponseDTO> list = projetDetailleService.getProjetDetaillesByProjet(projetId)
                .stream()
                .map(EntityMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/assign-projet/{projetDetailleId}/{projetId}")
    @Operation(summary = "Associer à un projet", description = "Associe une fiche de détails à un projet existant.")
    @ApiResponse(responseCode = "200", description = "Association réussie")
    @ApiResponse(responseCode = "404", description = "Projet ou fiche détaillée introuvable")
    public ResponseEntity<ProjetDetailleResponseDTO> assignProjetDetailleToProjet(@PathVariable Long projetDetailleId, @PathVariable Long projetId) {
        ProjetDetaille updated = projetDetailleService.assignProjetDetailleToProjet(projetDetailleId, projetId);
        return ResponseEntity.ok(EntityMapper.toResponse(updated));
    }
}
