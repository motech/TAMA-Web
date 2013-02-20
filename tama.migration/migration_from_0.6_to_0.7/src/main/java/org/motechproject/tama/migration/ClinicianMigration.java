package org.motechproject.tama.migration;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.reporting.ClinicianRequestMapper;
import org.motechproject.tama.migration.repository.PagedClinicianRepository;
import org.motechproject.tama.reporting.service.ClinicianReportingService;
import org.motechproject.tama.reports.contract.ClinicianRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClinicianMigration extends Migration<Clinician> {

    private ClinicianReportingService clinicianReportingService;

    @Autowired
    public ClinicianMigration(PagedClinicianRepository allDocuments, ClinicianReportingService clinicianReportingService) {
        super(allDocuments);
        this.clinicianReportingService = clinicianReportingService;
    }

    @Override
    @Seed(version = "2.0", priority = 0)
    public void migrate() {
        super.migrate();
    }

    @Override
    protected void save(Clinician document) {
        ClinicianRequest clinicianRequest = new ClinicianRequestMapper(document).map();
        clinicianReportingService.save(clinicianRequest);
    }
}
