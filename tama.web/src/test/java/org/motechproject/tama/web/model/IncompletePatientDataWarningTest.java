package org.motechproject.tama.web.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
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
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(mock(VitalStatistics.class));
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(mock(TreatmentAdvice.class));
        when(allLabResults.findLatestLabResultsByPatientId("patientId")).thenReturn(new LabResults());

        patient.setStatus(Status.Inactive);

        assertEquals("Patient has not been Activated", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingVitalStats() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(mock(TreatmentAdvice.class));
        when(allLabResults.findLatestLabResultsByPatientId("patientId")).thenReturn(mock(LabResults.class));
        assertEquals("The Vital Statistics need to be filled so that the patient can access Symptoms Reporting and Health Tips", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingLabTestResults() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(mock(VitalStatistics.class));
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(mock(TreatmentAdvice.class));
        when(allLabResults.findLatestLabResultsByPatientId("patientId")).thenReturn(new LabResults());
        assertEquals("The Lab Results need to be filled so that the patient can access Symptoms Reporting and Health Tips", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingTreatmentAdviceAndLabResults() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(mock(VitalStatistics.class));
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(null);
        when(allLabResults.findLatestLabResultsByPatientId("patientId")).thenReturn(new LabResults());
        assertEquals("The Regimen details, Lab Results need to be filled so that the patient can access Symptoms Reporting and Health Tips", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingVitalStatisticsAndTreatmentAdviceAndLabTestResults() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(null);
        when(allLabResults.findLatestLabResultsByPatientId("patientId")).thenReturn(new LabResults());
        assertEquals("The Vital Statistics, Regimen details, Lab Results need to be filled so that the patient can access Symptoms Reporting and Health Tips", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldNotWarnAboutAnythingIfVitalStatisticsAndTreatmentAdviceAndLabTestResultsArePresent() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(mock(VitalStatistics.class));
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(mock(TreatmentAdvice.class));
        when(allLabResults.findLatestLabResultsByPatientId("patientId")).thenReturn(mock(LabResults.class));
        assertEquals(null, incompletePatientDataWarning.toString());
    }

}
