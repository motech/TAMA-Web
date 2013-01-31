package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.ClinicRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClinicReportingService extends ReportingService<ClinicRequest> {

    public static final String PATH_TO_CLINIC = "clinic";

    @Autowired
    public ClinicReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        super(reportingProperties, httpClientService);
    }

    public void save(ClinicRequest clinicRequest) {
        super.save(clinicRequest, PATH_TO_CLINIC);
    }

    public void update(ClinicRequest clinicRequest) {
        super.update(clinicRequest, PATH_TO_CLINIC + "/update");
    }
}
