package org.motechproject.tama.facility.reporting;


import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.reports.contract.ClinicianContactRequest;
import org.motechproject.tama.reports.contract.ClinicianContactRequests;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ClinicianContactRequestMapper {

    private Clinic clinic;

    public ClinicianContactRequestMapper(Clinic clinic) {
        this.clinic = clinic;
    }

    public ClinicianContactRequests map() {
        List<ClinicianContactRequest> requests = new ArrayList<>();
        for (Clinic.ClinicianContact clinicianContact : clinic.getClinicianContacts()) {
            ClinicianContactRequest request = new ClinicianContactRequest();
            BeanUtils.copyProperties(clinicianContact, request);
            request.setClinicId(clinic.getId());
            requests.add(request);
        }
        return new ClinicianContactRequests(requests);
    }
}
