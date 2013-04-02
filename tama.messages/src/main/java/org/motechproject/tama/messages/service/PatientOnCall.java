package org.motechproject.tama.messages.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.Appointment;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.service.ClinicVisitService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientOnCall {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private ClinicVisitService clinicVisitService;

    @Autowired
    public PatientOnCall(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, ClinicVisitService clinicVisitService) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.clinicVisitService = clinicVisitService;
    }

    public Patient getPatient(TAMAIVRContext context) {
        return allPatients.get(context.patientDocumentId());
    }

    public TreatmentAdvice getCurrentTreatmentAdvice(TAMAIVRContext context) {
        return allTreatmentAdvices.currentTreatmentAdvice(context.patientDocumentId());
    }

    public Appointment getUpcomingAppointment(TAMAIVRContext context, LocalDate reference) {
        return getClinicVisits(context).upcomingAppointment(reference);
    }

    public ClinicVisits getClinicVisits(TAMAIVRContext context) {
        return clinicVisitService.get(context.patientDocumentId());
    }
}
