package org.motechproject.tama.appointments.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.appointments.domain.ClinicVisit;
import org.motechproject.tama.appointments.service.ClinicVisitService;
import org.motechproject.tama.appointments.builder.ClinicVisitBuilder;
import org.motechproject.tama.appointments.integration.repository.AllClinicVisits;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicVisitServiceTest {

    public static final String CLINIC_VISIT_ID = "clinicVisitId";

    @Mock
    private AllClinicVisits allClinicVisits;

    private ClinicVisitService clinicVisitService;

    @Before
    public void setUp() {
        initMocks(this);
        clinicVisitService = new ClinicVisitService(allClinicVisits);
    }

    @Test
    public void shouldCreateVisit() {
        String patientId = "patientId";
        String treatmentAdviceId = "treatmentAdviceId";
        List<String> labResultIds = Arrays.asList("labResultId");
        String vitalStatisticsId = "vitalStatisticsId";
        DateTime now = DateUtil.now();

        clinicVisitService.createOrUpdateVisit(null, now.toLocalDate(), patientId, treatmentAdviceId, labResultIds, vitalStatisticsId);

        ArgumentCaptor<ClinicVisit> clinicVisitArgumentCaptor = ArgumentCaptor.forClass(ClinicVisit.class);
        verify(allClinicVisits).add(clinicVisitArgumentCaptor.capture());

        ClinicVisit clinicVisit = clinicVisitArgumentCaptor.getValue();
        assertEquals(patientId, clinicVisit.getPatientId());
        assertEquals(treatmentAdviceId, clinicVisit.getTreatmentAdviceId());
        assertEquals(labResultIds, clinicVisit.getLabResultIds());
        assertEquals(vitalStatisticsId, clinicVisit.getVitalStatisticsId());
        assertEquals(DateUtil.today(), clinicVisit.getVisitDate());
    }

    @Test
    public void shouldReturnVisitZeroForPatient() {
        LocalDate today = DateUtil.today();
        LocalDate yesterday = today.minusDays(1);
        LocalDate dayBefore = yesterday.minusDays(1);

        final ClinicVisit visit0 = ClinicVisitBuilder.startRecording().withDefaults().withPatientId("pid").withVisitDate(dayBefore).build();
        final ClinicVisit visit1 = ClinicVisitBuilder.startRecording().withDefaults().withPatientId("pid").withVisitDate(yesterday).build();
        final ClinicVisit visit2 = ClinicVisitBuilder.startRecording().withDefaults().withPatientId("pid").withVisitDate(today).build();

        final List<ClinicVisit> clinicVisits = Arrays.asList(visit0, visit1, visit2);

        when(allClinicVisits.find_by_patient_id("pid")).thenReturn(clinicVisits);

        final ClinicVisit visitZero = clinicVisitService.visitZero("pid");
        assertNotNull(visitZero);
        assertEquals(visit0, visitZero);
    }

    @Test
    public void shouldCreateExpectedVisit() {
        clinicVisitService.createExpectedVisit(DateUtil.now(),0, "patientId");
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
        List<ClinicVisit> visitsInDb = Arrays.asList(ClinicVisit.createExpectedVisit(DateUtil.now(), 0, patientId));
        when(allClinicVisits.find_by_patient_id(patientId)).thenReturn(visitsInDb);
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
}
