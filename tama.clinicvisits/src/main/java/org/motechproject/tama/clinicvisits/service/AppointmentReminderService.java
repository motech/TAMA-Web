package org.motechproject.tama.clinicvisits.service;

import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
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

    private OutboxService outboxService;
    private PatientAlertService patientAlertService;

    @Autowired
    public AppointmentReminderService(ReminderOutboxCriteria reminderOutboxCriteria, ReminderAlertCriteria reminderAlertCriteria, PatientAlertService patientAlertService, OutboxService outboxService) {
        this.reminderOutboxCriteria = reminderOutboxCriteria;
        this.reminderAlertCriteria = reminderAlertCriteria;
        this.patientAlertService = patientAlertService;
        this.outboxService = outboxService;
    }

    public void addOutboxMessage(Patient patient, Appointment appointment) {
        if (reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, appointment)) {
            outboxService.addMessage(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
        }
    }

    public void raiseAlert(Patient patient, Appointment appointment) {
        if (reminderAlertCriteria.shouldRaiseAlert(appointment)) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put(PatientAlert.APPOINTMENT_DUE_DATE, effectiveDueDate(appointment).toString());
            patientAlertService.createAlert(patient.getId(), TAMAConstants.NO_ALERT_PRIORITY,
                    TAMAConstants.APPOINTMENT_REMINDER, "", PatientAlertType.AppointmentReminder, data);
        }
    }

    private LocalDate effectiveDueDate(Appointment appointment) {
        if (appointment.getData().get(ClinicVisit.ADJUSTED_DUE_DATE) != null) {
            return new LocalDate(appointment.getData().get(ClinicVisit.ADJUSTED_DUE_DATE));
        } else {
            return appointment.dueDate().toLocalDate();
        }
    }
}
