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
import static junit.framework.Assert.assertNull;
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
    public void shouldBeNullIfPatientIsInActive() {
        when(allVitalStatistics.findByPatientId("patientId")).thenReturn(mock(VitalStatistics.class));
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(mock(TreatmentAdvice.class));
        when(allLabResults.findByPatientId("patientId")).thenReturn(new LabResults());

        patient.setStatus(Status.Inactive);

        assertNull(incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingVitalStats() {
        when(allVitalStatistics.findByPatientId("patientId")).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(mock(TreatmentAdvice.class));
        when(allLabResults.findByPatientId("patientId")).thenReturn(mock(LabResults.class));
        assertEquals("Please fill in the Vital Statistics for the patient", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingLabTestResults() {
        when(allVitalStatistics.findByPatientId("patientId")).thenReturn(mock(VitalStatistics.class));
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(mock(TreatmentAdvice.class));
        when(allLabResults.findByPatientId("patientId")).thenReturn(new LabResults());
        assertEquals("Please fill in the Lab Results for the patient", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingTreatmentAdviceAndLabResults() {
        when(allVitalStatistics.findByPatientId("patientId")).thenReturn(mock(VitalStatistics.class));
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(null);
        when(allLabResults.findByPatientId("patientId")).thenReturn(new LabResults());
        assertEquals("Please fill in the current Treatment Advice, Lab Results for the patient", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingVitalStatisticsAndTreatmentAdviceAndLabTestResults() {
        when(allVitalStatistics.findByPatientId("patientId")).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(null);
        when(allLabResults.findByPatientId("patientId")).thenReturn(new LabResults());
        assertEquals("Please fill in the Vital Statistics, current Treatment Advice, Lab Results for the patient", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldNotWarnAboutAnythingIfVitalStatisticsAndTreatmentAdviceAndLabTestResultsArePresent() {
        when(allVitalStatistics.findByPatientId("patientId")).thenReturn(mock(VitalStatistics.class));
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(mock(TreatmentAdvice.class));
        when(allLabResults.findByPatientId("patientId")).thenReturn(mock(LabResults.class));
        assertEquals(null, incompletePatientDataWarning.toString());
    }

}
