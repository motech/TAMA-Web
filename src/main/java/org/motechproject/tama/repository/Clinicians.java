package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Clinicians extends AbstractCouchRepository<Clinician> {
    private Clinics clinics;
    private PBEStringEncryptor encryptor;

    @Autowired
    public Clinicians(CouchDbConnector db, PBEStringEncryptor encryptor, Clinics clinics) {
        super(Clinician.class, db);
        this.clinics = clinics;
        this.encryptor = encryptor;
        initStandardDesignDocument();
    }

    public Clinician findByUserNameAndPassword(String username, String password) {
        Clinician clinician = findByUsername(username);
        if (clinician != null && clinician.credentialsAre(password)) return clinician;
        return null;
    }

    @GenerateView
    public Clinician findByUsername(String username) {
        List<Clinician> clinicians = queryView("by_username", username);
        if (clinicians != null && !clinicians.isEmpty()) {
            Clinician clinician = clinicians.get(0);
            Clinic clinic = clinics.get(clinician.getClinicId());
            clinician.setClinic(clinic);
            clinician.setPassword(encryptor.decrypt(clinician.getEncryptedPassword()));
            return clinician;
        }
        return null;
    }

    @Override
    public void add(Clinician entity) {
        entity.setEncryptedPassword(encryptor.encrypt(entity.getPassword()));
        super.add(entity);
    }

    @Override
    public Clinician get(String id) {
        Clinician clinician = super.get(id);
        clinician.setPassword(encryptor.decrypt(clinician.getPassword()));
        return clinician;
    }

}
