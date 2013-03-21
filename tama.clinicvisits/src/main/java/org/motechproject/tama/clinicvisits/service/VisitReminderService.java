package org.motechproject.tama.clinicvisits.service;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TAMAReminderConfiguration;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderOutboxCriteria;
import org.motechproject.tama.clinicvisits.domain.criteria.VisitMissedAlertCriteria;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class VisitReminderService {

    private ReminderOutboxCriteria reminderOutboxCriteria;
    private OutboxService outboxService;
    private VisitMissedAlertCriteria visitMissedAlertCriteria;
    private PatientAlertService patientAlertService;
    private TAMAReminderConfiguration tamaReminderConfiguration;


    @Autowired
    public VisitReminderService(ReminderOutboxCriteria reminderOutboxCriteria, OutboxService outboxService, VisitMissedAlertCriteria visitMissedAlertCriteria, PatientAlertService patientAlertService, TAMAReminderConfiguration tamaReminderConfiguration) {
        this.reminderOutboxCriteria = reminderOutboxCriteria;
        this.outboxService = outboxService;
        this.visitMissedAlertCriteria = visitMissedAlertCriteria;
        this.patientAlertService = patientAlertService;
        this.tamaReminderConfiguration = tamaReminderConfiguration;
    }

    public void addOutboxMessage(Patient patient, ClinicVisit clinicVisit) {
        addPushedVisitReminders(patient, clinicVisit);
        addPulledVisitReminders(patient, clinicVisit);
    }

    public void raiseAlert(Patient patient, ClinicVisit clinicVisit) {
        if (visitMissedAlertCriteria.shouldRaiseAlert(clinicVisit)) {
            HashMap<String, String> data = new HashMap<>();
            data.put(PatientAlert.CONFIRMED_APPOINTMENT_DATE, clinicVisit.getConfirmedAppointmentDate().toString());
            patientAlertService.createAlert(patient.getId(), TAMAConstants.NO_ALERT_PRIORITY,
                    TAMAConstants.APPOINTMENT_MISSED_REMINDER, "", PatientAlertType.VisitMissed, data);
        }
    }

    private void addPushedVisitReminders(Patient patient, ClinicVisit clinicVisit) {
        if (reminderOutboxCriteria.shouldAddPushedOutboxMessageForVisits(patient, clinicVisit)) {
            final HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME, clinicVisit.getId());
            for (int i = 0; i < tamaReminderConfiguration.getPushedVisitReminderVoiceMessageCount(); i++) {
                outboxService.addMessage(patient.getId(), TAMAConstants.PUSHED_VISIT_REMINDER_VOICE_MESSAGE, parameters);
            }
        }
    }

    private void addPulledVisitReminders(Patient patient, ClinicVisit clinicVisit) {
        if (reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit)) {
            final HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME, clinicVisit.getId());
            outboxService.addMessage(patient.getId(), TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE, parameters);
        }
    }
}