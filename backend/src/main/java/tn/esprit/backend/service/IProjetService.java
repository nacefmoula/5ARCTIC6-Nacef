package tn.esprit.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit.backend.entity.Projet;

import java.util.List;

public interface IProjetService {
    Projet addProjet(Projet projet);
    Projet updateProjet(Projet projet);
    void deleteProjet(Long id);
    Projet getProjetById(Long id);
    List<Projet> getAllProjets();
    Page<Projet> getAllProjets(Pageable pageable);
}
