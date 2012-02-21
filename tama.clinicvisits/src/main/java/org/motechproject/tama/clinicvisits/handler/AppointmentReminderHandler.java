package org.motechproject.tama.clinicvisits.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentReminderHandler {

    OutboxService outboxService;
    private AllPatients allPatients;

    @Autowired
    public AppointmentReminderHandler(OutboxService outboxService, AllPatients allPatients) {
        this.outboxService = outboxService;
        this.allPatients = allPatients;
    }

    @MotechListener(subjects = EventKeys.REMINDER_EVENT_SUBJECT)
    public void handleEvent(MotechEvent appointmentReminderEvent) {
        String patientId = appointmentReminderEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        String reminderMessageType = TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE;
        Patient patient = allPatients.get(patientId);



        boolean shouldCreateOutboxMessage = patient.shouldReceiveAppointmentReminder() && noPendingReminderMessage(patientId, reminderMessageType);
        if (shouldCreateOutboxMessage) {
            outboxService.addMessage(patientId, reminderMessageType);
        }
    }

    private boolean noPendingReminderMessage(String patientId, String reminderMessageType) {
        return (!outboxService.hasPendingOutboxMessages(patientId, reminderMessageType));
    }
}
