package org.motechproject.tama.clinicvisits.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderAlertCriteria;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderOutboxCriteria;
import org.motechproject.tama.clinicvisits.repository.AllAppointments;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentReminderHandler {

    private AllPatients allPatients;
    private PatientAlertService patientAlertService;
    private AllAppointments allAppointments;
    private OutboxService outboxService;
    private ReminderOutboxCriteria appointmentReminderCriteria;
    private ReminderAlertCriteria appointmentConfirmationCriteria;

    @Autowired
    public AppointmentReminderHandler(AllPatients allPatients,
                                      PatientAlertService patientAlertService,
                                      AllAppointments allAppointments,
                                      OutboxService outboxService,
                                      ReminderOutboxCriteria appointmentReminderCriteria,
                                      ReminderAlertCriteria appointmentConfirmationCriteria) {
        this.allPatients = allPatients;
        this.patientAlertService = patientAlertService;
        this.allAppointments = allAppointments;
        this.outboxService = outboxService;
        this.appointmentReminderCriteria = appointmentReminderCriteria;
        this.appointmentConfirmationCriteria = appointmentConfirmationCriteria;
    }

    @MotechListener(subjects = EventKeys.REMINDER_EVENT_SUBJECT)
    public void handleEvent(MotechEvent appointmentReminderEvent) {
        String patientId = appointmentReminderEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        String appointmentId = appointmentReminderEvent.getParameters().get(EventKeys.APPOINTMENT_ID).toString();

        Patient patient = allPatients.get(patientId);
        Appointment appointment = allAppointments.get(appointmentId);

        if (appointmentReminderCriteria.shouldAddOutboxMessage(patient)) {
            outboxService.addMessage(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
        }

        if (appointmentConfirmationCriteria.shouldRaiseAlert(appointment)) {
            patientAlertService.createAlert(patient.getId(),
                    TAMAConstants.NO_ALERT_PRIORITY,
                    TAMAConstants.APPOINTMENT_REMINDER,
                    "",
                    PatientAlertType.AppointmentReminder,
                    null);
        }
    }
}
