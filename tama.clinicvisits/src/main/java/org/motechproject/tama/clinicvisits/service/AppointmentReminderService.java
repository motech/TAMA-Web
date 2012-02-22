package org.motechproject.tama.clinicvisits.service;

import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AppointmentReminderService {

    private final int DAYS_FOR_REMINDER_ALERT;

    private OutboxService outboxService;
    private PatientAlertService patientAlertService;

    @Autowired
    public AppointmentReminderService(PatientAlertService patientAlertService,
                                      @Qualifier("appointments") Properties appointmentProperties,
                                      OutboxService outboxService) {

        this.patientAlertService = patientAlertService;

        String dayForReminderAlertProperty = appointmentProperties.getProperty(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED);
        this.DAYS_FOR_REMINDER_ALERT = Integer.parseInt(dayForReminderAlertProperty);

        this.outboxService = outboxService;
    }

    public void raiseReminderAlert(Patient patient, Appointment appointment) {
        String adjustedDateString = (String) appointment.getData().get(ClinicVisit.ADJUSTED_DUE_DATE);
        LocalDate adjustedDueDate = adjustedDateString == null ? null : new LocalDate(adjustedDateString);
        LocalDate dueDate = adjustedDueDate == null ? appointment.dueDate().toLocalDate() : adjustedDueDate;
        boolean isDayOfReminderAlert = DateUtil.today().equals(dueDate.minusDays(DAYS_FOR_REMINDER_ALERT));
        if (appointment.firmDate() == null && isDayOfReminderAlert) {
            patientAlertService.createAlert(patient.getId(), TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.APPOINTMENT_REMINDER, "", PatientAlertType.AppointmentReminder, null);
        }
    }

    public void raiseOutboxMessage(Patient patient) {
        String reminderMessageType = TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE;
        boolean noPendingReminderMessage = !outboxService.hasPendingOutboxMessages(patient.getId(), reminderMessageType);
        if (patient.shouldReceiveAppointmentReminder() && noPendingReminderMessage) {
            outboxService.addMessage(patient.getId(), reminderMessageType);
        }
    }
}
