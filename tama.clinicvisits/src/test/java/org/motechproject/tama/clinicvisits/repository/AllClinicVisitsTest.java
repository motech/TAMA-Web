package org.motechproject.tama.clinicvisits.repository;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllClinicVisitsTest extends BaseUnitTest {

    static final String PATIENT_ID = "patientId";

    static int FIRST_SCHEDULE_WEEK = 1;
    static int SECOND_SCHEDULE_WEEK = 2;
    private static final String REMIND_FROM_DAYS = "10";
    protected Properties appointmentsTemplate;
    protected DateTime now;
    protected LocalDate today;

    @Mock
    private AllPatients allPatients;
    @Mock
    private AppointmentService appointmentService;

    private AllClinicVisits allClinicVisits;

    @Before
    public void setUp() {
        initMocks(this);
        setUpTime();
        setUpAppointmentsTemplate();
        when(allPatients.get(PATIENT_ID)).thenReturn(PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build());
        allClinicVisits = new AllClinicVisits(allPatients, appointmentService, appointmentsTemplate);
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
    }

    @Test
    public void shouldAddAppointmentCalendar() {
        allClinicVisits.addAppointmentCalendar(PATIENT_ID);

        ArgumentCaptor<AppointmentCalendarRequest> requestArgumentCaptor = ArgumentCaptor.forClass(AppointmentCalendarRequest.class);
        verify(appointmentService).addCalendar(requestArgumentCaptor.capture());
        assertEquals(PATIENT_ID, requestArgumentCaptor.getValue().getExternalId());
        assertEquals(Arrays.asList(1, 2), requestArgumentCaptor.getValue().getWeekOffsets());

        ReminderConfiguration reminderConfiguration = requestArgumentCaptor.getValue().getReminderConfiguration();
        assertEquals(10, reminderConfiguration.getRemindFrom());
        assertEquals(1, reminderConfiguration.getIntervalCount());
        assertEquals(ReminderConfiguration.IntervalUnit.DAYS, reminderConfiguration.getIntervalUnit());
        assertEquals(10, reminderConfiguration.getRepeatCount());
    }

    @Test
    public void shouldFirstRemoveAppointmentCalenderForPatient(){
        allClinicVisits.addAppointmentCalendar(PATIENT_ID);

        verify(appointmentService).removeCalendar(PATIENT_ID);
    }

    @Test
    public void shouldFindByPatientId() {
        final String patientId = "patientId";
        final Visit visitForPatient = new Visit().name("visit2");
        final AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(patientId).addVisit(visitForPatient);

        when(appointmentService.getAppointmentCalendar(patientId)).thenReturn(appointmentCalendar);

        ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patientId);
        assertEquals(2, clinicVisits.size());
    }

    @Test
    public void shouldChangeRegimenForAVisit() {
        final String newTreatmentAdviceId = "newTreatmentAdviceId";

        Visit visit = new Visit().name("visit2");
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit);

        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);
        allClinicVisits.changeRegimen(PATIENT_ID, visit.name(), newTreatmentAdviceId);

        verify(appointmentService).updateVisit(visit, PATIENT_ID);
        Assert.assertEquals(newTreatmentAdviceId, visit.getData().get(ClinicVisit.TREATMENT_ADVICE));
    }

    @Test
    public void shouldAdjustDueDate() throws Exception {
        final DateTime today = DateUtil.now();

        Visit visit = new Visit().name("visit2").addAppointment(today, null);
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit);
        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);

        allClinicVisits.adjustDueDate(PATIENT_ID, visit.name(), today.toLocalDate());

        Assert.assertEquals(today.toLocalDate(), visit.appointment().getData().get(ClinicVisit.ADJUSTED_DUE_DATE));
        verify(appointmentService).updateVisit(any(Visit.class), eq(PATIENT_ID));
    }

    @Test
    public void shouldUpdateConfirmedDueDate() throws Exception {
        final DateTime today = DateUtil.now();
        Visit visit = new Visit().name("visit2").addAppointment(today, null);
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit);
        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);

        allClinicVisits.confirmVisitDate(PATIENT_ID, visit.name(), today);

        verify(appointmentService).updateVisit(any(Visit.class), eq(PATIENT_ID));
    }

    @Test
    public void shouldMarkTheClinicVisitAsMissed() throws Exception {
        Visit visit = new Visit().name("visit2");
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().addVisit(visit);

        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);

        allClinicVisits.markAsMissed(PATIENT_ID, visit.name());
        ArgumentCaptor<Visit> visitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(appointmentService).updateVisit(visitArgumentCaptor.capture(), eq(PATIENT_ID));
        Assert.assertEquals(true, visitArgumentCaptor.getValue().missed());
    }

    @Test
    public void shouldCreateAdhocAppointment() throws Exception {
        final String patientId = "patientId";

        allClinicVisits.createAppointment(patientId, now, TypeOfVisit.Unscheduled);

        verify(appointmentService).addVisit(eq(patientId), eq(now), Matchers.<ReminderConfiguration>any(), eq(TypeOfVisit.Unscheduled));
    }
}
