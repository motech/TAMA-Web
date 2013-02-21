package org.motechproject.tama.reporting;


import lombok.Data;
import org.motechproject.tama.reports.contract.ClinicRequest;
import org.motechproject.tama.reports.contract.ClinicianContactRequest;

import java.util.List;

@Data
public class ClinicReportingRequest {

    private ClinicRequest clinicRequest;
    private List<ClinicianContactRequest> clinicianContactRequests;

    public ClinicReportingRequest(ClinicRequest clinicRequest, List<ClinicianContactRequest> clinicianContactRequests) {
        this.clinicRequest = clinicRequest;
        this.clinicianContactRequests = clinicianContactRequests;
    }
}
