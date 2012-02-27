package org.motechproject.tama.clinicvisits.service;

import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderOutboxCriteria;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class VisitReminderService {

    private ReminderOutboxCriteria reminderOutboxCriteria;
    private OutboxService outboxService;

    @Autowired
    public VisitReminderService(ReminderOutboxCriteria reminderOutboxCriteria, OutboxService outboxService) {
        this.reminderOutboxCriteria = reminderOutboxCriteria;
        this.outboxService = outboxService;
    }

    public void addOutboxMessage(Patient patient, Appointment appointment, String visitName) {
        if (reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, appointment)) {
            final HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME, visitName);
            outboxService.addMessage(patient.getId(), TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE, parameters);
        }
    }
}