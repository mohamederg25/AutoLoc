package tn.esprit.autoloc;


import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tn.esprit.autoloc.domain.Agence;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;
// partie 1
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class  AgenceTests {
    @Autowired
    private AgenceRepositoryMock agenceRepsitory;
    @Test
    public void addAgence()
    {

        Agence agence = new Agence();
        agence.setNom("Agence Centrale");
        agence.setVille("Tunis");
        agence.setAdresse("10, rue de la Republique");
        agence.setTelephone("+216 71 000 000");

        Vehicule vehicule1 = new Vehicule();
        vehicule1.setImmatriculation("123 Tunis 1");
        vehicule1.setMarque("Peugeot");
        vehicule1.setModele("208");
        vehicule1.setCategorie(CategorieVehicule.CITADINE);
        vehicule1.setTarifJournalier(new BigDecimal("45.00"));
        vehicule1.setStatut(StatutVehicule.DISPONIBLE);
        vehicule1.setAgence(agence);
        agence.getVehicules().add(vehicule1);

        Vehicule vehicule2 = new Vehicule();
        vehicule2.setImmatriculation("456 Tunis 2");
        vehicule2.setMarque("Renault");
        vehicule2.setModele("Clio");
        vehicule2.setCategorie(CategorieVehicule.CITADINE);
        vehicule2.setTarifJournalier(new BigDecimal("40.00"));
        vehicule2.setStatut(StatutVehicule.DISPONIBLE);
        vehicule2.setAgence(agence);
        agence.getVehicules().add(vehicule2);

        agenceRepsitory.save(agence);

        Assertions.assertNotNull(agence.getIdAgence());
        Assertions.assertEquals(2, agence.getVehicules().size());


    };


    @org.junit.jupiter.api.Test
    public void loadAgence() {
        StringBuilder resultat = new StringBuilder();
        Iterable<Agence> agences = agenceRepsitory.findAll(); /// el find hnee

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
};

interface AgenceRepositoryMock extends CrudRepository<Agence,Integer>{}
