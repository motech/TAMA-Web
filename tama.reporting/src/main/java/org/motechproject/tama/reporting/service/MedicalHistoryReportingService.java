package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicalHistoryReportingService extends ReportingService {

    public static final String PATH_TO_MEDICAL_HISTORY = "medicalHistory";

    @Autowired
    public MedicalHistoryReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        super(reportingProperties, httpClientService);
    }

    public void save(MedicalHistoryRequest medicalHistoryRequest) {
        super.save(medicalHistoryRequest, PATH_TO_MEDICAL_HISTORY);
    }

    public void update(MedicalHistoryRequest medicalHistoryRequest) {
        super.update(medicalHistoryRequest, PATH_TO_MEDICAL_HISTORY);
    }
}
