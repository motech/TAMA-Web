package org.motechproject.tama.web.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.VitalStatisticsBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class IncompletePatientDataWarningTest {

    public static final String CD4_RESULT_ID = "cd4ResultId";
    private IncompletePatientDataWarning incompletePatientDataWarning;
    private Patient patient;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllClinicVisits allClinicVisits;

    @Before
    public void setUp() {
        initMocks(this);
        patient = PatientBuilder.startRecording().withId("patientId").withStatus(Status.Active).build();
        incompletePatientDataWarning = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits);
    }

    @Test
    public void shouldShowWarningIfPatientIsInActive() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(validVitalStatistics());
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        when(allLabResults.withIds(any(List.class))).thenReturn(Collections.<LabResult>emptyList());
        when(allLabResults.allLabResults(patient.getId())).thenReturn(new LabResults());
        when(allClinicVisits.getBaselineVisit(patient.getId())).thenReturn(clinicVisit(false));

        patient.setStatus(Status.Inactive);

        assertEquals("Patient has not been Activated", incompletePatientDataWarning.toString());
    }

    @Test
    public void shouldWarnAboutMissingVitalStats() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        setupValidResult();

        assertTrue(incompletePatientDataWarning.toString().contains("The Vital Statistics(Height, Weight) need to be filled so that the patient can access Symptoms Reporting"));
    }

    @Test
    public void shouldWarnAboutMissingVitalStats_ifWeightAndHeightIsMissing() {
        VitalStatistics vitalStatsWithoutWeightAndHeight = new VitalStatistics();
        vitalStatsWithoutWeightAndHeight.setPulse(80);
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(vitalStatsWithoutWeightAndHeight);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        setupValidResult();

        assertTrue(incompletePatientDataWarning.toString().contains("The Vital Statistics(Height, Weight) need to be filled so that the patient can access Symptoms Reporting"));
    }

    @Test
    public void shouldWarnAboutMissingLabTestResults_IfCd4CountNotAvailable() {
        ClinicVisit clinicVisit = clinicVisit(true);
        LabResult pvlLabResult = LabResultBuilder.defaultPVLResult().withId(clinicVisit.getLabResultIds().get(0)).build();

        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(validVitalStatistics());
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        when(allLabResults.allLabResults(patient.getId())).thenReturn(new LabResults());
        when(allClinicVisits.getBaselineVisit(patient.getId())).thenReturn(clinicVisit);
        when(allLabResults.withIds(asList(pvlLabResult.getId()))).thenReturn(asList(pvlLabResult));

        assertTrue(incompletePatientDataWarning.toString().contains("Baseline CD4 count need to be filled so that the patient can access Symptoms Reporting"));
    }

    @Test
    public void shouldWarnAboutMissingTreatmentAdviceAndLabResults() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(validVitalStatistics());
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(null);
        when(allLabResults.withIds(any(List.class))).thenReturn(Collections.<LabResult>emptyList());
        when(allLabResults.allLabResults(patient.getId())).thenReturn(new LabResults());
        when(allClinicVisits.getBaselineVisit(patient.getId())).thenReturn(clinicVisit(false));

        assertTrue(incompletePatientDataWarning.toString().contains("The Regimen details need to be filled so that the patient can access Symptoms Reporting and Health Tips"));
        assertTrue(incompletePatientDataWarning.toString().contains("Baseline CD4 count need to be filled so that the patient can access Symptoms Reporting"));
    }

    @Test
    public void shouldWarnAboutMissingVitalStatisticsAndTreatmentAdviceAndLabTestResults() {
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(null);
        when(allLabResults.withIds(any(List.class))).thenReturn(Collections.<LabResult>emptyList());
        when(allLabResults.allLabResults(patient.getId())).thenReturn(new LabResults());
        when(allClinicVisits.getBaselineVisit(patient.getId())).thenReturn(clinicVisit(false));

        assertTrue(incompletePatientDataWarning.toString().contains("The Regimen details need to be filled so that the patient can access Symptoms Reporting and Health Tips"));
        assertTrue(incompletePatientDataWarning.toString().contains("Baseline CD4 count need to be filled so that the patient can access Symptoms Reporting"));
        assertTrue(incompletePatientDataWarning.toString().contains("The Vital Statistics(Height, Weight) need to be filled so that the patient can access Symptoms Reporting"));
    }

    @Test
    public void shouldNotWarnAboutAnythingIfVitalStatisticsAndTreatmentAdviceAndLabTestResultsArePresent() {
        setupValidResult();
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patientId")).thenReturn(validVitalStatistics());
        when(allTreatmentAdvices.currentTreatmentAdvice("patientId")).thenReturn(validTreatmentAdvice());
        when(allClinicVisits.getBaselineVisit(patient.getId())).thenReturn(clinicVisit(false));

        assertEquals(null, incompletePatientDataWarning.toString());
    }

    private void setupValidResult() {
        ClinicVisit clinicVisit = clinicVisit(true);
        LabResult labResult = LabResultBuilder.defaultCD4Result().withId(CD4_RESULT_ID).build();
        when(allLabResults.withIds(any(List.class))).thenReturn(asList(labResult));
        when(allClinicVisits.getBaselineVisit(patient.getId())).thenReturn(clinicVisit);
        when(allLabResults.allLabResults(patient.getId())).thenReturn(new LabResults(asList(labResult)));
    }

    private ClinicVisit clinicVisit(boolean hasCD4Result) {
        ClinicVisit clinicVisit = new ClinicVisit(patient, new VisitResponse());
        if (hasCD4Result) {
            clinicVisit.setLabResultIds(asList(CD4_RESULT_ID));
        }
        return clinicVisit;
    }

    private TreatmentAdvice validTreatmentAdvice() {
        return mock(TreatmentAdvice.class);
    }

    private VitalStatistics validVitalStatistics() {
        return VitalStatisticsBuilder.startRecording().withDefaults().build();
    }
}
