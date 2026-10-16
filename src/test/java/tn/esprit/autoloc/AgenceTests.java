package tn.esprit.autoloc;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tn.esprit.autoloc.domain.Agence;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.math.BigDecimal;
// partie 1
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class  AgenceTests {
    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;

    @Autowired
    private IAgenceRepository fullAgenceRepository;// questiopn 3

    private static final AtomicInteger COMPTEUR_IMMATRICULATION =
            new AtomicInteger(0);// question 5

    // affiche le type de repository utilise par chaque test (basic vs full)
    private static String typeDeRepository(CrudRepository<Agence, Long> repository) {
        for (Class<?> type : repository.getClass().getInterfaces()) {
            if (Repository.class.isAssignableFrom(type)) {
                String base = JpaRepository.class.isAssignableFrom(type)
                        ? "JpaRepository (full)"
                        : "CrudRepository (basic)";
                return type.getSimpleName() + " -> " + base;
            }
        }
        return repository.getClass().getSimpleName();
    }

    private void addAgence(CrudRepository<Agence, Long> repository)// question4 //question 6
    {
        System.out.println("Repository utilise : " + typeDeRepository(repository));
        int horodatage = (int) (System.currentTimeMillis() & 0x7FFFFFFF); //question 5
        String suffixe = Integer.toString(horodatage, 36) + COMPTEUR_IMMATRICULATION.getAndIncrement();

        Agence agence = new Agence();
       agence.setNom("Agence Centrale");
       agence.setVille("Tunis");
        agence.setAdresse("10, rue de la Republique");
       agence.setTelephone("+216 71 000 000");

      Vehicule vehicule1 = new Vehicule();
       vehicule1.setImmatriculation("123 Tunis 1"+suffixe);
       vehicule1.setMarque("Peugeot");
       vehicule1.setModele("208");
       vehicule1.setCategorie(CategorieVehicule.CITADINE);
       vehicule1.setTarifJournalier(new BigDecimal("45.00"));
       vehicule1.setStatut(StatutVehicule.DISPONIBLE);
       vehicule1.setAgence(agence);
       agence.getVehicules().add(vehicule1);

       Vehicule vehicule2 = new Vehicule();
       vehicule2.setImmatriculation("456 Tunis 2"+suffixe);
       vehicule2.setMarque("Renault");vehicule2.setModele("Clio");
     vehicule2.setCategorie(CategorieVehicule.CITADINE);
       vehicule2.setTarifJournalier(new BigDecimal("40.00"));
       vehicule2.setStatut(StatutVehicule.DISPONIBLE);
        vehicule2.setAgence(agence);
        agence.getVehicules().add(vehicule2);

       repository.save(agence); // question4

       Assertions.assertNotNull(agence.getIdAgence());
       Assertions.assertEquals(2, agence.getVehicules().size());


    };


    /// ////
    @org.junit.jupiter.api.Test
    public void basicAddAgence() {
        addAgence(basicAgenceRepository);
    }


    @org.junit.jupiter.api.Test
    public void fullAddAgence() {
        addAgence(fullAgenceRepository);
    }
    /// ///////////
    public void loadAgence(CrudRepository<Agence, Long> repository) {
        StringBuilder resultat = new StringBuilder();
        resultat.append("Repository utilise : ").append(typeDeRepository(repository))
                .append(System.lineSeparator());
        Iterable<Agence> agences = repository.findAll(); /// el find hnee

        for (Agence agence : agences) {
            resultat.append("Agence id : ").append(agence.getIdAgence())
                    .append(" | Nom : ").append(agence.getNom())
                    .append(" | Nombre de vehicules : ").append(agence.getVehicules().size())
                    .append(System.lineSeparator());

            for (Vehicule vehicule : agence.getVehicules()) {
                resultat.append("    Vehicule id : ").append(vehicule.getIdVehicule())
                        .append(" | Immatriculation : ").append(vehicule.getImmatriculation())
                        .append(System.lineSeparator());
            }
        }


        Assertions.fail(resultat.toString()); // errror hnee
    }

    @Test
    public void basicLoadAgence() {
        loadAgence(basicAgenceRepository);
    }

    @Test
    public void fullLoadAgence() {
        loadAgence(fullAgenceRepository);
    }

    // partie 2 (questions 11 -> 14) : chargement des agences triees par id decroissant
    @Test
    public void loadSortedAgences() {
        // on garantit au moins une agence pour verifier le tri
        Agence aAjouter = new Agence();
        aAjouter.setNom("Agence Tri " + (System.currentTimeMillis() & 0x7FFFFFFF)
                + COMPTEUR_IMMATRICULATION.getAndIncrement());
        aAjouter.setVille("Tunis");
        aAjouter.setAdresse("10, rue de la Republique");
        aAjouter.setTelephone("+216 71 000 000");
        fullAgenceRepository.save(aAjouter);

        StringBuilder resultat = new StringBuilder();
        resultat.append("Repository utilise : ").append(typeDeRepository(fullAgenceRepository))
                .append(System.lineSeparator());

        // question 12 : tous les agences tries par id decroissant
        Iterable<Agence> agences =
                fullAgenceRepository.findAll(Sort.by(Sort.Direction.DESC, "idAgence"));

        Long precedent = null;
        int compteur = 0;
        for (Agence agence : agences) {
            // question 13 : informations sur l'agence, sans les vehicules
            resultat.append("Agence id : ").append(agence.getIdAgence())
                    .append(" | Nom : ").append(agence.getNom())
                    .append(" | Ville : ").append(agence.getVille())
                    .append(" | Adresse : ").append(agence.getAdresse())
                    .append(" | Telephone : ").append(agence.getTelephone())
                    .append(System.lineSeparator());

            // question 14 : verification de l'ordre decroissant des ids
            Assertions.assertTrue(precedent == null || precedent > agence.getIdAgence(),
                    "Les agences ne sont pas triees par id decroissant : "
                            + precedent + " puis " + agence.getIdAgence());
            precedent = agence.getIdAgence();
            compteur++;
        }

        System.out.println(resultat);

        // question 14 : verification du resultat
        Assertions.assertTrue(compteur > 0, "Aucune agence n'a ete chargee");
        System.out.println("Nombre total d'agences chargees : " + compteur);
    }

    // partie 2 (questions 15 -> 18) : chargement trie et paginé par lot de 2
    @Test
    public void loadPagedAgences() {
        // on garantit au moins une agence pour verifier la pagination
        Agence aAjouter = new Agence();
        aAjouter.setNom("Agence Page " + (System.currentTimeMillis() & 0x7FFFFFFF)
                + COMPTEUR_IMMATRICULATION.getAndIncrement());
        aAjouter.setVille("Tunis");
        aAjouter.setAdresse("10, rue de la Republique");
        aAjouter.setTelephone("+216 71 000 000");
        fullAgenceRepository.save(aAjouter);

        StringBuilder resultat = new StringBuilder();
        resultat.append("Repository utilise : ").append(typeDeRepository(fullAgenceRepository))
                .append(System.lineSeparator());

        // question 16 : tous les agences tries par id decroissant, pagines par lot de 2
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "idAgence"));
        Page<Agence> page = fullAgenceRepository.findAll(pageable);

        // question 17 : nombre total de pages, page en cours, informations des agences
        resultat.append("Nombre total de pages : ").append(page.getTotalPages())
                .append(System.lineSeparator());
        resultat.append("Page en cours : ").append(page.getNumber() + 1)
                .append(" (page 0-indexee : ").append(page.getNumber()).append(")")
                .append(" | Taille d'un lot : ").append(page.getSize())
                .append(System.lineSeparator());

        for (Agence agence : page) {
            // sans les details des vehicules
            resultat.append("Agence id : ").append(agence.getIdAgence())
                    .append(" | Nom : ").append(agence.getNom())
                    .append(" | Ville : ").append(agence.getVille())
                    .append(" | Adresse : ").append(agence.getAdresse())
                    .append(" | Telephone : ").append(agence.getTelephone())
                    .append(System.lineSeparator());
        }

        System.out.println(resultat);

        // question 18 : verification du resultat
        Assertions.assertEquals(2, page.getSize(), "Le lot doit contenir 2 agences");
        Assertions.assertTrue(page.getNumberOfElements() <= 2,
                "Une page ne peut pas contenir plus de 2 agences");
        Assertions.assertEquals(
                (page.getTotalElements() + page.getSize() - 1) / page.getSize(),
                (long) page.getTotalPages(),
                "Le nombre total de pages est incorrect");

        // la page courante contient bien les premiers ids de la liste triee
        List<Agence> triees = fullAgenceRepository.findAll(Sort.by(Sort.Direction.DESC, "idAgence"));
        Assertions.assertTrue(triees.size() >= page.getNumberOfElements(),
                "Le nombre d'agences chargees est incoherent");
        for (int i = 0; i < page.getNumberOfElements(); i++) {
            Assertions.assertEquals(triees.get(i).getIdAgence(), page.getContent().get(i).getIdAgence(),
                    "La page courante ne contient pas les agences attendues");
        }
        System.out.println("Nombre total d'agences : " + page.getTotalElements()
                + " | Agences de la page courante : " + page.getNumberOfElements());
    }

};

interface AgenceRepositoryMock extends CrudRepository<Agence,Long>{}
