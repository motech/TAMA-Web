package org.motechproject.tama.clinicvisits.domain.criteria;

import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReminderOutboxCriteria {

    private OutboxService outboxService;

    @Autowired
    public ReminderOutboxCriteria(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    public boolean shouldAddOutboxMessageForAppointments(Patient patient, Appointment appointment) {
        return (patient.getPatientPreferences().getActivateAppointmentReminders())
                && (!outboxService.hasPendingOutboxMessages(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE))
                && (appointment.confirmedDate() == null);
    }

    public boolean shouldAddOutboxMessageForVisits(Patient patient, Appointment appointment) {
        return (patient.getPatientPreferences().getActivateAppointmentReminders())
                && (!outboxService.hasPendingOutboxMessages(patient.getId(), TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE))
                && (appointment.confirmedDate() != null)
                && (DateUtil.now().isBefore(appointment.confirmedDate()));
    }
}
