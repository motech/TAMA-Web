package org.motechproject.tama.clinicvisits.service;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
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


    @Autowired
    public VisitReminderService(ReminderOutboxCriteria reminderOutboxCriteria, OutboxService outboxService, VisitMissedAlertCriteria visitMissedAlertCriteria, PatientAlertService patientAlertService) {
        this.reminderOutboxCriteria = reminderOutboxCriteria;
        this.outboxService = outboxService;
        this.visitMissedAlertCriteria = visitMissedAlertCriteria;
        this.patientAlertService = patientAlertService;
    }

    public void addOutboxMessage(Patient patient, ClinicVisit clinicVisit) {
        if (reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit)) {
            final HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME, clinicVisit.getId());
            outboxService.addMessage(patient.getId(), TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE, parameters);
        }
    }

    public void raiseAlert(Patient patient, ClinicVisit clinicVisit) {
        if (visitMissedAlertCriteria.shouldRaiseAlert(clinicVisit)) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put(PatientAlert.APPOINTMENT_DATE, clinicVisit.getConfirmedAppointmentDate().toLocalDate().toString());
            patientAlertService.createAlert(patient.getId(), TAMAConstants.NO_ALERT_PRIORITY,
                    TAMAConstants.APPOINTMENT_MISSED_REMINDER, "", PatientAlertType.VisitMissed, data);
        }
    }

}