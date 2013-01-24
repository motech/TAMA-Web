package org.motechproject.tama.reporting.mapper;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reports.contract.PatientRequest;

public class PatientRequestMapper {

    private Patient patient;

    public PatientRequestMapper(Patient patient) {
        this.patient = patient;
    }

    public PatientRequest map() {
        PatientRequest request = new PatientRequest();
        new BasicDetails(patient).copyTo(request);
        new IVRDetails(patient).copyTo(request);
        return request;
    }
}
