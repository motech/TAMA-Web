package org.motechproject.tama.migration.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.repository.AllClinicianIds;
import org.motechproject.tama.facility.repository.AllClinicians;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.reporting.service.ClinicianReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedClinicianRepository extends AllClinicians implements Paged<Clinician> {

    @Autowired
    public PagedClinicianRepository(@Qualifier("tamaDbConnector") CouchDbConnector db, PBEStringEncryptor encryptor, AllClinics allClinics, AllClinicianIds clinicinanIds, AllAuditRecords allAuditRecords, ClinicianReportingService clinicianReportingService) {
        super(db, encryptor, allClinics, clinicinanIds, allAuditRecords, clinicianReportingService);
    }

    @Override
    public List<Clinician> get(int skip, int limit) {
        ViewQuery query = createQuery("all").includeDocs(true).skip(skip).limit(limit);
        List<Clinician> clinicians = db.queryView(query, Clinician.class);
        loadDependencies(clinicians);
        return clinicians;
    }

    @Override
    protected void add(Clinician clinician, String userName, boolean report) {
        super.add(clinician, userName, report);
    }

}

