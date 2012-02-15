package org.motechproject.tama.appointments.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentReminderHandler {

    OutboxService outboxService;

    @Autowired
    public AppointmentReminderHandler(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    @MotechListener(subjects = EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT)
    public void handleEvent(MotechEvent appointmentReminderEvent) {
        String patientId = appointmentReminderEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        outboxService.addMessage(patientId, TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }
}
