package tn.esprit.autoloc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// partie 3 (questions 16 -> 21) : on teste la creation des colonnes et des tables de jointure
@SpringBootTest
@Transactional
public class RelationsTests {

    @Autowired
    private AgenceRepositoryMock agenceRepository;
    @Autowired
    private EmployeRepositoryMock employeRepository;
    @Autowired
    private VehiculeRepositoryMock vehiculeRepository;
    @Autowired
    private ClientRepositoryMock clientRepository;
    @Autowired
    private ReservationRepositoryMock reservationRepository;
    @Autowired
    private ContratRepositoryMock contratRepository;
    @Autowired
    private PaiementRepositoryMock paiementRepository;
    @Autowired
    private MaintenanceRepositoryMock maintenanceRepository;
    @Autowired
    private EquipementRepositoryMock equipementRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    private String suffixe() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Agence nouvelleAgence() {
        Agence agence = new Agence();
        agence.setNom("Agence " + suffixe());
        agence.setVille("Tunis");
        agence.setAdresse("10, rue de la Republique");
        agence.setTelephone("+216 71 000 000");
        return agenceRepository.save(agence);
    }

    private Vehicule nouveauVehicule(Agence agence) {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("T" + suffixe());
        vehicule.setMarque("Peugeot");
        vehicule.setModele("208");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("45.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule.setAgence(agence);
        return vehiculeRepository.save(vehicule);
    }

    private Client nouveauClient() {
        Client client = new Client();
        client.setNom("Trabelsi");
        client.setPrenom("Ines");
        client.setEmail("ines." + suffixe() + "@autoloc.tn");
        client.setTelephone("+216 22 111 222");
        client.setNumPermis("PERMIS" + suffixe());
        client.setDateInscription(LocalDate.now());
        return clientRepository.save(client);
    }

    private Contrat nouveauContrat() {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(new BigDecimal("900.00"));
        contrat.setValide(true);
        return contratRepository.save(contrat);
    }

    // question 17 : creation des colonnes necessaires
    @Test
    public void creationDesColonnes() {
        Assertions.assertTrue(colonneExiste("vehicule", "%agence%"),
                "La colonne de liaison vehicule -> agence manque");
        Assertions.assertTrue(colonneExiste("employe", "%agence%"),
                "La colonne de liaison employe -> agence manque");
        Assertions.assertTrue(colonneExiste("reservation", "%vehicule%"),
                "La colonne de liaison reservation -> vehicule manque");
        Assertions.assertTrue(colonneExiste("reservation", "%client%"),
                "La colonne de liaison reservation -> client manque");
        Assertions.assertTrue(colonneExiste("reservation", "%contrat%"),
                "La colonne de liaison reservation -> contrat manque");
        Assertions.assertTrue(colonneExiste("paiement", "%contrat%"),
                "La colonne de liaison paiement -> contrat manque");
        Assertions.assertTrue(colonneExiste("maintenance", "%vehicule%"),
                "La colonne de liaison maintenance -> vehicule manque");
    }

    // question 16 : le chargement d'une agence ne charge pas ses employes
    // et sa suppression ne supprime pas ses employes
    @Test
    public void agenceEmploye() throws NoSuchFieldException {
        jakarta.persistence.OneToMany annotation = Agence.class.getDeclaredField("employes")
                .getAnnotation(jakarta.persistence.OneToMany.class);

        Assertions.assertEquals(jakarta.persistence.FetchType.LAZY, annotation.fetch(),
                "Le chargement d'une agence ne doit pas charger les employes");
        Assertions.assertFalse(containsCascadeRemove(annotation),
                "La suppression d'une agence ne doit pas supprimer ses employes");

        Agence agence = nouvelleAgence();

        Employe employe = new Employe();
        employe.setNom("Ben Salah");
        employe.setPrenom("Sami");
        employe.setEmail("sami." + suffixe() + "@autoloc.tn");
        employe.setRole(RoleEmploye.AGENT);
        employe.setAgence(agence);
        agence.getEmployes().add(employe);
        employe = employeRepository.save(employe);

        Assertions.assertNotNull(agence.getIdAgence());
        Assertions.assertNotNull(employe.getIdEmploye());

        // la relation est bien bidirectionnelle
        Agence rechargee = entityManager.find(Agence.class, agence.getIdAgence());
        Assertions.assertNotNull(rechargee);
        Assertions.assertEquals(1, rechargee.getEmployes().size());
    }

    // question 16 : le chargement du client charge ses reservations
    // et sa suppression supprime ses reservations
    @Test
    public void clientReservation() throws NoSuchFieldException {
        jakarta.persistence.OneToMany annotation = Client.class.getDeclaredField("reservations")
                .getAnnotation(jakarta.persistence.OneToMany.class);

        Assertions.assertEquals(jakarta.persistence.FetchType.EAGER, annotation.fetch(),
                "Le chargement d'un client doit charger ses reservations");
        Assertions.assertTrue(containsCascadeRemove(annotation),
                "La suppression d'un client doit supprimer ses reservations");

        Client client = nouveauClient();
        Agence agence = nouvelleAgence();
        Vehicule vehicule = nouveauVehicule(agence);

        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now().plusDays(1));
        reservation.setDateFin(LocalDate.now().plusDays(5));
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        client.getReservations().add(reservation);
        reservation = reservationRepository.save(reservation);

        Assertions.assertNotNull(reservation.getIdReservation());
        Assertions.assertNotNull(reservation.getClient().getIdClient());
        Assertions.assertNotNull(reservation.getVehicule().getIdVehicule());
    }

    // question 16 : le chargement d'un vehicule ne charge pas ses reservations
    // et sa suppression supprime ses reservations
    @Test
    public void vehiculeReservation() throws NoSuchFieldException {
        jakarta.persistence.OneToMany annotation = Vehicule.class.getDeclaredField("reservations")
                .getAnnotation(jakarta.persistence.OneToMany.class);

        Assertions.assertEquals(jakarta.persistence.FetchType.LAZY, annotation.fetch(),
                "Le chargement d'un vehicule ne doit pas charger ses reservations");
        Assertions.assertTrue(containsCascadeRemove(annotation),
                "La suppression d'un vehicule doit supprimer ses reservations");
    }

    // question 18 : relation contrat <-> paiement,
    // le chargement du contrat charge ses paiements
    @Test
    public void contratPaiement() throws NoSuchFieldException {
        jakarta.persistence.OneToMany annotation = Contrat.class.getDeclaredField("paiements")
                .getAnnotation(jakarta.persistence.OneToMany.class);
        Assertions.assertEquals(jakarta.persistence.FetchType.EAGER, annotation.fetch(),
                "Le chargement du contrat doit charger ses paiements");

        Contrat contrat = nouveauContrat();

        Paiement paiement = new Paiement();
        paiement.setMontant(new BigDecimal("450.00"));
        paiement.setDatePaiement(LocalDate.now());
        paiement.setModePaiement(ModePaiement.CARTE);
        paiement.setContrat(contrat);
        paiement = paiementRepository.save(paiement);

        Assertions.assertNotNull(contrat.getIdContrat());
        Assertions.assertNotNull(paiement.getIdPaiement());

        entityManager.flush();

        String colonneContrat = colonneDeTable("paiement", "%contrat%");
        Assertions.assertNotNull(colonneContrat, "La colonne de liaison paiement -> contrat manque");

        Integer nb = jdbcTemplate.queryForObject(
                "select count(*) from paiement where " + colonneContrat + " = ?",
                Integer.class, contrat.getIdContrat());
        Assertions.assertNotNull(nb);
        Assertions.assertEquals(1, nb.intValue());
    }

    // question 19 : relation maintenance <-> vehicule, creation de la colonne
    @Test
    public void maintenanceVehicule() {
        Agence agence = nouvelleAgence();
        Vehicule vehicule = nouveauVehicule(agence);

        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(LocalDate.now());
        maintenance.setDateFin(LocalDate.now().plusDays(2));
        maintenance.setDescription("Vidange");
        maintenance.setVehicule(vehicule);
        maintenance = maintenanceRepository.save(maintenance);

        Assertions.assertNotNull(maintenance.getIdMaintenance());
        Assertions.assertNotNull(maintenance.getVehicule().getIdVehicule());
        Assertions.assertTrue(colonneExiste("maintenance", "%vehicule%"),
                "La colonne de liaison maintenance -> vehicule manque");

        // relation bidirectionnelle
        vehicule.getMaintenances().add(maintenance);
        Vehicule recharge = vehiculeRepository.findById(vehicule.getIdVehicule()).orElseThrow();
        Assertions.assertEquals(1, recharge.getMaintenances().size());
    }

    // question 20 : relation reservation <-> contrat avec la cle dans la table reservation
    @Test
    public void reservationContrat() {
        Client client = nouveauClient();
        Agence agence = nouvelleAgence();
        Vehicule vehicule = nouveauVehicule(agence);
        Contrat contrat = nouveauContrat();

        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now().plusDays(1));
        reservation.setDateFin(LocalDate.now().plusDays(3));
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        reservation.setContrat(contrat);
        reservation = reservationRepository.save(reservation);

        Assertions.assertNotNull(reservation.getIdReservation());

        entityManager.flush();

        // la cle est bien dans la table associee a la reservation
        String colonneContrat = colonneDeTable("reservation", "%contrat%");
        Assertions.assertNotNull(colonneContrat,
                "La cle du contrat doit etre dans la table reservation");

        Long idContrat = jdbcTemplate.queryForObject(
                "select " + colonneContrat + " from reservation where id_reservation = ?",
                Long.class, reservation.getIdReservation());
        Assertions.assertNotNull(idContrat);
        Assertions.assertEquals(contrat.getIdContrat(), idContrat);
    }

    // question 21 : relation equipement <-> vehicule avec table de jointure,
    // les chargements et les suppressions ne sont pas lies
    @Test
    public void equipementVehicule() throws NoSuchFieldException {
        jakarta.persistence.ManyToMany annotation = Vehicule.class.getDeclaredField("equipements")
                .getAnnotation(jakarta.persistence.ManyToMany.class);

        Assertions.assertEquals(jakarta.persistence.FetchType.LAZY, annotation.fetch(),
                "Les chargements de vehicule et d'equipement ne sont pas lies");
        Assertions.assertEquals(0, annotation.cascade().length,
                "Les suppressions de vehicule et d'equipement ne sont pas liees");

        Equipement gps = new Equipement();
        gps.setLibelle("GPS " + suffixe());
        gps = equipementRepository.save(gps);

        Equipement siegeBebe = new Equipement();
        siegeBebe.setLibelle("Siege bebe " + suffixe());
        siegeBebe = equipementRepository.save(siegeBebe);

        Agence agence = nouvelleAgence();
        Vehicule vehicule = nouveauVehicule(agence);
        vehicule.getEquipements().add(gps);
        vehicule.getEquipements().add(siegeBebe);
        vehicule = vehiculeRepository.save(vehicule);

        Assertions.assertNotNull(vehicule.getIdVehicule());
        Assertions.assertNotNull(gps.getIdEquipement());

        entityManager.flush();

        // la table de jointure a bien ete cree et renseignee
        Assertions.assertTrue(tableExiste("vehicule_equipement"),
                "La table de jointure vehicule_equipement manque");
        Integer nb = jdbcTemplate.queryForObject(
                "select count(*) from vehicule_equipement where id_vehicule = ?",
                Integer.class, vehicule.getIdVehicule());
        Assertions.assertNotNull(nb);
        Assertions.assertEquals(2, nb.intValue());
    }

    private boolean containsCascadeRemove(jakarta.persistence.OneToMany annotation) {
        for (jakarta.persistence.CascadeType type : annotation.cascade()) {
            if (type == jakarta.persistence.CascadeType.REMOVE) {
                return true;
            }
        }
        return false;
    }

    private boolean colonneExiste(String table, String patternColonne) {
        return colonneDeTable(table, patternColonne) != null;
    }

    private String colonneDeTable(String table, String patternColonne) {
        return jdbcTemplate.query(
                "select column_name from information_schema.columns " +
                        "where table_schema = database() and table_name = ? and column_name like ?",
                rs -> rs.next() ? rs.getString(1) : null,
                table, patternColonne);
    }

    private boolean tableExiste(String table) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables " +
                        "where table_schema = database() and table_name = ?",
                Integer.class, table);
        return count != null && count > 0;
    }
}

interface EmployeRepositoryMock extends CrudRepository<Employe, Long> {
}

interface VehiculeRepositoryMock extends CrudRepository<Vehicule, Long> {
}

interface ClientRepositoryMock extends CrudRepository<Client, Long> {
}

interface ReservationRepositoryMock extends CrudRepository<Reservation, Long> {
}

interface ContratRepositoryMock extends CrudRepository<Contrat, Long> {
}

interface PaiementRepositoryMock extends CrudRepository<Paiement, Long> {
}

interface MaintenanceRepositoryMock extends CrudRepository<Maintenance, Long> {
}

interface EquipementRepositoryMock extends CrudRepository<Equipement, Long> {
}
