package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.PatientEventRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientEventReportingService extends ReportingService {

    public static final String PATH_TO_PATIENT_EVENT = "/patientEvent";

    @Autowired
    public PatientEventReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        super(reportingProperties, httpClientService);
    }

    public void save(PatientEventRequest patientEventRequest) {
        super.save(patientEventRequest, PATH_TO_PATIENT_EVENT);
    }
}
