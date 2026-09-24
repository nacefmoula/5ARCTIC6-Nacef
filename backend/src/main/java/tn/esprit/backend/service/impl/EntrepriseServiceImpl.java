package tn.esprit.backend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.service.IEntrepriseService;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EntrepriseServiceImpl implements IEntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    @Override
    @Transactional
    public Entreprise addEntreprise(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    @Override
    @Transactional
    public Entreprise updateEntreprise(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    @Override
    @Transactional
    public void deleteEntreprise(Long id) {
        entrepriseRepository.deleteById(id);
    }

    @Override
    public Entreprise getEntrepriseById(Long id) {
        return entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise", id));
    }

    @Override
    public List<Entreprise> getAllEntreprises() {
        return entrepriseRepository.findAll();
    }

    @Override
    public Page<Entreprise> getAllEntreprises(Pageable pageable) {
        return entrepriseRepository.findAll(pageable);
    }
}
