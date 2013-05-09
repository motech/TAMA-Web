package org.motechproject.tama.web.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.refdata.domain.LabTest;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientDetailsServiceTest {

    public static final String PATIENT_ID = "patientId";

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;

    private PatientDetailsService patientDetailsService;

    @Before
    public void setup() {
        initMocks(this);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withStatus(Status.Active).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        patientDetailsService = new PatientDetailsService(allPatients, allTreatmentAdvices, allVitalStatistics, allClinicVisits, allLabResults);
    }

    @Test
    public void shouldSetPatientDetailsToCompleteWhenAllDataAreAvailable() {
        ClinicVisit clinicVisit = requiredClinicVisit();
        when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(new TreatmentAdvice());
        when(allLabResults.withIds(clinicVisit.getLabResultIds())).thenReturn(requiredLabResults());
        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(requiredVitalStatistics());

        patientDetailsService.update(PATIENT_ID);

        ArgumentCaptor<Patient> patientCapture = ArgumentCaptor.forClass(Patient.class);
        verify(allPatients).update(patientCapture.capture(), anyString());
        assertTrue(patientCapture.getValue().isComplete());
    }

    @Test
    public void shouldSetPatientDetailsToIncompleteWhenAllDataAreNotAvailable() {
        patientDetailsService.update(PATIENT_ID);

        ArgumentCaptor<Patient> patientCapture = ArgumentCaptor.forClass(Patient.class);
        verify(allPatients).update(patientCapture.capture(), anyString());
        assertFalse(patientCapture.getValue().isComplete());
    }

    private ClinicVisit requiredClinicVisit() {
        ClinicVisit clinicVisit = mock(ClinicVisit.class);
        when(clinicVisit.getLabResultIds()).thenReturn(asList("labresult1"));
        return clinicVisit;
    }

    private VitalStatistics requiredVitalStatistics() {
        VitalStatistics statistics = new VitalStatistics();
        statistics.setHeightInCm(10d);
        statistics.setWeightInKg(10d);
        return statistics;
    }

    private List<LabResult> requiredLabResults() {
        LabResult cd4Result = new LabResult();
        LabTest cd4Test = new LabTest();
        cd4Test.setName(TAMAConstants.LabTestType.CD4.getName());

        cd4Result.setLabTest(cd4Test);
        return asList(cd4Result);
    }
}
