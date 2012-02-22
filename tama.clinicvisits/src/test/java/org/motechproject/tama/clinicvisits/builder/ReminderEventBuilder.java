package org.motechproject.tama.clinicvisits.builder;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.patient.domain.Patient;

import java.util.HashMap;
import java.util.Map;

public class ReminderEventBuilder {

    private Patient patient;
    private Appointment appointment;

    private ReminderEventBuilder() {
    }

    public static ReminderEventBuilder startRecording() {
        return new ReminderEventBuilder();
    }

    public ReminderEventBuilder withPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public ReminderEventBuilder withAppointment(Appointment appointment) {
        this.appointment = appointment;
        return this;
    }

    public MotechEvent build() {
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put(EventKeys.EXTERNAL_ID_KEY, patient.getId());
        eventParams.put(EventKeys.APPOINTMENT_ID, appointment.id());
        return new MotechEvent(EventKeys.REMINDER_EVENT_SUBJECT, eventParams);
    }
}
