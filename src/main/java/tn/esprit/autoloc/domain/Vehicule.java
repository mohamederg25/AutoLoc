package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "vehicule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    @Column(nullable = false, unique = true, length = 20)
    private String immatriculation;

    @Column(nullable = false, length = 50)
    private String marque;

    @Column(nullable = false, length = 50)
    private String modele;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategorieVehicule categorie;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutVehicule statut;


// question 1(partie 1)
    @ManyToOne
    private Agence agence;
    //partie 3 (question 16)
    // chargement du véhicule ne charge pas les réservations (LAZY par défaut)
    // suppression du véhicule supprime ses réservations (CascadeType.REMOVE)
    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.REMOVE)
    private List<Reservation> reservations = new ArrayList<>();

    // partie 3 (question 19) : Maintenance <-> Vehicule, la colonne est dans la table maintenance
    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.PERSIST)
    private List<Maintenance> maintenances = new ArrayList<>();

    // partie 3 (question 21) : Equipement <-> Vehicule, table de jointure,
    // chargements et suppressions ne sont pas liés (LAZY par défaut, aucun cascade)
    @ManyToMany
    @JoinTable(name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "id_vehicule"),
            inverseJoinColumns = @JoinColumn(name = "id_equipement"))
    private Set<Equipement> equipements = new HashSet<>();

}
