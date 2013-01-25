package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.ClinicRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClinicReportingService {

    public static final String PATH_TO_CLINIC = "clinic";

    private HttpClientService httpClientService;
    private ReportingProperties reportingProperties;

    @Autowired
    public ClinicReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        this.httpClientService = httpClientService;
        this.reportingProperties = reportingProperties;
    }

    public void save(ClinicRequest clinicRequest) {
        httpClientService.post(reportingProperties.reportingURL() + PATH_TO_CLINIC, clinicRequest);
    }

    public void update(ClinicRequest clinicRequest) {
        httpClientService.post(reportingProperties.reportingURL() + PATH_TO_CLINIC + "/update", clinicRequest);
    }
}
