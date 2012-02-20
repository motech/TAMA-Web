package org.motechproject.tama.clinicvisits.factory;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;

import java.util.Date;

public class AppointmentsFactory {

    public static Appointment createAppointment(DateTime scheduledDate, Reminder reminder) {
        return new Appointment().dueDate(scheduledDate).reminder(reminder);
    }

    public static Reminder createReminder(DateTime scheduledDate, Integer remindFrom, Integer remindTill) {
        Date startDate = scheduledDate.toLocalDate().minusDays(remindFrom).toDate();
        Date endDate = scheduledDate.toLocalDate().minusDays(remindTill).toDate();
        return new Reminder().startDate(startDate).endDate(endDate).intervalCount(1).units(Reminder.IntervalUnits.DAYS).repeatCount(remindFrom - remindTill);
    }

    public static Visit createVisit(Appointment appointment, int weeks) {
        boolean firstAppointment = weeks == 0;
        String title = firstAppointment ? "Registered with TAMA" : weeks + " weeks Follow-up visit";
        ClinicVisit.TypeOfVisit typeOfVisit = firstAppointment ? ClinicVisit.TypeOfVisit.Baseline : ClinicVisit.TypeOfVisit.Scheduled;
        Visit visit = new Visit().title(title).appointment(appointment);
        visit.addData(ClinicVisit.TYPE_OF_VISIT, typeOfVisit.toString());
        return visit;
    }
}
