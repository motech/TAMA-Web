package org.motechproject.tama.healthtips.criteria;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllVitalStatistics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ContinueToHealthTipsCriteriaTest {

    public static final String PATIENT_ID = "patientId";
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllVitalStatistics allVitalStatistics;

    private ContinueToHealthTipsCriteria continueToHealthTipsCriteria;

    @Before
    public void setup() {
        initMocks(this);
        continueToHealthTipsCriteria = new ContinueToHealthTipsCriteria(allLabResults, allVitalStatistics);
    }

    @Test
    public void shouldNotContinueToSymptomsFlowWhenLabResultsAreEmpty() {
        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(new LabResults());
        assertFalse(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldNotContinueToSymptomsFlowWhenVitalStatisticsAreEmpty() {
        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(new LabResults());
        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(null);

        assertFalse(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldContinueToSymptomsFlowWhenVitalStatisticsAndLabResultsArePresent() {
        LabResults labResults = new LabResults();
        labResults.add(new LabResult());

        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(labResults);
        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(new VitalStatistics());

        assertTrue(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }
}
