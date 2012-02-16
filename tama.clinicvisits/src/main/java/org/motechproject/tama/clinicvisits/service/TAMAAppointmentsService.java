package org.motechproject.tama.clinicvisits.service;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.AppointmentService;
import org.motechproject.appointments.api.ReminderService;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.tama.clinicvisits.factory.AppointmentsFactory;
import org.motechproject.tama.clinicvisits.domain.ListOfWeeks;
import org.motechproject.tama.common.util.UUIDUtil;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class TAMAAppointmentsService {

    public static final String APPOINTMENT_SCHEDULE = "appointment-schedule";
    public static final String REMIND_FROM = "remindFrom";
    public static final String REMIND_TILL = "remindTill";

    private AppointmentService appointmentService;
    private ReminderService reminderService;
    private ClinicVisitService clinicVisitService;
    private Properties appointmentsTemplate;

    @Autowired
    public TAMAAppointmentsService(AppointmentService appointmentService, ReminderService reminderService, ClinicVisitService clinicVisitService, @Qualifier("appointmentsTemplate") Properties appointmentsTemplate) {
        this.appointmentService = appointmentService;
        this.reminderService = reminderService;
        this.clinicVisitService = clinicVisitService;
        this.appointmentsTemplate = appointmentsTemplate;
    }

    public void scheduleAppointments(String patientId) {
        List<Integer> appointmentWeeks = ListOfWeeks.weeks(appointmentsTemplate.getProperty(APPOINTMENT_SCHEDULE));
        DateTime now = DateUtil.now();
        createAppointmentReminderAndVisit(patientId, now, 0);
        for (Integer week : appointmentWeeks) {
            createAppointmentReminderAndVisit(patientId, now, week);
        }
    }

    private void createAppointmentReminderAndVisit(String patientId, DateTime firstAppointmentTime, Integer week) {
        DateTime appointmentDate = firstAppointmentTime.plusWeeks(week);
        Appointment appointment = AppointmentsFactory.createAppointment(patientId, appointmentDate, UUIDUtil.newUUID());
        scheduleAppointment(appointment);
        createReminder(patientId, appointment);
        createClinicVisits(patientId, firstAppointmentTime, week);
    }

    private void scheduleAppointment(Appointment appointment) {
        appointmentService.addAppointment(appointment);
    }

    private void createReminder(String patientId, Appointment appointment) {
        Reminder appointmentReminder = AppointmentsFactory.createReminder(
                patientId,
                appointment,
                Integer.parseInt(appointmentsTemplate.getProperty(REMIND_FROM)),
                Integer.parseInt(appointmentsTemplate.getProperty(REMIND_TILL)));
        reminderService.addReminder(appointmentReminder);
    }

    private void createClinicVisits(String patientId, DateTime firstAppointmentTime, Integer week) {
       clinicVisitService.createExpectedVisit(firstAppointmentTime, week, patientId);
    }
}
