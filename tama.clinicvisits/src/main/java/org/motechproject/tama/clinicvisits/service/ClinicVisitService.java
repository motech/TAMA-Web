package org.motechproject.tama.clinicvisits.service;

import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.appointments.api.service.contract.VisitsQuery;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClinicVisitService {

    private AppointmentService appointmentService;
    private AllPatients allPatients;

    @Autowired
    public ClinicVisitService(AppointmentService appointmentService, AllPatients allPatients) {
        this.appointmentService = appointmentService;
        this.allPatients = allPatients;
    }

    public ClinicVisits get(String patientDocumentId) {
        VisitsQuery query = new VisitsQuery().havingExternalId(patientDocumentId);
        List<VisitResponse> responses = appointmentService.search(query);
        Patient patient = allPatients.get(patientDocumentId);
        return new ClinicVisits(patient, responses);
    }
}
