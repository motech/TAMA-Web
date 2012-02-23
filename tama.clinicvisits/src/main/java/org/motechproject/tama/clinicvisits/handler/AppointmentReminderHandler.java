package org.motechproject.tama.clinicvisits.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.clinicvisits.repository.AllAppointments;
import org.motechproject.tama.clinicvisits.service.AppointmentReminderService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentReminderHandler {

    private AllPatients allPatients;
    private AllAppointments allAppointments;
    private AppointmentReminderService appointmentReminderService;

    @Autowired
    public AppointmentReminderHandler(AllPatients allPatients,
                                      AllAppointments allAppointments,
                                      AppointmentReminderService appointmentReminderService) {
        this.allPatients = allPatients;
        this.allAppointments = allAppointments;
        this.appointmentReminderService = appointmentReminderService;
    }

    @MotechListener(subjects = EventKeys.REMINDER_EVENT_SUBJECT)
    public void handleEvent(MotechEvent appointmentReminderEvent) {
        String patientId = appointmentReminderEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        String appointmentId = appointmentReminderEvent.getParameters().get(EventKeys.APPOINTMENT_ID).toString();

        Patient patient = allPatients.get(patientId);
        Appointment appointment = allAppointments.get(appointmentId);

        appointmentReminderService.addOutboxMessage(patient, appointment);
        appointmentReminderService.raiseAlert(patient, appointment);
    }
}
