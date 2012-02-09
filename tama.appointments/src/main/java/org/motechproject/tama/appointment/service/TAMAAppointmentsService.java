package org.motechproject.tama.appointment.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.AppointmentService;
import org.motechproject.appointments.api.ReminderService;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.tama.appointment.Factory;
import org.motechproject.tama.common.util.UUIDUtil;
import org.motechproject.tama.patient.service.ClinicVisitService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class TAMAAppointmentsService {

    public static final String APPOINTMENT_SCHEDULE = "appointment-schedule";

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
        List<Integer> weeks = ListOfWeeks.weeks(appointmentsTemplate.getProperty(APPOINTMENT_SCHEDULE));
        DateTime now = DateUtil.now();
	scheduleAppointmentForWeek(patientId, now , 0);
        for (Integer week : weeks) {
            scheduleAppointmentForWeek(patientId, now, week);
        }
    }

    private void scheduleAppointmentForWeek(String patientId, DateTime visitDateTime, Integer weeks) {
	LocalDate visitDate = visitDateTime.toLocalDate();
	Appointment appointment = Factory.createAppointment(patientId, visitDate.plusWeeks(weeks));
	appointment.setId(UUIDUtil.newUUID());
	appointmentService.addAppointment(appointment);

	Reminder appointmentReminder = Factory.createReminder(patientId, appointment.getId(), visitDate.plusWeeks(weeks));

	reminderService.addReminder(appointmentReminder);
	if (weeks == 0 ) 
	    clinicVisitService.createFirstVisit(visitDateTime, patientId);
	else 
	    clinicVisitService.createExpectedVisit(visitDateTime, weeks, patientId);
    }
}
