package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor // lambok
public class BasicAgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository; // affecte mara w ma3dech nbadlo na3mlo f construct

    @Override
    public boolean check(Agence agence, boolean isNew) {
        return (!isNew || agence.getIdAgence() == null)
                && Utils.isNotEmpty(agence.getTelephone());
    }

    @Override
    public Agence create(Agence agence) {
        if (agence.getIdAgence() != null) {
            throw new IllegalArgumentException("L'identifiant de l'agence doit être nul lors de la création");
        }
        if (!check(agence, true)) {
            throw new IllegalArgumentException("Les données de l'agence sont invalides");
        }
        return agenceRepository.save(agence);
    }

    @Override
    public Agence findById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", String.valueOf(id)));
    }

    @Override
    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    @Override
    public Agence update(Long id, Agence agence) {
        Agence existingAgence = findById(id);

        if (!check(agence, false)) {
            throw new IllegalArgumentException("Les données de l'agence sont invalides");
        }

        existingAgence.setNom(agence.getNom());
        existingAgence.setVille(agence.getVille());
        existingAgence.setAdresse(agence.getAdresse());
        existingAgence.setTelephone(agence.getTelephone());

        return agenceRepository.save(existingAgence);
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", String.valueOf(id));
        }
        agenceRepository.deleteById(id);
    }

    @Override
    public boolean hasVehicules(Long id) {
        Agence agence = findById(id);
        return agence.getVehicules() != null && !agence.getVehicules().isEmpty();
    }
}
