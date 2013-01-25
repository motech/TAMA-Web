package org.motechproject.tama.patient.reporting;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reports.contract.PatientRequest;
import org.springframework.stereotype.Component;

@Component
public class PatientRequestMapper {

    public PatientRequest map(Patient patient) {
        PatientRequest request = new PatientRequest();
        new BasicDetails(patient).copyTo(request);
        new IVRDetails(patient).copyTo(request);
        return request;
    }
}
