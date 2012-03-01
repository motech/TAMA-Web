package org.motechproject.tama.clinicvisits.repository;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.tama.clinicvisits.mapper.AppointmentCalendarRequestBuilder;
import org.motechproject.tama.clinicvisits.mapper.VisitRequestBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllClinicVisitsTest extends BaseUnitTest {

    static final String PATIENT_ID = "patientId";
    private static final String REMIND_FOR_VISIT_FROM_DAYS = "2";

    protected Properties appointmentsProperties = new Properties();

    @Mock
    private AllPatients allPatients;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private AppointmentCalendarRequestBuilder appointmentCalendarRequestBuilder;
    @Mock
    private VisitRequestBuilder visitRequestBuilder;

    private AllClinicVisits allClinicVisits;

    @Before
    public void setUp() {
        initMocks(this);
        when(allPatients.get(PATIENT_ID)).thenReturn(PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build());
        appointmentsProperties.setProperty(AllClinicVisits.REMIND_FOR_VISIT_FROM, REMIND_FOR_VISIT_FROM_DAYS);
        allClinicVisits = new AllClinicVisits(allPatients, appointmentService, appointmentCalendarRequestBuilder, visitRequestBuilder, appointmentsProperties);
    }

    @Test
    public void shouldAddAppointmentCalendar() {
        allClinicVisits.addAppointmentCalendar(PATIENT_ID);
        verify(appointmentCalendarRequestBuilder).calendarForPatient(PATIENT_ID);
        verify(appointmentService).addCalendar(Matchers.<AppointmentCalendarRequest>any());
    }

    @Test
    public void shouldFirstRemoveAppointmentCalenderForPatient() {
        allClinicVisits.addAppointmentCalendar(PATIENT_ID);

        verify(appointmentService).removeCalendar(PATIENT_ID);
    }

    @Test
    public void shouldFindByPatientId() {
        final Visit visitForPatient = new Visit().name("visit2");
        final AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId(PATIENT_ID).addVisit(visitForPatient);

        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);

        ClinicVisits clinicVisits = allClinicVisits.clinicVisits(PATIENT_ID);
        assertEquals(1, clinicVisits.size());
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
        allClinicVisits.confirmVisitDate(PATIENT_ID, "visit2", today);
        verify(appointmentService).confirmVisit(eq(PATIENT_ID), eq("visit2"), eq(today), Matchers.<ReminderConfiguration>any());
    }

    @Test
    public void shouldCloseVisit() {
        final DateTime today = DateUtil.now();
        allClinicVisits.closeVisit(PATIENT_ID, "visit2", today);
        verify(appointmentService).setVisitDate(eq(PATIENT_ID), eq("visit2"), eq(today));
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
    public void shouldCreateUnscheduledVisit() {
        DateTime dueDate = DateUtil.now();
        VisitResponse visitResponse = new VisitResponse(new Visit().name("visitName"));

        when(appointmentService.addVisit(eq(PATIENT_ID),
                eq("visitFor-" + dueDate.getMillis()),
                Matchers.<VisitRequest>any())).thenReturn(visitResponse);

        assertEquals("visitname", allClinicVisits.createUnscheduledVisit(PATIENT_ID, dueDate, TypeOfVisit.Unscheduled));
    }

    @Test
    public void shouldCreateUniqueVisitNameForUnscheduledAppointment() {
        DateTime dueDate = DateUtil.now();

        allClinicVisits.createUnScheduledAppointment(PATIENT_ID, dueDate, TypeOfVisit.Unscheduled);
        verify(appointmentService).addVisit(eq(PATIENT_ID), eq("visitFor-" + dueDate.getMillis()), Matchers.<VisitRequest>any());
    }

    @Test
    public void shouldMakeVisitRequestForUnscheduledAppointment() {
        DateTime dueDate = DateUtil.now();
        VisitRequest visitRequest = mock(VisitRequest.class);

        when(visitRequestBuilder.visitWithReminderRequest(dueDate, TypeOfVisit.Unscheduled)).thenReturn(visitRequest);
        allClinicVisits.createUnScheduledAppointment(PATIENT_ID, dueDate, TypeOfVisit.Unscheduled);
        verify(appointmentService).addVisit(eq(PATIENT_ID), anyString(), same(visitRequest));
    }

    @Test
    public void shouldUpdateVisitData() {
        AppointmentCalendar appointmentCalendar = mock(AppointmentCalendar.class);
        Visit visit = mock(Visit.class);
        when(appointmentService.getAppointmentCalendar(PATIENT_ID)).thenReturn(appointmentCalendar);
        when(appointmentCalendar.getVisit("visitId")).thenReturn(visit);
        String oppInfectionsId = "oppInfectionsId";
        String treatmentAdviceId = "treatmentAdviceId";
        String vitalStatsId = "vitalStatsId";
        DateTime visitDate = DateUtil.now();
        List<String> labResultIds = new ArrayList<String>();
        allClinicVisits.updateVisit("visitId", visitDate, PATIENT_ID, treatmentAdviceId, labResultIds, vitalStatsId, oppInfectionsId);
        verify(visit).addData(ClinicVisit.REPORTED_OPPORTUNISTIC_INFECTIONS, oppInfectionsId);
        verify(visit).addData(ClinicVisit.TREATMENT_ADVICE, treatmentAdviceId);
        verify(visit).addData(ClinicVisit.VITAL_STATISTICS, vitalStatsId);
        verify(visit).addData(ClinicVisit.LAB_RESULTS, labResultIds);
        verify(visit).visitDate(visitDate);
        verify(appointmentService).updateVisit(visit, PATIENT_ID);

    }
}
