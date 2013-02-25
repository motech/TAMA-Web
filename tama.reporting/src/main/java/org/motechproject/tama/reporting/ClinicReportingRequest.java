package org.motechproject.tama.reporting;


import lombok.Data;
import org.motechproject.tama.reports.contract.ClinicRequest;
import org.motechproject.tama.reports.contract.ClinicianContactRequests;

@Data
public class ClinicReportingRequest {

    private ClinicRequest clinicRequest;
    private ClinicianContactRequests clinicianContactRequests;

    public ClinicReportingRequest(ClinicRequest clinicRequest, ClinicianContactRequests clinicianContactRequests) {
        this.clinicRequest = clinicRequest;
        this.clinicianContactRequests = clinicianContactRequests;
    }
}
