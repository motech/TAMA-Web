package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.ClinicianRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClinicianReportingService extends ReportingService<ClinicianRequest> {

    public static final String PATH_TO_CLINICIAN = "clinician";

    @Autowired
    public ClinicianReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        super(reportingProperties, httpClientService);
    }

    public void save(ClinicianRequest request) {
        super.save(request, PATH_TO_CLINICIAN);
    }

    public void update(ClinicianRequest request) {
        super.update(request, PATH_TO_CLINICIAN);
    }
}
