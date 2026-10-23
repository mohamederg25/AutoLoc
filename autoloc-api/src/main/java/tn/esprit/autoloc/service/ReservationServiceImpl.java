package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.repository.IReservationRepository;

@Service
public class ReservationServiceImpl extends GenericServiceImpl<Reservation, Long> implements IReservationService {

    public ReservationServiceImpl(IReservationRepository reservationRepository) {
        super(reservationRepository);
    }

    @Override
    protected String getEntityName() {
        return "Reservation";
    }

    @Override
    protected Long getId(Reservation reservation) {
        return reservation.getIdReservation();
    }

    @Override
    protected void setFields(Reservation source, Reservation target) {
        target.setDateDebut(source.getDateDebut());
        target.setDateFin(source.getDateFin());
        target.setStatut(source.getStatut());
    }

    @Override
    public boolean check(Reservation reservation, boolean isNew) {
        return !isNew || reservation.getIdReservation() == null;
    }
}
