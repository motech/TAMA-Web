package org.motechproject.tama.symptomreporting.criteria;

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

public class ContinueToSymptomsTreeCriteriaTest {

    public static final String PATIENT_ID = "patientId";
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllVitalStatistics allVitalStatistics;

    private ContinueToSymptomsTreeCriteria continueToSymptomsTreeCriteria;

    @Before
    public void setup() {
        initMocks(this);
        continueToSymptomsTreeCriteria = new ContinueToSymptomsTreeCriteria(allLabResults, allVitalStatistics);
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
    public void shouldContinueToSymptomsFlowWhenVitalStatisticsAndLabResultsArePresent() {
        LabResults labResults = new LabResults();
        labResults.add(new LabResult());

        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(labResults);
        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(new VitalStatistics());

        assertTrue(continueToSymptomsTreeCriteria.shouldContinue(PATIENT_ID));
    }
}
