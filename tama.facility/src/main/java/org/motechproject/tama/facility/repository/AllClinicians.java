package org.motechproject.tama.facility.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.facility.domain.Clinician;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllClinicians extends AbstractCouchRepository<Clinician> {
    private AllClinics allClinics;
    private AllClinicianIds allClinicianIds;
    private PBEStringEncryptor encryptor;

    @Autowired
    public AllClinicians(@Qualifier("tamaDbConnector") CouchDbConnector db, PBEStringEncryptor encryptor, AllClinics allClinics, AllClinicianIds clinicinanIds) {
        super(Clinician.class, db);
        this.allClinics = allClinics;
        this.encryptor = encryptor;
        this.allClinicianIds = clinicinanIds;
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
        allClinicianIds.add(clinician);
        super.add(clinician);
    }

    @Override
    public void remove(Clinician clinician) {
        super.remove(clinician);
        allClinicianIds.remove(clinician);
    }

    @Override
    public void update(Clinician clinician) {
        Clinician dbClinician = get(clinician.getId());
        clinician.setUsername(dbClinician.getUsername());
        clinician.setRevision(dbClinician.getRevision());
        clinician.setEncryptedPassword(dbClinician.getEncryptedPassword());
        super.update(clinician);
    }

    public void updatePassword(Clinician clinician) {
        Clinician dbClinician = get(clinician.getId());
        clinician.setRevision(dbClinician.getRevision());
        clinician.setEncryptedPassword(encryptor.encrypt(clinician.getPassword()));
        super.update(clinician);
    }

    @Override
    public List<Clinician> getAll() {
        List<Clinician> clinicianList = super.getAll();
        for (Clinician clinician : clinicianList) {
            loadDependencies(clinician);
        }
        return clinicianList;
    }

    private void loadDependencies(Clinician clinician) {
        if (!StringUtils.isEmpty(clinician.getClinicId())) clinician.setClinic(allClinics.get(clinician.getClinicId()));
    }

    @Override
    public Clinician get(String id) {
        Clinician clinician = super.get(id);
        loadDependencies(clinician);
        clinician.setPassword(encryptor.decrypt(clinician.getPassword()));
        return clinician;
    }

}
