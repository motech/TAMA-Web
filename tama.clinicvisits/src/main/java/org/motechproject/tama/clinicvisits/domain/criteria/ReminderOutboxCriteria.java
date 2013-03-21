package org.motechproject.tama.clinicvisits.domain.criteria;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TAMAReminderConfiguration;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.tama.common.TAMAConstants.*;

@Component
public class ReminderOutboxCriteria {

    private OutboxService outboxService;
    private TAMAReminderConfiguration TAMAReminderConfiguration;

    @Autowired
    public ReminderOutboxCriteria(OutboxService outboxService, TAMAReminderConfiguration TAMAReminderConfiguration) {
        this.outboxService = outboxService;
        this.TAMAReminderConfiguration = TAMAReminderConfiguration;
    }

    public boolean shouldAddPushedOutboxMessageForAppointments(Patient patient, ClinicVisit clinicVisit) {
        return (patient.getPatientPreferences().getActivateAppointmentReminders())
                && (clinicVisit.getConfirmedAppointmentDate() == null)
                && (!outboxService.hasPendingOutboxMessages(patient.getId(), PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE))
                && (!outboxService.hasMessages(patient.getId(), PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE, TAMAReminderConfiguration.reminderStartDate(clinicVisit)))
                && (!DateUtil.today().isAfter(clinicVisit.getEffectiveDueDate()));
    }

    public boolean shouldAddOutboxMessageForAppointments(Patient patient, ClinicVisit clinicVisit) {
        return (patient.getPatientPreferences().getActivateAppointmentReminders())
                && (!outboxService.hasPendingOutboxMessages(patient.getId(), APPOINTMENT_REMINDER_VOICE_MESSAGE))
                && (clinicVisit.getConfirmedAppointmentDate() == null)
                && (!DateUtil.today().isAfter(clinicVisit.getEffectiveDueDate()));
    }

    public boolean shouldAddOutboxMessageForVisits(Patient patient, ClinicVisit clinicVisit) {
        return (patient.getPatientPreferences().getActivateAppointmentReminders())
                && (!outboxService.hasPendingOutboxMessages(patient.getId(), VISIT_REMINDER_VOICE_MESSAGE))
                && (clinicVisit.getConfirmedAppointmentDate() != null)
                && (DateUtil.now().isBefore(clinicVisit.getConfirmedAppointmentDate())
                && clinicVisit.getVisitDate() == null);
    }

}
