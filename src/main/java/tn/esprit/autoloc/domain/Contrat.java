package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contrat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;

    @Column(nullable = false)
    private LocalDate dateSignature;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(nullable = false)
    private Boolean valide;

// partie 3 (question 18) : le chargement du contrat charge ses paiements (EAGER)
    @OneToMany(mappedBy = "contrat", fetch = FetchType.EAGER)
    private List<Paiement> paiements = new ArrayList<>();

// partie 3 (questions 16/20) : côté inverse de la relation, la clé reste dans reservation
    @OneToMany(mappedBy = "contrat")
    private List<Reservation> reservations = new ArrayList<>();
}
