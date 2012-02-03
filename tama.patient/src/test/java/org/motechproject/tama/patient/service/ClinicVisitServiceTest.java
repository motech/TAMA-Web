package org.motechproject.tama.patient.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.ClinicVisitBuilder;
import org.motechproject.tama.patient.domain.ClinicVisit;
import org.motechproject.tama.patient.repository.AllClinicVisits;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicVisitServiceTest {

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

        clinicVisitService.createVisit(now, patientId, treatmentAdviceId, labResultIds, vitalStatisticsId);

        ArgumentCaptor<ClinicVisit> clinicVisitArgumentCaptor = ArgumentCaptor.forClass(ClinicVisit.class);
        verify(allClinicVisits).add(clinicVisitArgumentCaptor.capture());

        ClinicVisit clinicVisit = clinicVisitArgumentCaptor.getValue();
        assertEquals(patientId, clinicVisit.getPatientId());
        assertEquals(treatmentAdviceId, clinicVisit.getTreatmentAdviceId());
        assertEquals(labResultIds, clinicVisit.getLabResultIds());
        assertEquals(vitalStatisticsId, clinicVisit.getVitalStatisticsId());
        assertEquals(DateUtil.today(), clinicVisit.getVisitDate().toLocalDate());
    }

    @Test
    public void shouldReturnVisitZeroForPatient() {
        DateTime today = DateUtil.now();
        DateTime yesterday = today.minusDays(1);
        DateTime dayBefore = yesterday.minusDays(1);

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
        clinicVisitService.createExpectedVisit(DateUtil.now(), "patientId");
        verify(allClinicVisits).add(any(ClinicVisit.class));
    }

    @Test
    public void shouldChangeRegimenForAVisit() {
        final String clinicVisitId = "clinicVisitId";
        final String newTreatmentAdviceId = "newTreatmentAdviceId";
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();

        when(allClinicVisits.get(clinicVisitId)).thenReturn(clinicVisit);
        clinicVisitService.changeRegimen(clinicVisitId, newTreatmentAdviceId);

        verify(allClinicVisits).update(clinicVisit);
        assertEquals(newTreatmentAdviceId, clinicVisit.getTreatmentAdviceId());
    }
}
