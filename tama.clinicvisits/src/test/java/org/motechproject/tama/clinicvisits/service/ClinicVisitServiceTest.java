package org.motechproject.tama.clinicvisits.service;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.ReminderService;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.factory.AppointmentsFactory;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicVisitServiceTest extends BaseUnitTest {

    public static final String CLINIC_VISIT_ID = "clinicVisitId";
    static final String PATIENT_ID = "patientId";
    static int FIRST_SCHEDULE_WEEK = 1;
    static int SECOND_SCHEDULE_WEEK = 2;
    private static final String REMIND_FROM_DAYS = "10";
    private static final String REMIND_TILL_DAYS = "5";
    protected Properties appointmentsTemplate;
    protected DateTime now;
    protected LocalDate today;

    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    protected ReminderService reminderService;

    private ClinicVisitService clinicVisitService;

    @Before
    public void setUp() {
        initMocks(this);
        setUpTime();
        setUpAppointmentsTemplate();
        clinicVisitService = new ClinicVisitService(allClinicVisits, reminderService, appointmentsTemplate);
    }

    private void setUpTime() {
        now = DateUtil.now();
        today = now.toLocalDate();
        mockCurrentDate(now);
    }

    private void setUpAppointmentsTemplate() {
        appointmentsTemplate = new Properties();
        appointmentsTemplate.setProperty(ClinicVisitService.APPOINTMENT_SCHEDULE, FIRST_SCHEDULE_WEEK + "," + SECOND_SCHEDULE_WEEK);
        appointmentsTemplate.setProperty(ClinicVisitService.REMIND_FROM, REMIND_FROM_DAYS);
        appointmentsTemplate.setProperty(ClinicVisitService.REMIND_TILL, REMIND_TILL_DAYS);
    }

    @Test
    public void shouldCreateAppointments() {
        DateTime activationTime = now;
        LocalDate activationDate = activationTime.toLocalDate();
        ArgumentCaptor<ClinicVisit> clinicVisitCapture = ArgumentCaptor.forClass(ClinicVisit.class);

        clinicVisitService.scheduleVisits(PATIENT_ID);
        verify(allClinicVisits, times(3)).add(clinicVisitCapture.capture());
        verifyAppointment(PATIENT_ID, activationDate, 0, clinicVisitCapture.getAllValues().get(0).getAppointment());
        verifyAppointment(PATIENT_ID, activationDate, FIRST_SCHEDULE_WEEK, clinicVisitCapture.getAllValues().get(1).getAppointment());
        verifyAppointment(PATIENT_ID, activationDate, SECOND_SCHEDULE_WEEK, clinicVisitCapture.getAllValues().get(2).getAppointment());
    }

    @Test
    public void shouldCreateExpectedClinicVisit() {
        DateTime activationTime = now;
        ArgumentCaptor<ClinicVisit> clinicVisitCapture = ArgumentCaptor.forClass(ClinicVisit.class);

        clinicVisitService.scheduleVisits(PATIENT_ID);
        verify(allClinicVisits, times(3)).add(clinicVisitCapture.capture());
        assertEquals(activationTime, clinicVisitCapture.getAllValues().get(0).getAppointmentDueDate());
    }

    @Test
    public void shouldCreateReminders() {
        ArgumentCaptor<Reminder> reminderCapture = ArgumentCaptor.forClass(Reminder.class);

        clinicVisitService.scheduleVisits(PATIENT_ID);
        verify(reminderService, times(3)).addReminder(reminderCapture.capture());
        verifyReminder(PATIENT_ID, 0, reminderCapture.getAllValues().get(0));
        verifyReminder(PATIENT_ID, FIRST_SCHEDULE_WEEK, reminderCapture.getAllValues().get(1));
        verifyReminder(PATIENT_ID, SECOND_SCHEDULE_WEEK, reminderCapture.getAllValues().get(2));
    }

    @Test
    public void shouldCreateExpectedVisit() {
        clinicVisitService.createExpectedVisit("patientId", DateUtil.now(), 0);
        verify(allClinicVisits).add(any(ClinicVisit.class));
    }

    @Test
    public void shouldChangeRegimenForAVisit() {
        final String newTreatmentAdviceId = "newTreatmentAdviceId";
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();

        when(allClinicVisits.get(CLINIC_VISIT_ID)).thenReturn(clinicVisit);
        clinicVisitService.changeRegimen(CLINIC_VISIT_ID, newTreatmentAdviceId);

        verify(allClinicVisits).update(clinicVisit);
        assertEquals(newTreatmentAdviceId, clinicVisit.getTreatmentAdviceId());
    }

    @Test
    public void shouldGetListOfClinicVisitsForPatient() throws Exception {
        final String patientId = "patientId";
        final Appointment appointment = new Appointment() {{ setId("appointmentId"); }};
        ClinicVisits visitsInDb = new ClinicVisits(){{
            ClinicVisit clinicVisit = AppointmentsFactory.createClinicVisit(appointment, patientId, DateUtil.now(), 0);
            add(clinicVisit);}};
        when(allClinicVisits.findByPatientId(patientId)).thenReturn(visitsInDb);
        List<ClinicVisit> visits = clinicVisitService.getClinicVisits(patientId);
        assertEquals(visitsInDb, visits);
    }

    @Test
    public void shouldAdjustDueDate() throws Exception {
        final LocalDate today = DateUtil.today();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        when(allClinicVisits.get(CLINIC_VISIT_ID)).thenReturn(clinicVisit);

        clinicVisitService.adjustDueDate(CLINIC_VISIT_ID, today);

        verify(allClinicVisits).update(any(ClinicVisit.class));
    }

    @Test
    public void shouldUpdateConfirmedDueDate() throws Exception {
        final DateTime today = DateUtil.now();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        when(allClinicVisits.get(CLINIC_VISIT_ID)).thenReturn(clinicVisit);

        clinicVisitService.confirmVisitDate(CLINIC_VISIT_ID, today);

        verify(allClinicVisits).update(any(ClinicVisit.class));
        assertEquals(clinicVisit.getConfirmedVisitDate(), today);
    }

    @Test
    public void shouldMarkTheClinicVisitAsMissed() throws Exception {
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        when(allClinicVisits.get(CLINIC_VISIT_ID)).thenReturn(clinicVisit);
        clinicVisitService.markAsMissed(CLINIC_VISIT_ID);
        ArgumentCaptor<ClinicVisit> clinicVisitArgumentCaptor = ArgumentCaptor.forClass(ClinicVisit.class);
        verify(allClinicVisits).update(clinicVisitArgumentCaptor.capture());
        assertEquals(true, clinicVisitArgumentCaptor.getValue().isMissed());
    }

    private void verifyAppointment(String patientId, LocalDate activationDate, int offsetWeekNumber, Appointment appointment) {
        assertEquals(patientId, appointment.getExternalId());
        assertEquals(activationDate.plusWeeks(offsetWeekNumber), appointment.getDueDate().toLocalDate());
    }

    private void verifyReminder(String patientId, int week, Reminder reminder) {
        assertEquals(patientId, reminder.getExternalId());
        Assert.assertNotNull(reminder.getAppointmentId());
        Date expectedStartDate = DateUtil.newDate(today.plusWeeks(week).toDate()).minusDays(Integer.parseInt(REMIND_FROM_DAYS)).toDate();
        assertEquals(expectedStartDate, reminder.getStartDate());
        Date expectedEndDate = DateUtil.newDate(today.plusWeeks(week).toDate()).minusDays(Integer.parseInt(REMIND_TILL_DAYS)).toDate();
        assertEquals(expectedEndDate, reminder.getEndDate());
        assertEquals(1, reminder.getIntervalCount());
        assertEquals(Reminder.intervalUnits.DAYS, reminder.getUnits());
        assertTrue(reminder.getEnabled());
    }
}
