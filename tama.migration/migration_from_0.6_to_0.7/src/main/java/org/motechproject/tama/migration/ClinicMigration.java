package org.motechproject.tama.migration;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.common.util.UUIDUtil;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.reporting.ClinicRequestMapper;
import org.motechproject.tama.facility.reporting.ClinicianContactRequestMapper;
import org.motechproject.tama.migration.repository.PagedClinicsRepository;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.reporting.ClinicReportingRequest;
import org.motechproject.tama.reporting.service.ClinicReportingService;
import org.motechproject.tama.reports.contract.ClinicRequest;
import org.motechproject.tama.reports.contract.ClinicianContactRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClinicMigration extends Migration<Clinic> {

    private PagedClinicsRepository allDocuments;
    private ClinicReportingService reportingService;
    private AllCitiesCache allCities;

    @Autowired
    public ClinicMigration(PagedClinicsRepository allDocuments, ClinicReportingService reportingService, AllCitiesCache allCities) {
        super(allDocuments);
        this.allDocuments = allDocuments;
        this.reportingService = reportingService;
        this.allCities = allCities;
    }

    @Override
    @Seed(version = "2.0", priority = 0)
    public void migrate() {
        super.migrate();
    }

    @Override
    protected void save(Clinic document) {
        updateClinicianContactUUID(document);
        publishToReports(document);
    }

    private void updateClinicianContactUUID(Clinic document) {
        for (Clinic.ClinicianContact clinicianContact : document.getClinicianContacts()) {
            clinicianContact.setId(UUIDUtil.newUUID());
        }
        allDocuments.update(document);
        /*Dummy fetch to rebuild index*/
        allDocuments.get(document.getId());
    }

    private void publishToReports(Clinic document) {
        ClinicRequest request = new ClinicRequestMapper(allCities, document).map();
        ClinicianContactRequests contactRequests = new ClinicianContactRequestMapper(document).map();
        reportingService.save(new ClinicReportingRequest(request, contactRequests));
    }
}
