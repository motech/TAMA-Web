package org.motechproject.tama.clinicvisits.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.clinicvisits.repository.AllAppointments;
import org.motechproject.tama.clinicvisits.service.VisitReminderService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitReminderHandler {

    private AllPatients allPatients;
    private AllAppointments allAppointments;
    private VisitReminderService visitReminderService;

    @Autowired
    public VisitReminderHandler(AllPatients allPatients,
                                AllAppointments allAppointments,
                                VisitReminderService visitReminderService) {
        this.allPatients = allPatients;
        this.allAppointments = allAppointments;
        this.visitReminderService = visitReminderService;
    }

    @MotechListener(subjects = EventKeys.VISIT_REMINDER_EVENT_SUBJECT)
    public void handleEvent(MotechEvent visitReminderEvent) {
        String patientId = visitReminderEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        String appointmentId = visitReminderEvent.getParameters().get(EventKeys.APPOINTMENT_ID).toString();
        String visitName = visitReminderEvent.getParameters().get(EventKeys.VISIT_NAME).toString();

        Patient patient = allPatients.get(patientId);
        Appointment appointment = allAppointments.get(appointmentId);

        visitReminderService.addOutboxMessage(patient, appointment, visitName);
    }
}
