package org.motechproject.tama.clinicvisits.builder;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.patient.domain.Patient;

import java.util.HashMap;
import java.util.Map;

public class ReminderEventBuilder {

    private Patient patient;
    private ClinicVisit visit;

    private ReminderEventBuilder() {
    }

    public static ReminderEventBuilder startRecording() {
        return new ReminderEventBuilder();
    }

    public ReminderEventBuilder withPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public ReminderEventBuilder withClinicVisit(ClinicVisit visit) {
        this.visit = visit;
        return this;
    }

    public MotechEvent build() {
        Map<String, Object> eventParams = new HashMap<String, Object>();

        if (patient != null)
            eventParams.put(EventKeys.EXTERNAL_ID_KEY, patient.getId());
        if (visit != null)
            eventParams.put(EventKeys.VISIT_NAME, visit.getId());

        return new MotechEvent(EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT, eventParams);
    }
}