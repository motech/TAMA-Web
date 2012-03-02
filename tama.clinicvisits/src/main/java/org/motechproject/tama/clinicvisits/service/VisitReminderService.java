package org.motechproject.tama.clinicvisits.service;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
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

    public void addOutboxMessage(Patient patient, ClinicVisit clinicVisit) {
        if (reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit)) {
            final HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME, clinicVisit.getId());
            outboxService.addMessage(patient.getId(), TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE, parameters);
        }
    }
}