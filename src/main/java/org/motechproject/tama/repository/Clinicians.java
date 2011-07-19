package org.motechproject.tama.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.domain.Clinician;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Clinicians extends AbstractCouchRepository<Clinician> {
    private Clinics clinics;
    private ClinicianIds clinicianIds;
    private PBEStringEncryptor encryptor;

    @Autowired
    public Clinicians(CouchDbConnector db, PBEStringEncryptor encryptor, Clinics clinics, ClinicianIds clinicinanIds) {
        super(Clinician.class, db);
        this.clinics = clinics;
        this.encryptor = encryptor;
        this.clinicianIds = clinicinanIds;
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
            loadDependencies(clinician);
            clinician.setPassword(encryptor.decrypt(clinician.getEncryptedPassword()));
            return clinician;
        }
        return null;
    }

    @Override
    public void add(Clinician clinician) {
        clinician.setEncryptedPassword(encryptor.encrypt(clinician.getPassword()));
        clinicianIds.add(clinician);
        super.add(clinician);
    }

    @Override
    public void remove(Clinician clinician) {
        super.remove(clinician);
        clinicianIds.remove(clinician);
    }

    @Override
    public void update(Clinician clinician) {
        Clinician dbClinician = get(clinician.getId());
        clinician.setUsername(dbClinician.getUsername());
        clinician.setRevision(dbClinician.getRevision());
        clinician.setEncryptedPassword(dbClinician.getEncryptedPassword());
        super.update(clinician);
    }

    public void updatePassword(Clinician clinician){
        Clinician dbClinician = get(clinician.getId());
        clinician.setRevision(dbClinician.getRevision());
        clinician.setEncryptedPassword(encryptor.encrypt(clinician.getPassword()));
        super.update(clinician);
    }

    @Override
    public List<Clinician> getAll() {
        List<Clinician> clinicianList = super.getAll();
        for(Clinician clinician : clinicianList) {
            loadDependencies(clinician);
        }
        return clinicianList;
    }

    private void loadDependencies(Clinician clinician) {
        if(!StringUtils.isEmpty(clinician.getClinicId())) clinician.setClinic(clinics.get(clinician.getClinicId()));
    }

    @Override
    public Clinician get(String id) {
        Clinician clinician = super.get(id);
        loadDependencies(clinician);
        clinician.setPassword(encryptor.decrypt(clinician.getPassword()));
        return clinician;
    }

}
