package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(nullable = false, length = 200)
    private String adresse;

    @Column(nullable = false, length = 20)
    private String telephone;


// question 1+2(partie 1)
    @OneToMany(mappedBy = "agence", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<Vehicule> vehicules=new HashSet<>(); ;

// partie 3 (question 16) : chargement de l'agence != chargement des employés (LAZY par défaut)
    // et suppression de l'agence ne supprime pas les employés (pas de CascadeType.REMOVE)
    @OneToMany(mappedBy = "agence", cascade = CascadeType.PERSIST)
    private List<Employe> employes = new ArrayList<>();

}
