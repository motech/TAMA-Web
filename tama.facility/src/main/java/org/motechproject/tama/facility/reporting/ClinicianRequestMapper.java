package org.motechproject.tama.facility.reporting;

import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.reports.contract.ClinicianRequest;


public class ClinicianRequestMapper {

    private Clinician clinician;

    public ClinicianRequestMapper(Clinician clinician) {
        this.clinician = clinician;
    }

    public ClinicianRequest map() {
        ClinicianRequest request = new ClinicianRequest();
        request.setClinicianId(clinician.getId());
        request.setClinicId(clinician.getClinicId());
        request.setContactNumber(clinician.getContactNumber());
        request.setAlternateNumber(clinician.getAlternateContactNumber());
        request.setRole(clinician.getRole().name());
        request.setUserName(clinician.getUsername());
        request.setName(clinician.getName());
        return request;
    }
}
