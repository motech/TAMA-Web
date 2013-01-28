package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;
import org.motechproject.tama.reports.contract.PatientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientReportingService {

    public static final String PATH_TO_PATIENT = "patient";

    private HttpClientService httpClientService;
    private ReportingProperties reportingProperties;
    private MedicalHistoryReportingService medicalHistoryReportingService;

    @Autowired
    public PatientReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties, MedicalHistoryReportingService medicalHistoryReportingService) {
        this.httpClientService = httpClientService;
        this.reportingProperties = reportingProperties;
        this.medicalHistoryReportingService = medicalHistoryReportingService;
    }

    public void save(PatientRequest patientRequest, MedicalHistoryRequest medicalHistoryRequest) {
        httpClientService.post(reportingProperties.reportingURL() + PATH_TO_PATIENT, patientRequest);
        medicalHistoryReportingService.save(medicalHistoryRequest);
    }

    public void update(PatientRequest patientRequest) {
        httpClientService.post(reportingProperties.reportingURL() + PATH_TO_PATIENT + "/update", patientRequest);
    }
}
