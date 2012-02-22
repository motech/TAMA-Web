package org.motechproject.tama.clinicvisits.builder;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.Appointment;

public class AppointmentBuilder {

    private DateTime dueDate;

    private AppointmentBuilder() {
    }

    public static AppointmentBuilder startRecording() {
        return new AppointmentBuilder();
    }

    public AppointmentBuilder withDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public Appointment build() {
        return new Appointment().dueDate(dueDate);
    }
}
