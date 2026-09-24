package tn.esprit.backend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.exception.ResourceNotFoundException;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.repository.EquipeRepository;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.IEquipeService;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EquipeServiceImpl implements IEquipeService {

    private final EquipeRepository equipeRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final ProjetRepository projetRepository;

    @Override
    @Transactional
    public Equipe addEquipe(Equipe equipe) {
        return equipeRepository.save(equipe);
    }

    @Override
    @Transactional
    public Equipe updateEquipe(Equipe equipe) {
        return equipeRepository.save(equipe);
    }

    @Override
    @Transactional
    public void deleteEquipe(Long id) {
        equipeRepository.deleteById(id);
    }

    @Override
    public Equipe getEquipeById(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe", id));
    }

    @Override
    public List<Equipe> getAllEquipes() {
        return equipeRepository.findAll();
    }

    @Override
    public Page<Equipe> getAllEquipes(Pageable pageable) {
        return equipeRepository.findAll(pageable);
    }

    @Override
    public List<Equipe> getEquipesByEntreprise(Long entrepriseId) {
        return equipeRepository.findByEntrepriseId(entrepriseId);
    }

    @Override
    @Transactional
    public Equipe assignEquipeToEntreprise(Long equipeId, Long entrepriseId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe", equipeId));
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise", entrepriseId));
        equipe.setEntreprise(entreprise);
        return equipeRepository.save(equipe);
    }

    @Override
    @Transactional
    public Equipe assignEquipeToProjet(Long equipeId, Long projetId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe", equipeId));
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet", projetId));
        equipe.getProjets().add(projet);
        return equipeRepository.save(equipe);
    }
}
