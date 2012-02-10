package org.motechproject.tama.appointments.service;

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
import org.motechproject.tama.patient.service.ClinicVisitService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
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

        tamaAppointmentsService.scheduleAppointments(PATIENT_ID);
        verify(appointmentService, times(3)).addAppointment(appointmentCapture.capture());
        verifyAppointment(PATIENT_ID, activationDate, 0, appointmentCapture.getAllValues().get(0));
        verifyAppointment(PATIENT_ID, activationDate, FIRST_SCHEDULE_WEEK, appointmentCapture.getAllValues().get(1));
        verifyAppointment(PATIENT_ID, activationDate, SECOND_SCHEDULE_WEEK, appointmentCapture.getAllValues().get(2));
    }

    @Test
    public void shouldCreateExpectedClinicVisit() {
	//DateTimeSourceUtil.SourceInstance = new FixedDateTimeSource(now);
        DateTime activationTime = now;
        ArgumentCaptor<DateTime> dateCapture = ArgumentCaptor.forClass(DateTime.class);

        tamaAppointmentsService.scheduleAppointments(PATIENT_ID);
        verify(clinicVisitService, times(2)).createExpectedVisit(dateCapture.capture(), anyInt(), eq(PATIENT_ID));
        verify(clinicVisitService, times(1)).createFirstVisit(now, PATIENT_ID);
        assertEquals(activationTime, dateCapture.getAllValues().get(0));
    }

    @Test
    public void shouldCreateReminders() {
        ArgumentCaptor<Reminder> reminderCapture = ArgumentCaptor.forClass(Reminder.class);

        tamaAppointmentsService.scheduleAppointments(PATIENT_ID);
        verify(reminderService, times(3)).addReminder(reminderCapture.capture());
        verifyReminder(PATIENT_ID, 0, reminderCapture.getAllValues().get(0));
        verifyReminder(PATIENT_ID, FIRST_SCHEDULE_WEEK, reminderCapture.getAllValues().get(1));
        verifyReminder(PATIENT_ID, SECOND_SCHEDULE_WEEK, reminderCapture.getAllValues().get(2));
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
