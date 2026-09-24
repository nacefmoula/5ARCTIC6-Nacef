package tn.esprit.backend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.IProjetService;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProjetServiceImpl implements IProjetService {

    private final ProjetRepository projetRepository;

    @Override
    @Transactional
    public Projet addProjet(Projet projet) {
        return projetRepository.save(projet);
    }

    @Override
    @Transactional
    public Projet updateProjet(Projet projet) {
        return projetRepository.save(projet);
    }

    @Override
    @Transactional
    public void deleteProjet(Long id) {
        projetRepository.deleteById(id);
    }

    @Override
    public Projet getProjetById(Long id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet", id));
    }

    @Override
    public List<Projet> getAllProjets() {
        return projetRepository.findAll();
    }

    @Override
    public Page<Projet> getAllProjets(Pageable pageable) {
        return projetRepository.findAll(pageable);
    }
}
