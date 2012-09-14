package org.motechproject.tama.symptomreporting.criteria;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.refdata.domain.LabTest;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ContinueToSymptomsTreeCriteriaTest {

    public static final String PATIENT_ID = "patientId";
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private AllClinicVisits allClinicVisits;

    private ContinueToSymptomsTreeCriteria continueToSymptomsTreeCriteria;

    @Before
    public void setup() {
        initMocks(this);
        continueToSymptomsTreeCriteria = new ContinueToSymptomsTreeCriteria(allLabResults, allVitalStatistics, allClinicVisits);
    }

    @Test
    public void shouldNotContinueToSymptomsFlowWhenLabResultsAreEmpty() {
        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(new LabResults());
        assertFalse(continueToSymptomsTreeCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldNotContinueToSymptomsFlowWhenVitalStatisticsAreEmpty() {
        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(new LabResults());
        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(null);

        assertFalse(continueToSymptomsTreeCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldNotContinueToSymptomsFlowWhenHeightIsNotPresent() {
        setupLabResults();

        vitalStatistics(false);
        baselineVisit();
        assertFalse(continueToSymptomsTreeCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldNotContinueToSymptomsFlowWhenWeightIsNotPresent() {
        setupLabResults();
        vitalStatistics(false);
        baselineVisit();
        assertFalse(continueToSymptomsTreeCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldContinueToSymptomsFlowWhenMandatoryVitalStatisticsAndBaselineCD4CountArePresent() {
        setupLabResults();
        vitalStatistics(true, 1d, 1d);
        baselineVisit();
        assertTrue(continueToSymptomsTreeCriteria.shouldContinue(PATIENT_ID));
    }

    private void setupLabResults() {
        final LabResult labResult = labResult();
        when(allLabResults.get(anyString())).thenReturn(labResult);
    }

    private void vitalStatistics(boolean heightAndWeightPresent, Double... heightAndWeight) {
        VitalStatistics vitalStatistics = new VitalStatistics();
        if (heightAndWeightPresent) {
            vitalStatistics.setHeightInCm(heightAndWeight[0]);
            vitalStatistics.setWeightInKg(heightAndWeight[1]);
        }
        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(vitalStatistics);
    }

    private LabResult labResult() {
        final LabResult labResult = new LabResult();
        labResult.setLabTest(LabTest.newLabTest(TAMAConstants.LabTestType.CD4, "500"));
        labResult.setResult("100");
        return labResult;
    }

    private void baselineVisit() {
        ClinicVisit clinicVisit = mock(ClinicVisit.class);
        when(clinicVisit.getLabResultIds()).thenReturn(Arrays.asList("labResultId"));
        when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
    }
}
