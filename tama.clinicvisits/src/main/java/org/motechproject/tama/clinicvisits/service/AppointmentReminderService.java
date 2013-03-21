package org.motechproject.tama.clinicvisits.service;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TAMAReminderConfiguration;
import org.motechproject.tama.clinicvisits.domain.criteria.AppointmentConfirmationMissedAlertCriteria;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderAlertCriteria;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderOutboxCriteria;
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
public class AppointmentReminderService {

    private ReminderOutboxCriteria reminderOutboxCriteria;
    private ReminderAlertCriteria reminderAlertCriteria;
    private AppointmentConfirmationMissedAlertCriteria appointmentConfirmationMissedAlertCriteria;
    private TAMAReminderConfiguration TAMAReminderConfiguration;

    private OutboxService outboxService;
    private PatientAlertService patientAlertService;

    @Autowired
    public AppointmentReminderService(ReminderOutboxCriteria reminderOutboxCriteria,
                                      ReminderAlertCriteria reminderAlertCriteria,
                                      PatientAlertService patientAlertService,
                                      OutboxService outboxService,
                                      AppointmentConfirmationMissedAlertCriteria appointmentConfirmationMissedAlertCriteria,
                                      TAMAReminderConfiguration TAMAReminderConfiguration) {
        this.reminderOutboxCriteria = reminderOutboxCriteria;
        this.reminderAlertCriteria = reminderAlertCriteria;
        this.patientAlertService = patientAlertService;
        this.outboxService = outboxService;
        this.appointmentConfirmationMissedAlertCriteria = appointmentConfirmationMissedAlertCriteria;
        this.TAMAReminderConfiguration = TAMAReminderConfiguration;
    }

    public void addOutboxMessage(Patient patient, ClinicVisit clinicVisit) {
        if(reminderOutboxCriteria.shouldAddPushedOutboxMessageForAppointments(patient, clinicVisit)){
            for(int i=0; i< TAMAReminderConfiguration.getPushedAppointmentReminderVoiceMessageCount(); i++){
                outboxService.addMessage(patient.getId(), TAMAConstants.PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE);
            }
        }
        if (reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, clinicVisit)) {
            outboxService.addMessage(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
        }
    }

    public void raiseAlert(Patient patient, ClinicVisit clinicVisit) {
        if (reminderAlertCriteria.shouldRaiseAlert(clinicVisit)) {
            raiseAppointmentAlert(patient, clinicVisit, TAMAConstants.APPOINTMENT_REMINDER, PatientAlertType.AppointmentReminder);
        }
        if (appointmentConfirmationMissedAlertCriteria.shouldRaiseAlert(clinicVisit)) {
            raiseAppointmentAlert(patient, clinicVisit, TAMAConstants.APPOINTMENT_LOST_REMINDER, PatientAlertType.AppointmentConfirmationMissed);
        }
    }

    private void raiseAppointmentAlert(Patient patient, ClinicVisit clinicVisit, String alertName, PatientAlertType alertType) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.APPOINTMENT_DATE, clinicVisit.getEffectiveDueDate().toString());
        patientAlertService.createAlert(patient.getId(), TAMAConstants.NO_ALERT_PRIORITY,
                alertName, "", alertType, data);
    }
}
