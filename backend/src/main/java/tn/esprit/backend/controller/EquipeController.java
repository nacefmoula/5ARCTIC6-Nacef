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
import tn.esprit.backend.dto.EquipeRequestDTO;
import tn.esprit.backend.dto.EquipeResponseDTO;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.mapper.EntityMapper;
import tn.esprit.backend.service.IEquipeService;

import java.util.List;

@RestController
@RequestMapping("/equipe")
@AllArgsConstructor
@Tag(name = "Équipes", description = "Gestion des équipes de projet et de leurs affectations")
public class EquipeController {

    private final IEquipeService equipeService;

    @PostMapping("/add")
    @Operation(summary = "Ajouter une équipe", description = "Crée une nouvelle équipe avec validation des données.")
    @ApiResponse(responseCode = "201", description = "Équipe créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données d'équipe invalides")
    public ResponseEntity<EquipeResponseDTO> addEquipe(@Valid @RequestBody EquipeRequestDTO request) {
        Equipe saved = equipeService.addEquipe(EntityMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityMapper.toResponse(saved));
    }

    @PutMapping("/update")
    @Operation(summary = "Modifier une équipe", description = "Met à jour les informations d'une équipe.")
    @ApiResponse(responseCode = "200", description = "Équipe mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Équipe non trouvée")
    public ResponseEntity<EquipeResponseDTO> updateEquipe(@Valid @RequestBody EquipeRequestDTO request) {
        Equipe updated = equipeService.updateEquipe(EntityMapper.toEntity(request));
        return ResponseEntity.ok(EntityMapper.toResponse(updated));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Supprimer une équipe", description = "Supprime une équipe par son identifiant.")
    @ApiResponse(responseCode = "204", description = "Équipe supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Équipe non trouvée")
    public ResponseEntity<Void> deleteEquipe(@PathVariable Long id) {
        equipeService.deleteEquipe(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Consulter une équipe", description = "Récupère les détails d'une équipe.")
    @ApiResponse(responseCode = "200", description = "Équipe trouvée")
    @ApiResponse(responseCode = "404", description = "Équipe non trouvée")
    public ResponseEntity<EquipeResponseDTO> getEquipeById(@PathVariable Long id) {
        Equipe equipe = equipeService.getEquipeById(id);
        return ResponseEntity.ok(EntityMapper.toResponse(equipe));
    }

    @GetMapping("/all")
    @Operation(summary = "Lister toutes les équipes", description = "Retourne la liste de toutes les équipes.")
    @ApiResponse(responseCode = "200", description = "Liste des équipes")
    public ResponseEntity<List<EquipeResponseDTO>> getAllEquipes() {
        List<EquipeResponseDTO> list = equipeService.getAllEquipes()
                .stream()
                .map(EntityMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/page")
    @Operation(summary = "Lister les équipes avec pagination", description = "Permet de paginer et trier les équipes.")
    @ApiResponse(responseCode = "200", description = "Page d'équipes")
    public ResponseEntity<Page<EquipeResponseDTO>> getAllEquipesPaged(Pageable pageable) {
        Page<EquipeResponseDTO> page = equipeService.getAllEquipes(pageable)
                .map(EntityMapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/by-entreprise/{entrepriseId}")
    @Operation(summary = "Lister les équipes par entreprise", description = "Filtre les équipes appartenant à une entreprise donnée.")
    @ApiResponse(responseCode = "200", description = "Liste des équipes de l'entreprise")
    public ResponseEntity<List<EquipeResponseDTO>> getEquipesByEntreprise(@PathVariable Long entrepriseId) {
        List<EquipeResponseDTO> list = equipeService.getEquipesByEntreprise(entrepriseId)
                .stream()
                .map(EntityMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/assign-entreprise/{equipeId}/{entrepriseId}")
    @Operation(summary = "Affecter une équipe à une entreprise", description = "Lie une équipe existante à une entreprise.")
    @ApiResponse(responseCode = "200", description = "Affectation réussie")
    @ApiResponse(responseCode = "404", description = "Équipe ou entreprise introuvable")
    public ResponseEntity<EquipeResponseDTO> assignEquipeToEntreprise(@PathVariable Long equipeId, @PathVariable Long entrepriseId) {
        Equipe updated = equipeService.assignEquipeToEntreprise(equipeId, entrepriseId);
        return ResponseEntity.ok(EntityMapper.toResponse(updated));
    }

    @PutMapping("/assign-projet/{equipeId}/{projetId}")
    @Operation(summary = "Affecter une équipe à un projet", description = "Lie une équipe à un projet.")
    @ApiResponse(responseCode = "200", description = "Affectation réussie")
    @ApiResponse(responseCode = "404", description = "Équipe ou projet introuvable")
    public ResponseEntity<EquipeResponseDTO> assignEquipeToProjet(@PathVariable Long equipeId, @PathVariable Long projetId) {
        Equipe updated = equipeService.assignEquipeToProjet(equipeId, projetId);
        return ResponseEntity.ok(EntityMapper.toResponse(updated));
    }
}
