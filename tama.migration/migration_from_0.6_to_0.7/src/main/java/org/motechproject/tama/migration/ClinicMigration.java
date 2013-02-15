package org.motechproject.tama.migration;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.reporting.ClinicRequestMapper;
import org.motechproject.tama.migration.repository.PagedClinicsRepository;
import org.motechproject.tama.reporting.service.ClinicReportingService;
import org.motechproject.tama.reports.contract.ClinicRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClinicMigration extends Migration<Clinic> {

    private ClinicReportingService reportingService;

    @Autowired
    public ClinicMigration(PagedClinicsRepository allDocuments, ClinicReportingService reportingService) {
        super(allDocuments);
        this.reportingService = reportingService;
    }

    @Override
    @Seed(version = "1.0", priority = 0)
    public void migrate() {
        super.migrate();
    }

    @Override
    protected void save(Clinic document) {
        ClinicRequest request = new ClinicRequestMapper(document).map();
        reportingService.save(request);
    }
}
