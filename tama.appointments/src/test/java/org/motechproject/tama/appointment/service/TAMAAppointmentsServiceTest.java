package org.motechproject.tama.appointment.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.AppointmentService;
import org.motechproject.appointments.api.ReminderService;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.tama.appointment.service.TAMAAppointmentsService;
import org.motechproject.tama.patient.service.ClinicVisitService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class TAMAAppointmentsServiceTest extends BaseUnitTest {

    static final String PATIENT_ID = "patientId";
    static int FIRST_SCHEDULE_WEEK = 1;
    static int SECOND_SCHEDULE_WEEK = 2;

    @Mock
    protected ReminderService reminderService;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private ClinicVisitService clinicVisitService;

    protected Properties appointmentsTemplate;
    protected DateTime now;
    protected LocalDate today;

    protected TAMAAppointmentsService tamaAppointmentsService;

    @Before
    public void setUp() {
        initMocks(this);
        setUpTime();
        setUpAppointmentsTemplate();
        tamaAppointmentsService = new TAMAAppointmentsService(appointmentService, reminderService, clinicVisitService, appointmentsTemplate);
    }

    private void setUpTime() {
        now = DateUtil.now();
        today = now.toLocalDate();
        mockCurrentDate(now);
    }

    private void setUpAppointmentsTemplate() {
        appointmentsTemplate = new Properties();
        appointmentsTemplate.setProperty(TAMAAppointmentsService.APPOINTMENT_SCHEDULE, FIRST_SCHEDULE_WEEK + "," + SECOND_SCHEDULE_WEEK);
    }

    @Test
    public void shouldCreateAppointments() {
        DateTime activationTime = now;
        LocalDate activationDate = activationTime.toLocalDate();
        ArgumentCaptor<Appointment> appointmentCapture = ArgumentCaptor.forClass(Appointment.class);

        tamaAppointmentsService.scheduleAppointments(PATIENT_ID, activationTime);
        verify(appointmentService, times(2)).addAppointment(appointmentCapture.capture());
        verifyAppointment(PATIENT_ID, activationDate, FIRST_SCHEDULE_WEEK, appointmentCapture.getAllValues().get(0));
        verifyAppointment(PATIENT_ID, activationDate, SECOND_SCHEDULE_WEEK, appointmentCapture.getAllValues().get(1));
    }

    @Test
    public void shouldCreateExpectedClinicVisit() {
        DateTime activationTime = now;
        ArgumentCaptor<DateTime> dateCapture = ArgumentCaptor.forClass(DateTime.class);

        tamaAppointmentsService.scheduleAppointments(PATIENT_ID, activationTime);
        verify(clinicVisitService, times(2)).createExpectedVisit(dateCapture.capture(), eq(PATIENT_ID));
        assertEquals(activationTime.plusWeeks(FIRST_SCHEDULE_WEEK), dateCapture.getAllValues().get(0));
        assertEquals(activationTime.plusWeeks(SECOND_SCHEDULE_WEEK), dateCapture.getAllValues().get(1));
    }

    @Test
    public void shouldCreateReminders() {
        ArgumentCaptor<Reminder> reminderCapture = ArgumentCaptor.forClass(Reminder.class);

        tamaAppointmentsService.scheduleAppointments(PATIENT_ID, now);
        verify(reminderService, times(2)).addReminder(reminderCapture.capture());
        verifyReminder(PATIENT_ID, FIRST_SCHEDULE_WEEK, reminderCapture.getAllValues().get(0));
        verifyReminder(PATIENT_ID, SECOND_SCHEDULE_WEEK, reminderCapture.getAllValues().get(1));
    }

    private void verifyAppointment(String patientId, LocalDate activationDate, int offsetWeekNumber, Appointment appointment) {
        assertEquals(patientId, appointment.getExternalId());
        assertEquals(activationDate.plusWeeks(offsetWeekNumber).toDate(), appointment.getScheduledDate());
    }

    private void verifyReminder(String patientId, int week, Reminder reminder) {
        assertEquals(patientId, reminder.getExternalId());
        assertNotNull(reminder.getAppointmentId());
        assertEquals(today.plusWeeks(week).toDate(), reminder.getStartDate());
    }
}
