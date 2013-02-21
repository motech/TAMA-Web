package org.motechproject.tama.facility.reporting;


import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.reports.contract.ClinicianContactRequest;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ClinicianContactRequestMapper {

    private Clinic clinic;

    public ClinicianContactRequestMapper(Clinic clinic) {
        this.clinic = clinic;
    }

    public List<ClinicianContactRequest> map() {
        List<ClinicianContactRequest> requests = new ArrayList<>();
        for (Clinic.ClinicianContact clinicianContact : clinic.getClinicianContacts()) {
            ClinicianContactRequest request = new ClinicianContactRequest();
            BeanUtils.copyProperties(clinicianContact, request);
            requests.add(request);
        }
        return requests;
    }
}
