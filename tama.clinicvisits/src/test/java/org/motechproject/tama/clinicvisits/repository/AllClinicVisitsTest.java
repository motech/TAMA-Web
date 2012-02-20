package org.motechproject.tama.clinicvisits.repository;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllClinicVisitsTest extends BaseUnitTest {

    static final String PATIENT_ID = "patientId";

    static int FIRST_SCHEDULE_WEEK = 1;
    static int SECOND_SCHEDULE_WEEK = 2;
    private static final String REMIND_FROM_DAYS = "10";
    private static final String REMIND_TILL_DAYS = "5";
    protected Properties appointmentsTemplate;
    protected DateTime now;
    protected LocalDate today;

    @Mock
    private AppointmentService appointmentService;

    private AllClinicVisits allClinicVisits;

    @Before
    public void setUp() {
        initMocks(this);
        setUpTime();
        setUpAppointmentsTemplate();
        allClinicVisits = new AllClinicVisits(appointmentService, appointmentsTemplate);
    }

    private void setUpTime() {
        now = DateUtil.now();
        today = now.toLocalDate();
        mockCurrentDate(now);
    }

    private void setUpAppointmentsTemplate() {
        appointmentsTemplate = new Properties();
        appointmentsTemplate.setProperty(AllClinicVisits.APPOINTMENT_SCHEDULE, FIRST_SCHEDULE_WEEK + "," + SECOND_SCHEDULE_WEEK);
        appointmentsTemplate.setProperty(AllClinicVisits.REMIND_FROM, REMIND_FROM_DAYS);
        appointmentsTemplate.setProperty(AllClinicVisits.REMIND_TILL, REMIND_TILL_DAYS);
    }

    @Test
    public void shouldCreateVisits() {
        DateTime activationTime = now;
        LocalDate activationDate = activationTime.toLocalDate();
        ArgumentCaptor<Visit> visitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);

        allClinicVisits.scheduleVisits(PATIENT_ID);
        verify(appointmentService, times(3)).addVisit(visitArgumentCaptor.capture(), eq(PATIENT_ID));
        verifyAppointment(activationDate, 0, visitArgumentCaptor.getAllValues().get(0).appointment());
        verifyAppointment(activationDate, FIRST_SCHEDULE_WEEK, visitArgumentCaptor.getAllValues().get(1).appointment());
        verifyAppointment(activationDate, SECOND_SCHEDULE_WEEK, visitArgumentCaptor.getAllValues().get(2).appointment());
    }

    @Test
    public void shouldNotScheduleAppointmentRemindersForBaselineVisit() {
        allClinicVisits.scheduleVisits("patientId");
        ArgumentCaptor<Visit> visitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(appointmentService, times(3)).addVisit(visitArgumentCaptor.capture(), eq("patientId"));
        assertNull(visitArgumentCaptor.getAllValues().get(0).appointment().reminder());
        assertNotNull(visitArgumentCaptor.getAllValues().get(1).appointment().reminder());
        assertNotNull(visitArgumentCaptor.getAllValues().get(2).appointment().reminder());
    }

    @Test
    public void shouldCreateExpectedClinicVisit() {
        DateTime activationTime = now;
        ArgumentCaptor<Visit> visitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);

        allClinicVisits.scheduleVisits(PATIENT_ID);
        verify(appointmentService, times(3)).addVisit(visitArgumentCaptor.capture(), eq(PATIENT_ID));
        Assert.assertEquals(activationTime, visitArgumentCaptor.getAllValues().get(0).appointment().dueDate());
    }

    @Test
    public void shouldCreateExpectedVisit() {
        allClinicVisits.addVisit("patientId", DateUtil.now(), 0);
        verify(appointmentService).addVisit(any(Visit.class), any(String.class));
    }

    @Test
    public void shouldFindByPatientId() {
        final String patientId = "patientId";
        final Visit visitForPatient = new Visit();
        final AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(patientId).addVisit(visitForPatient);

        when(appointmentService.getAppointmentCalendar(patientId)).thenReturn(appointmentCalendar);

        ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patientId);
        assertEquals(1, clinicVisits.size());
    }

    @Test
    public void shouldGetBaselineVisit() {
        final String patientId = "patientId";
        final Visit visitForPatient = new Visit() {{
            addData(ClinicVisit.TYPE_OF_VISIT, ClinicVisit.TypeOfVisit.Baseline.toString());
        }};
        final AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(patientId).addVisit(visitForPatient);

        when(appointmentService.getAppointmentCalendar(patientId)).thenReturn(appointmentCalendar);

        ClinicVisit clinicVisit = allClinicVisits.getBaselineVisit(patientId);
        assertEquals(ClinicVisit.TypeOfVisit.Baseline, clinicVisit.getTypeOfVisit());
    }

    @Test
    public void shouldChangeRegimenForAVisit() {
        final String newTreatmentAdviceId = "newTreatmentAdviceId";

        Visit visit = new Visit();
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit);

        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);
        allClinicVisits.changeRegimen(PATIENT_ID, visit.id(), newTreatmentAdviceId);

        verify(appointmentService).updateVisit(visit, PATIENT_ID);
        Assert.assertEquals(newTreatmentAdviceId, visit.getData().get(ClinicVisit.TREATMENT_ADVICE));
    }

    @Test
    public void shouldAdjustDueDate() throws Exception {
        final LocalDate today = DateUtil.today();

        Visit visit = new Visit().appointment(new Appointment());
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit);
        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);

        allClinicVisits.adjustDueDate(PATIENT_ID, visit.id(), today);

        Assert.assertEquals(today, visit.appointment().getData().get(ClinicVisit.ADJUSTED_DUE_DATE));
        verify(appointmentService).updateVisit(any(Visit.class), eq(PATIENT_ID));
    }

    @Test
    public void shouldUpdateConfirmedDueDate() throws Exception {
        final DateTime today = DateUtil.now();
        Appointment appointment = new Appointment().scheduledDate(today);
        Visit visit = new Visit().appointment(appointment);
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit);
        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);

        allClinicVisits.confirmVisitDate(PATIENT_ID, visit.id(), today);

        verify(appointmentService).updateVisit(any(Visit.class), eq(PATIENT_ID));
    }

    @Test
    public void shouldMarkTheClinicVisitAsMissed() throws Exception {
        Visit visit = new Visit();
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit);

        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);

        allClinicVisits.markAsMissed(PATIENT_ID, visit.id());
        ArgumentCaptor<Visit> visitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(appointmentService).updateVisit(visitArgumentCaptor.capture(), eq(PATIENT_ID));
        Assert.assertEquals(true, visitArgumentCaptor.getValue().missed());
    }

    private void verifyAppointment(LocalDate activationDate, int offsetWeekNumber, Appointment appointment) {
        assertEquals(activationDate.plusWeeks(offsetWeekNumber), appointment.dueDate().toLocalDate());
    }
}
