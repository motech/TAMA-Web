package org.motechproject.tama.facility.reporting;

import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.reports.contract.ClinicRequest;


public class ClinicRequestMapper {

    private Clinic clinic;

    public ClinicRequestMapper(Clinic clinic) {
        this.clinic = clinic;
    }

    public ClinicRequest map() {
        ClinicRequest request = new ClinicRequest();
        request.setClinicId(clinic.getId());
        request.setClinicName(clinic.getName());
        request.setCityName(clinic.getCity().getName());
        return request;
    }
}
