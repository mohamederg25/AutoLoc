package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import java.math.*;
import java.time.*;

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

    private LocalDate dateSignature;

    private BigDecimal montantTotal;

    private boolean valide;

    @OneToMany(mappedBy = "contrat", fetch = FetchType.EAGER)
    private Set<Paiement> paiements;

    @OneToOne(mappedBy = "contrat")
    private Reservation reservation;
}
