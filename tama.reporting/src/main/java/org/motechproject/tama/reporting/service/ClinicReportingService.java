package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.ClinicReportingRequest;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClinicReportingService extends ReportingService {

    public static final String PATH_TO_CLINIC = "clinic";
    private static final String PATH_TO_CLINICIAN_CONTACTS = "clinicianContact";

    @Autowired
    public ClinicReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        super(reportingProperties, httpClientService);
    }

    public void save(ClinicReportingRequest clinicReportingRequest) {
        super.save(clinicReportingRequest.getClinicRequest(), PATH_TO_CLINIC);
        super.save(clinicReportingRequest.getClinicianContactRequests(), PATH_TO_CLINICIAN_CONTACTS);
    }

    public void update(ClinicReportingRequest clinicRequest) {
        super.update(clinicRequest.getClinicRequest(), PATH_TO_CLINIC);
        super.update(clinicRequest.getClinicianContactRequests(), PATH_TO_CLINICIAN_CONTACTS);
    }
}
