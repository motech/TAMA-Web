package org.motechproject.tama.web.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.VitalStatisticsBuilder;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class IncompletePatientDataWarningTest {

    private IncompletePatientDataWarning incompletePatientDataWarning;
    private Patient patient;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllLabResults allLabResults;

    @Before
    public void setUp() {
        initMocks(this);
        patient = PatientBuilder.startRecording().withId("patientId").withStatus(Status.Active).build();
        incompletePatientDataWarning = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults);
    }

    @Test
    public void shouldShowWarningIfPatientIsInActive() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(validVitalStatistics());
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        when(allLabResults.allLabResults("patientId")).thenReturn(new LabResults());

        patient.setStatus(Status.Inactive);

        assertEquals("Patient has not been Activated", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingVitalStats() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        when(allLabResults.allLabResults("patientId")).thenReturn(validLabResults());
        assertEquals("The Vital Statistics(Height, Weight) need to be filled so that the patient can access Symptoms Reporting", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingVitalStats_ifWeightAndHeightIsMissing() {
        VitalStatistics vitalStatsWithoutWeightAndHeight = new VitalStatistics();
        vitalStatsWithoutWeightAndHeight.setPulse(80);
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(vitalStatsWithoutWeightAndHeight);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        when(allLabResults.allLabResults("patientId")).thenReturn(validLabResults());
        assertEquals("The Vital Statistics(Height, Weight) need to be filled so that the patient can access Symptoms Reporting", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingLabTestResults_IfCd4CountNotAvailable() {
        LabResult pvlLabResult = LabResultBuilder.defaultPVLResult().build();
        LabResults labResultsWithoutCd4Result = new LabResults();
        labResultsWithoutCd4Result.add(pvlLabResult);
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(validVitalStatistics());
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        when(allLabResults.allLabResults("patientId")).thenReturn(labResultsWithoutCd4Result);
        assertEquals("The Lab Results(CD4 count) need to be filled so that the patient can access Symptoms Reporting and Health Tips", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingTreatmentAdviceAndLabResults() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(validVitalStatistics());
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(null);
        when(allLabResults.allLabResults("patientId")).thenReturn(new LabResults());
        assertEquals("The Regimen details, Lab Results(CD4 count) need to be filled so that the patient can access Symptoms Reporting and Health Tips", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingVitalStatisticsAndTreatmentAdviceAndLabTestResults() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(null);
        when(allLabResults.allLabResults("patientId")).thenReturn(new LabResults());
        assertEquals("The Vital Statistics(Height, Weight), Regimen details, Lab Results(CD4 count) need to be filled so that the patient can access Symptoms Reporting and Health Tips", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldNotWarnAboutAnythingIfVitalStatisticsAndTreatmentAdviceAndLabTestResultsArePresent() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(validVitalStatistics());
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        when(allLabResults.allLabResults("patientId")).thenReturn(validLabResults());
        assertEquals(null, incompletePatientDataWarning.toString());
    }

    private TreatmentAdvice validTreatmentAdvice() {
        return mock(TreatmentAdvice.class);
    }

    private LabResults validLabResults() {
        LabResults labResults = new LabResults();
        labResults.add(LabResultBuilder.defaultCD4Result().build());
        return labResults;
    }

    private VitalStatistics validVitalStatistics() {
        return VitalStatisticsBuilder.startRecording().withDefaults().build();
    }
}
