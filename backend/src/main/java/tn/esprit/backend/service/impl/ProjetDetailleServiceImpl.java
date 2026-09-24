package tn.esprit.backend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.entity.ProjetDetaille;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.repository.ProjetDetailleRepository;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.IProjetDetailleService;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProjetDetailleServiceImpl implements IProjetDetailleService {

    private final ProjetDetailleRepository projetDetailleRepository;
    private final ProjetRepository projetRepository;

    @Override
    @Transactional
    public ProjetDetaille addProjetDetaille(ProjetDetaille projetDetaille) {
        return projetDetailleRepository.save(projetDetaille);
    }

    @Override
    @Transactional
    public ProjetDetaille updateProjetDetaille(ProjetDetaille projetDetaille) {
        return projetDetailleRepository.save(projetDetaille);
    }

    @Override
    @Transactional
    public void deleteProjetDetaille(Long id) {
        projetDetailleRepository.deleteById(id);
    }

    @Override
    public ProjetDetaille getProjetDetailleById(Long id) {
        return projetDetailleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProjetDetaille", id));
    }

    @Override
    public List<ProjetDetaille> getAllProjetsDetailles() {
        return projetDetailleRepository.findAll();
    }

    @Override
    public Page<ProjetDetaille> getAllProjetsDetailles(Pageable pageable) {
        return projetDetailleRepository.findAll(pageable);
    }

    @Override
    public List<ProjetDetaille> getProjetDetaillesByProjet(Long projetId) {
        return projetDetailleRepository.findByProjetId(projetId);
    }

    @Override
    @Transactional
    public ProjetDetaille assignProjetDetailleToProjet(Long projetDetailleId, Long projetId) {
        ProjetDetaille projetDetaille = projetDetailleRepository.findById(projetDetailleId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjetDetaille", projetDetailleId));
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet", projetId));
        projetDetaille.setProjet(projet);
        return projetDetailleRepository.save(projetDetaille);
    }
}
