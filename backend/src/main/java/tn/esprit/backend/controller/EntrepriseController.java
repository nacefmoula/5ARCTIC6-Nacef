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
import tn.esprit.backend.dto.EntrepriseRequestDTO;
import tn.esprit.backend.dto.EntrepriseResponseDTO;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.mapper.EntityMapper;
import tn.esprit.backend.service.IEntrepriseService;

import java.util.List;

@RestController
@RequestMapping("/entreprise")
@AllArgsConstructor
@Tag(name = "Entreprises", description = "Gestion des entreprises partenaires et clientes")
public class EntrepriseController {

    private final IEntrepriseService entrepriseService;

    @PostMapping("/add")
    @Operation(summary = "Ajouter une entreprise", description = "Crée une nouvelle entreprise avec validation des données.")
    @ApiResponse(responseCode = "201", description = "Entreprise créée avec succès")
    @ApiResponse(responseCode = "400", description = "Erreur de validation des champs obligatoires")
    public ResponseEntity<EntrepriseResponseDTO> addEntreprise(@Valid @RequestBody EntrepriseRequestDTO request) {
        Entreprise saved = entrepriseService.addEntreprise(EntityMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityMapper.toResponse(saved));
    }

    @PutMapping("/update")
    @Operation(summary = "Mettre à jour une entreprise", description = "Met à jour les informations d'une entreprise existante.")
    @ApiResponse(responseCode = "200", description = "Entreprise mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    public ResponseEntity<EntrepriseResponseDTO> updateEntreprise(@Valid @RequestBody EntrepriseRequestDTO request) {
        Entreprise updated = entrepriseService.updateEntreprise(EntityMapper.toEntity(request));
        return ResponseEntity.ok(EntityMapper.toResponse(updated));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Supprimer une entreprise", description = "Supprime une entreprise par son identifiant unique.")
    @ApiResponse(responseCode = "204", description = "Entreprise supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    public ResponseEntity<Void> deleteEntreprise(@PathVariable Long id) {
        entrepriseService.deleteEntreprise(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Consulter une entreprise", description = "Récupère les détails d'une entreprise par son identifiant.")
    @ApiResponse(responseCode = "200", description = "Entreprise trouvée")
    @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    public ResponseEntity<EntrepriseResponseDTO> getEntrepriseById(@PathVariable Long id) {
        Entreprise entreprise = entrepriseService.getEntrepriseById(id);
        return ResponseEntity.ok(EntityMapper.toResponse(entreprise));
    }

    @GetMapping("/all")
    @Operation(summary = "Lister toutes les entreprises", description = "Retourne la liste complète des entreprises.")
    @ApiResponse(responseCode = "200", description = "Liste des entreprises")
    public ResponseEntity<List<EntrepriseResponseDTO>> getAllEntreprises() {
        List<EntrepriseResponseDTO> list = entrepriseService.getAllEntreprises()
                .stream()
                .map(EntityMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/page")
    @Operation(summary = "Lister les entreprises avec pagination", description = "Permet de paginer et trier les entreprises (page, size, sort).")
    @ApiResponse(responseCode = "200", description = "Page d'entreprises")
    public ResponseEntity<Page<EntrepriseResponseDTO>> getAllEntreprisesPaged(Pageable pageable) {
        Page<EntrepriseResponseDTO> page = entrepriseService.getAllEntreprises(pageable)
                .map(EntityMapper::toResponse);
        return ResponseEntity.ok(page);
    }
}
