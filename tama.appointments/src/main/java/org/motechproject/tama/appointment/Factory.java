package org.motechproject.tama.appointment;

import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;

public class Factory {

    public static Reminder createReminder(String patientId, String appointmentId, LocalDate startDate) {
        Reminder reminder = new Reminder();
        reminder.setAppointmentId(appointmentId);
        reminder.setStartDate(startDate.toDate());
        reminder.setExternalId(patientId);
        return reminder;
    }

    public static Appointment createAppointment(String patientId, LocalDate scheduledDate) {
        Appointment appointment = new Appointment();
        appointment.setExternalId(patientId);
        appointment.setScheduledDate(scheduledDate.toDate());
        return appointment;
    }
}
