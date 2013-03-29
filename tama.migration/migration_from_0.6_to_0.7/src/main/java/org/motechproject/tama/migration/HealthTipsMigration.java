package org.motechproject.tama.migration;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.reporting.MessagesRequestMapper;
import org.motechproject.tama.migration.repository.PagedCallLogsRepository;
import org.motechproject.tama.reporting.service.CallLogReportingService;
import org.motechproject.tama.reports.contract.MessagesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HealthTipsMigration extends Migration<CallLog> {

    private CallLogReportingService reportingService;

    @Autowired
    public HealthTipsMigration(PagedCallLogsRepository allDocuments, CallLogReportingService reportingService) {
        super(allDocuments);
        this.reportingService = reportingService;
    }

    @Override
    @Seed(version = "2.0", priority = 0)
    public void migrate() {
        super.migrate();
    }

    @Override
    protected void save(CallLog document) {
        MessagesRequest request = new MessagesRequestMapper(document).map(CallTypeConstants.HEALTH_TIPS, null);
        reportingService.reportMessages(request);
    }
}
