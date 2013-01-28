package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicalHistoryReportingService {
    public static final String PATH_TO_MEDICAL_HISTORY = "medicalHistory";

    private HttpClientService httpClientService;
    private ReportingProperties reportingProperties;

    @Autowired
    public MedicalHistoryReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        this.httpClientService = httpClientService;
        this.reportingProperties = reportingProperties;
    }

    public void save(MedicalHistoryRequest medicalHistoryRequest) {
        httpClientService.post(reportingProperties.reportingURL() + PATH_TO_MEDICAL_HISTORY, medicalHistoryRequest);
    }

    public void update(MedicalHistoryRequest medicalHistoryRequest) {
        httpClientService.post(reportingProperties.reportingURL() + PATH_TO_MEDICAL_HISTORY + "/update", medicalHistoryRequest);
    }

}
