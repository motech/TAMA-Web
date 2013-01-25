package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.PatientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientReportingService {

    public static final String PATH_TO_PATIENT = "patient";

    private HttpClientService httpClientService;
    private ReportingProperties reportingProperties;

    @Autowired
    public PatientReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        this.httpClientService = httpClientService;
        this.reportingProperties = reportingProperties;
    }

    public void save(PatientRequest patientRequest) {
        httpClientService.post(reportingProperties.reportingURL() + PATH_TO_PATIENT, patientRequest);
    }

    public void update(PatientRequest patientRequest) {
        httpClientService.post(reportingProperties.reportingURL() + PATH_TO_PATIENT + "/update", patientRequest);
    }
}
