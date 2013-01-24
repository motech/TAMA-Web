package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reporting.mapper.PatientRequestMapper;
import org.motechproject.tama.reporting.properties.ReportingProperties;
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

    public void save(Patient patient) {
        httpClientService.post(reportingProperties.reportingURL() + PATH_TO_PATIENT, new PatientRequestMapper(patient).map());
    }
}
