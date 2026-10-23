package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.repository.IEmployeRepository;

@Service
public class EmployeServiceImpl extends GenericServiceImpl<Employe, Long> implements IEmployeService {

    public EmployeServiceImpl(IEmployeRepository employeRepository) {
        super(employeRepository);
    }

    @Override
    protected String getEntityName() {
        return "Employe";
    }

    @Override
    protected Long getId(Employe employe) {
        return employe.getIdEmploye();
    }

    @Override
    protected void setFields(Employe source, Employe target) {
        target.setNom(source.getNom());
        target.setPrenom(source.getPrenom());
        target.setRole(source.getRole());
    }

    @Override
    public boolean check(Employe employe, boolean isNew) {
        return !isNew || employe.getIdEmploye() == null;
    }
}
