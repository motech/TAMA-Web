package org.motechproject.tama.clinicvisits.factory;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.util.DateUtil;

public class AppointmentsFactory {

    public static Reminder createReminder(String patientId, Appointment appointment, Integer remindFrom, Integer remindTill) {
        Reminder reminder = new Reminder();
        reminder.setAppointmentId(appointment.getId());
        reminder.setExternalId(patientId);
        reminder.setStartDate(appointment.getScheduledDate().minusDays(remindFrom).toDate());
        reminder.setEndDate(appointment.getScheduledDate().minusDays(remindTill).toDate());
        reminder.setIntervalCount(1);
        reminder.setUnits(Reminder.intervalUnits.DAYS);
        reminder.setEnabled(true);
        return reminder;
    }

    public static Appointment createAppointment(String patientId, DateTime scheduledDate, String appointmentId) {
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setExternalId(patientId);
        appointment.setScheduledDate(scheduledDate);
        return appointment;
    }
}
