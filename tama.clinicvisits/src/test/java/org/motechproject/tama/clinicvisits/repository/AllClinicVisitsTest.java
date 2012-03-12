package org.motechproject.tama.clinicvisits.repository;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.*;
import org.motechproject.tama.clinicvisits.builder.servicecontract.AppointmentCalendarRequestBuilder;
import org.motechproject.tama.clinicvisits.builder.servicecontract.ConfirmAppointmentRequestBuilder;
import org.motechproject.tama.clinicvisits.builder.servicecontract.CreateVisitRequestBuilder;
import org.motechproject.tama.clinicvisits.builder.servicecontract.RescheduleAppointmentRequestBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllClinicVisitsTest extends BaseUnitTest {

    static final String PATIENT_ID = "patientId";

    protected Properties appointmentsProperties = new Properties();

    @Mock
    private AllPatients allPatients;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private AppointmentCalendarRequestBuilder appointmentCalendarRequestBuilder;
    @Mock
    private CreateVisitRequestBuilder createVisitRequestBuilder;
    @Mock
    private ConfirmAppointmentRequestBuilder confirmAppointmentRequestBuilder;
    @Mock
    private RescheduleAppointmentRequestBuilder rescheduleAppointmentRequestBuilder;
    
    private AllClinicVisits allClinicVisits;

    @Before
    public void setUp() {
        initMocks(this);
        when(allPatients.get(PATIENT_ID)).thenReturn(PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build());
        allClinicVisits = new AllClinicVisits(allPatients, appointmentService, appointmentCalendarRequestBuilder, createVisitRequestBuilder, confirmAppointmentRequestBuilder, rescheduleAppointmentRequestBuilder);
    }

    @Test
    public void shouldAddAppointmentCalendar() {
        allClinicVisits.addAppointmentCalendar(PATIENT_ID);
        verify(appointmentCalendarRequestBuilder).calendarForPatient(PATIENT_ID);
        verify(appointmentService).addCalendar(Matchers.<AppointmentCalendarRequest>any());
    }

    @Test
    public void shouldFirstRemoveAppointmentCalendarForPatient() {
        allClinicVisits.addAppointmentCalendar(PATIENT_ID);

        verify(appointmentService).removeCalendar(PATIENT_ID);
    }

    @Test
    public void shouldFindByPatientId() {
        final ArrayList<VisitResponse> visitResponses = new ArrayList<VisitResponse>() {{
            add(new VisitResponse().setName("visit2"));
        }};
        when(appointmentService.getAllVisits(PATIENT_ID)).thenReturn(visitResponses);

        ClinicVisits clinicVisits = allClinicVisits.clinicVisits(PATIENT_ID);
        assertEquals(1, clinicVisits.size());
    }

    @Test
    public void shouldChangeRegimenForAVisit() {
        final String newTreatmentAdviceId = "newTreatmentAdviceId";

        final ArrayList<VisitResponse> visitResponses = new ArrayList<VisitResponse>() {{
            add(new VisitResponse().setName("visit2"));
        }};
        when(appointmentService.getAllVisits(PATIENT_ID)).thenReturn(visitResponses);
        allClinicVisits.changeRegimen(PATIENT_ID, "visit2", newTreatmentAdviceId);

        ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(appointmentService).addCustomDataToVisit(eq(PATIENT_ID), eq("visit2"), mapArgumentCaptor.capture());
        Assert.assertEquals(newTreatmentAdviceId, mapArgumentCaptor.getValue().get(ClinicVisit.TREATMENT_ADVICE));
    }

    @Test
    public void shouldAdjustDueDate_ChangesDate_AndSetsTimeToMidnight() throws Exception {
        final DateTime today = DateUtil.now();
        RescheduleAppointmentRequest rescheduleAppointmentRequest = new RescheduleAppointmentRequest();
        when(rescheduleAppointmentRequestBuilder.create(PATIENT_ID, "visit2", today.toLocalDate())).thenReturn(rescheduleAppointmentRequest);

        allClinicVisits.adjustDueDate(PATIENT_ID, "visit2", today.toLocalDate());
        verify(appointmentService).rescheduleAppointment(rescheduleAppointmentRequest);
    }

    @Test
    public void shouldUpdateConfirmedDueDate() throws Exception {
        final DateTime today = DateUtil.now();
        final ConfirmAppointmentRequest request = new ConfirmAppointmentRequest();

        when(confirmAppointmentRequestBuilder.confirmAppointmentRequest(PATIENT_ID, "visit2", today)).thenReturn(request);
        allClinicVisits.confirmAppointmentDate(PATIENT_ID, "visit2", today);
        verify(appointmentService).confirmAppointment(request);
    }

    @Test
    public void shouldCloseVisit() {
        final DateTime today = DateUtil.now();
        allClinicVisits.closeVisit(PATIENT_ID, "visit2", today);
        verify(appointmentService).visited(eq(PATIENT_ID), eq("visit2"), eq(today));
    }

    @Test
    public void shouldMarkTheClinicVisitAsMissed() throws Exception {
        String visitName = "visit";
        allClinicVisits.markAsMissed(PATIENT_ID, visitName);
        verify(appointmentService).markVisitAsMissed(eq(PATIENT_ID), eq(visitName));
    }

    @Test
    public void shouldCreateUnscheduledVisit() {
        DateTime dueDate = DateUtil.now();
        VisitResponse visitResponse = new VisitResponse().setName("visitName");

        when(appointmentService.addVisit(eq(PATIENT_ID), Matchers.<CreateVisitRequest>any())).thenReturn(visitResponse);
        assertEquals("visitName", allClinicVisits.createUnscheduledVisit(PATIENT_ID, dueDate, TypeOfVisit.Unscheduled));
    }

    @Test
    public void shouldCreateUniqueVisitNameForUnscheduledAppointment() {
        DateTime dueDate = DateUtil.now();

        allClinicVisits.createUnScheduledAppointment(PATIENT_ID, dueDate, TypeOfVisit.Unscheduled);
        verify(appointmentService).addVisit(eq(PATIENT_ID), Matchers.<CreateVisitRequest>any());
    }

    @Test
    public void shouldMakeVisitRequestForUnscheduledAppointment() {
        DateTime dueDate = DateUtil.now();
        CreateVisitRequest createVisitRequest = mock(CreateVisitRequest.class);

        when(createVisitRequestBuilder.adHocVisitRequest(Matchers.<String>any(), eq(TypeOfVisit.Unscheduled), eq(dueDate))).thenReturn(createVisitRequest);
        allClinicVisits.createUnScheduledAppointment(PATIENT_ID, dueDate, TypeOfVisit.Unscheduled);
        verify(appointmentService).addVisit(eq(PATIENT_ID), same(createVisitRequest));
    }

    @Test
    public void shouldUpdateVisitData() {
        String visitId = "visitId";
        String oppInfectionsId = "oppInfectionsId";
        String treatmentAdviceId = "treatmentAdviceId";
        String vitalStatsId = "vitalStatsId";
        DateTime visitDate = DateUtil.now();

        List<String> labResultIds = new ArrayList<String>();
        allClinicVisits.updateVisitDetails(visitId, visitDate, PATIENT_ID, treatmentAdviceId, labResultIds, vitalStatsId, oppInfectionsId);

        ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(appointmentService).visited(PATIENT_ID, visitId, visitDate);
        verify(appointmentService).addCustomDataToVisit(eq(PATIENT_ID), eq("visitId"), mapArgumentCaptor.capture());
        Assert.assertEquals(treatmentAdviceId, mapArgumentCaptor.getValue().get(ClinicVisit.TREATMENT_ADVICE));
        Assert.assertEquals(vitalStatsId, mapArgumentCaptor.getValue().get(ClinicVisit.VITAL_STATISTICS));
        Assert.assertEquals(labResultIds, mapArgumentCaptor.getValue().get(ClinicVisit.LAB_RESULTS));
        Assert.assertEquals(oppInfectionsId, mapArgumentCaptor.getValue().get(ClinicVisit.REPORTED_OPPORTUNISTIC_INFECTIONS));
    }
}
