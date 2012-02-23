package org.motechproject.tama.clinicvisits.domain;

import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateAppointmentReminderCriteria {

    private OutboxService outboxService;

    @Autowired
    public CreateAppointmentReminderCriteria(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    public boolean shouldRaiseReminder(Patient patient) {
        return patient.getPatientPreferences().getActivateAppointmentReminders()
                && !outboxService.hasPendingOutboxMessages(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }


}
