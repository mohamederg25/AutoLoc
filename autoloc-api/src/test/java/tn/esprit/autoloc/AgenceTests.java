package tn.esprit.autoloc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.service.IAgenceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class AgenceTests {

    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;

    @Autowired
    private IAgenceRepository fullAgenceRepository;

    @Autowired
    private IAgenceService agenceService;

    @Test
    public void basicAddAgence() {
        addAgence(basicAgenceRepository);
    }

    @Test
    public void fullAddAgence() {
        addAgence(fullAgenceRepository);
    }

    @Test
    public void basicLoadAgence() {
        loadAgence(basicAgenceRepository, "basic (CrudRepository)");
    }

    @Test
    public void fullLoadAgence() {
        loadAgence(fullAgenceRepository, "full (JpaRepository)");
    }

    @Test
    public void createAgence() {
        Agence agence = new Agence();
        agence.setNom("Agence ariana");
        agence.setVille("Tunis");
        agence.setAdresse("1 Rue Hedi");
        agence.setTelephone("71585874");

        Agence createdAgence = agenceService.create(agence);

        Assertions.assertNotNull(createdAgence);
        Assertions.assertNotNull(createdAgence.getIdAgence());
        Assertions.assertEquals(agence.getNom(), createdAgence.getNom());
        Assertions.assertEquals(agence.getVille(), createdAgence.getVille());
    }

    @Test
    public void loadSortedAgences() {
        Sort sort = Sort.by("idAgence").descending();
        List<Agence> agences = fullAgenceRepository.findAll(sort);

        StringBuilder sb = new StringBuilder();
        for (Agence agence : agences) {
            sb.append("\n").append(agence.getIdAgence()).append(" | ").append(agence.getNom())
                    .append(" | ").append(agence.getVille())
                    .append(" | ").append(agence.getAdresse())
                    .append(" | ").append(agence.getTelephone());
        }

        Assertions.fail(sb.toString());
    }

    @Test
    public void loadPagedAgences() {
        Sort sort = Sort.by("idAgence").descending();
        Pageable pageable = PageRequest.of(0, 2, sort);
        Page<Agence> page;

        StringBuilder sb = new StringBuilder();
        do {
            page = fullAgenceRepository.findAll(pageable);

            sb.append("\nTotal pages : ").append(page.getTotalPages());
            sb.append("\nPage en cours : ").append(page.getNumber());
            for (Agence agence : page.getContent()) {
                sb.append("\n").append(agence.getIdAgence()).append(" | ").append(agence.getNom())
                        .append(" | ").append(agence.getVille())
                        .append(" | ").append(agence.getAdresse())
                        .append(" | ").append(agence.getTelephone());
            }

            pageable = pageable.next();
        } while (page.hasNext());

        Assertions.fail(sb.toString());
    }

    private void addAgence(CrudRepository<Agence, Long> repository) {
        int stamp = (int) System.currentTimeMillis();

        Agence agence = new Agence();
        agence.setNom("Agence ariana");
        agence.setVille("Tunis");
        agence.setAdresse("1 Rue Hedi");
        agence.setTelephone("71585874");

        Vehicule v1 = new Vehicule();
        v1.setImmatriculation("TU96-" + stamp);
        v1.setMarque("Isuzu");
        v1.setModele("DMax");
        v1.setCategorie(CategorieVehicule.SUV);
        v1.setStatut(StatutVehicule.MAINTENANCE);
        v1.setTarifJournalier(new BigDecimal("100"));
        v1.setAgence(agence);

        Vehicule v2 = new Vehicule();
        v2.setImmatriculation("TU95-" + stamp);
        v2.setMarque("Toyota");
        v2.setModele("Yaris");
        v2.setCategorie(CategorieVehicule.UTILITAIRE);
        v2.setStatut(StatutVehicule.DISPONIBLE);
        v2.setTarifJournalier(new BigDecimal("80"));
        v2.setAgence(agence);

        agence.setVehicules(Set.of(v1, v2));

        repository.save(agence);
    }

    private void loadAgence(CrudRepository<Agence, Long> repository, String repositoryType) {
        Iterable<Agence> agences = repository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("\nRepository : ").append(repositoryType);
        for (Agence agence : agences) {
            sb.append("\n").append(agence.getIdAgence()).append(" | ").append(agence.getNom());
            sb.append("\nVehicules Count : ").append(agence.getVehicules().size());
            for (Vehicule vehicule : agence.getVehicules()) {
                sb.append("\n=== ").append(vehicule.getIdVehicule()).append("|").append(vehicule.getImmatriculation());
            }
        }

        Assertions.fail(sb.toString());
    }
}

@Repository
interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}
