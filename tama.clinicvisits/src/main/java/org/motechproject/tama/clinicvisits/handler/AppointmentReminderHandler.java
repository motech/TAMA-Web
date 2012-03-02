package org.motechproject.tama.clinicvisits.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.clinicvisits.service.AppointmentReminderService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentReminderHandler {

    private AllPatients allPatients;
    private AllClinicVisits allClinicVisits;
    private AppointmentReminderService appointmentReminderService;

    @Autowired
    public AppointmentReminderHandler(AllPatients allPatients, AllClinicVisits allClinicVisits, AppointmentReminderService appointmentReminderService) {
        this.allPatients = allPatients;
        this.allClinicVisits = allClinicVisits;
        this.appointmentReminderService = appointmentReminderService;
    }

    @MotechListener(subjects = EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT)
    public void handleEvent(MotechEvent appointmentReminderEvent) {
        String patientDocId = appointmentReminderEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        String visitName = appointmentReminderEvent.getParameters().get(EventKeys.VISIT_NAME).toString();

        Patient patient = allPatients.get(patientDocId);
        ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, visitName);

        appointmentReminderService.addOutboxMessage(patient, clinicVisit);
        appointmentReminderService.raiseAlert(patient, clinicVisit);
    }
}
