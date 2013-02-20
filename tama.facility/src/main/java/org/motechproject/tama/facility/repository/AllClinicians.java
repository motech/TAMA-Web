package org.motechproject.tama.facility.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.reporting.ClinicianRequestMapper;
import org.motechproject.tama.reporting.service.ClinicianReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;

@Component
public class AllClinicians extends AuditableCouchRepository<Clinician> {
    private AllClinics allClinics;
    private AllClinicianIds allClinicianIds;
    private ClinicianReportingService clinicianReportingService;
    private PBEStringEncryptor encryptor;

    @Autowired
    public AllClinicians(@Qualifier("tamaDbConnector") CouchDbConnector db, PBEStringEncryptor encryptor, AllClinics allClinics, AllClinicianIds clinicinanIds, AllAuditRecords allAuditRecords, ClinicianReportingService clinicianReportingService) {
        super(Clinician.class, db, allAuditRecords);
        this.allClinics = allClinics;
        this.encryptor = encryptor;
        this.allClinicianIds = clinicinanIds;
        this.clinicianReportingService = clinicianReportingService;
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
        Clinician clinician = singleResult(clinicians);
        if (clinician != null) {
            loadDependencies(asList(clinician));
            clinician.setPassword(encryptor.decrypt(clinician.getEncryptedPassword()));
        }
        return clinician;
    }

    @Override
    public void add(Clinician clinician, String userName) {
        add(clinician, userName, true);
    }

    @Override
    public void remove(Clinician clinician, String userName) {
        super.remove(clinician, userName);
        allClinicianIds.remove(clinician);
    }

    @Override
    public void update(Clinician clinician, String userName) {
        Clinician dbClinician = get(clinician.getId());
        clinician.setUsername(dbClinician.getUsername());
        clinician.setRevision(dbClinician.getRevision());
        clinician.setEncryptedPassword(dbClinician.getEncryptedPassword());
        super.update(clinician, userName);
        clinicianReportingService.update(new ClinicianRequestMapper(clinician).map());
    }

    public void updatePassword(Clinician clinician, String userName) {
        Clinician dbClinician = get(clinician.getId());
        clinician.setRevision(dbClinician.getRevision());
        clinician.setEncryptedPassword(encryptor.encrypt(clinician.getPassword()));
        super.update(clinician, userName);
    }

    @Override
    public List<Clinician> getAll() {
        List<Clinician> clinicianList = super.getAll();
        loadDependencies(clinicianList);
        return clinicianList;
    }

    @Override
    public Clinician get(String id) {
        Clinician clinician = super.get(id);
        loadDependencies(asList(clinician));
        clinician.setPassword(encryptor.decrypt(clinician.getPassword()));
        return clinician;
    }

    protected void add(Clinician clinician, String userName, boolean report){
        clinician.setEncryptedPassword(encryptor.encrypt(clinician.getPassword()));
        allClinicianIds.add(clinician);
        super.add(clinician, userName);
        if(report){
            clinicianReportingService.save(new ClinicianRequestMapper(clinician).map());
        }
    }

    protected void loadDependencies(List<Clinician> clinicians) {
        for (Clinician clinician : clinicians) {
            if (!StringUtils.isEmpty(clinician.getClinicId()))
                clinician.setClinic(allClinics.get(clinician.getClinicId()));
        }
    }
}
