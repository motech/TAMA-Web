package org.motechproject.tama.clinicvisits.factory;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.util.UUIDUtil;

public class AppointmentsFactory {

    public static Appointment createAppointment(String patientId, DateTime scheduledDate) {
        Appointment appointment = new Appointment();
        appointment.setId(UUIDUtil.newUUID());
        appointment.setExternalId(patientId);
        appointment.setScheduledDate(scheduledDate);
        return appointment;
    }

    public static Reminder createReminder(Appointment appointment, String patientId, Integer remindFrom, Integer remindTill) {
        Reminder reminder = new Reminder();
        reminder.setExternalId(patientId);
        reminder.setAppointmentId(appointment.getId());
        reminder.setStartDate(appointment.getScheduledDate().toLocalDate().minusDays(remindFrom).toDate());
        reminder.setEndDate(appointment.getScheduledDate().toLocalDate().minusDays(remindTill).toDate());
        reminder.setIntervalCount(1);
        reminder.setUnits(Reminder.intervalUnits.DAYS);
        reminder.setEnabled(true);
        return reminder;
    }

    public static ClinicVisit createClinicVisit(Appointment appointment, String patientId, DateTime expectedVisitTime, int weeks) {
        ClinicVisit clinicVisit = new ClinicVisit();
        boolean firstAppointment = weeks == 0;
        String name = firstAppointment ? "Registered with TAMA" : weeks + " weeks Follow-up visit";
        ClinicVisit.TypeOfVisit typeOfVisit = firstAppointment ? ClinicVisit.TypeOfVisit.Baseline : ClinicVisit.TypeOfVisit.Scheduled;
        clinicVisit.setAppointment(appointment);
        clinicVisit.setName(name);
        clinicVisit.setAppointmentId(appointment.getId());
        clinicVisit.setTypeOfVisit(typeOfVisit);
        clinicVisit.setAppointmentDueDate(expectedVisitTime);
        clinicVisit.setPatientId(patientId);
        return clinicVisit;
    }
}
