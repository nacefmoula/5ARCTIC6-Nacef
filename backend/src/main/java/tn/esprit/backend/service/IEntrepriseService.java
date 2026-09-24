package tn.esprit.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit.backend.entity.Entreprise;

import java.util.List;

public interface IEntrepriseService {
    Entreprise addEntreprise(Entreprise entreprise);
    Entreprise updateEntreprise(Entreprise entreprise);
    void deleteEntreprise(Long id);
    Entreprise getEntrepriseById(Long id);
    List<Entreprise> getAllEntreprises();
    Page<Entreprise> getAllEntreprises(Pageable pageable);
}
