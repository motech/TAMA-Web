package org.motechproject.tama.migration.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.reporting.service.ClinicReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedClinicsRepository extends AllClinics implements Paged<Clinic> {

    @Autowired
    public PagedClinicsRepository(@Qualifier("tamaDbConnector") CouchDbConnector db, AllCitiesCache allCities, AllAuditRecords allAuditRecords, ClinicReportingService clinicReportingService) {
        super(db, allCities, allAuditRecords, clinicReportingService);
    }

    @Override
    public List<Clinic> get(int skip, int limit) {
        ViewQuery query = createQuery("all").includeDocs(true).skip(skip).limit(limit);
        List<Clinic> clinics = db.queryView(query, Clinic.class);
        loadDependencies(clinics);
        return clinics;
    }
}

