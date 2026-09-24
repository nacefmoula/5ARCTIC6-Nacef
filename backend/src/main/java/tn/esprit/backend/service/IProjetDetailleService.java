package tn.esprit.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit.backend.entity.ProjetDetaille;

import java.util.List;

public interface IProjetDetailleService {
    ProjetDetaille addProjetDetaille(ProjetDetaille projetDetaille);
    ProjetDetaille updateProjetDetaille(ProjetDetaille projetDetaille);
    void deleteProjetDetaille(Long id);
    ProjetDetaille getProjetDetailleById(Long id);
    List<ProjetDetaille> getAllProjetsDetailles();
    Page<ProjetDetaille> getAllProjetsDetailles(Pageable pageable);
    List<ProjetDetaille> getProjetDetaillesByProjet(Long projetId);
    ProjetDetaille assignProjetDetailleToProjet(Long projetDetailleId, Long projetId);
}
