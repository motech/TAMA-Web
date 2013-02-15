package org.motechproject.tama.migration;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.migration.repository.PagedPatientEventsRepository;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.tama.patient.reporting.PatientEventRequestMapper;
import org.motechproject.tama.reporting.service.PatientEventReportingService;
import org.motechproject.tama.reports.contract.PatientEventRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientEventLogMigration extends Migration<PatientEventLog> {

    private PatientEventReportingService reportingService;

    @Autowired
    public PatientEventLogMigration(PagedPatientEventsRepository allDocuments, PatientEventReportingService reportingService) {
        super(allDocuments);
        this.reportingService = reportingService;
    }

    @Override
    @Seed(version = "2.0", priority = 0)
    public void migrate() {
        super.migrate();
    }

    @Override
    protected void save(PatientEventLog document) {
        PatientEventRequest request = new PatientEventRequestMapper(document).map("");
        reportingService.save(request);
    }
}
