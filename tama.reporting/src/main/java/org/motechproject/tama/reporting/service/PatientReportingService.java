package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;
import org.motechproject.tama.reports.contract.PatientRequest;
import org.motechproject.tama.reports.contract.PillTimeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientReportingService extends ReportingService<PatientRequest> {

    public static final String PATH_TO_PATIENT = "patient";

    private MedicalHistoryReportingService medicalHistoryReportingService;

    @Autowired
    public PatientReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties, MedicalHistoryReportingService medicalHistoryReportingService) {
        super(reportingProperties, httpClientService);
        this.medicalHistoryReportingService = medicalHistoryReportingService;
    }

    public void save(PatientRequest patientRequest, MedicalHistoryRequest medicalHistoryRequest) {
        super.save(patientRequest, PATH_TO_PATIENT);
        medicalHistoryReportingService.save(medicalHistoryRequest);
    }

    public void update(PatientRequest patientRequest, MedicalHistoryRequest medicalHistoryRequest) {
        super.update(patientRequest, PATH_TO_PATIENT);
        medicalHistoryReportingService.update(medicalHistoryRequest);
    }

    public void savePillTimes(PillTimeRequest request) {
        httpClientService.put(reportingProperties.reportingURL() + PATH_TO_PATIENT + "/pillTimes", request);
    }
}
