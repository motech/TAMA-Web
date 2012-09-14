package org.motechproject.tama.healthtips.criteria;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.refdata.domain.LabTest;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.today;

public class ContinueToHealthTipsCriteriaTest {

    public static final String PATIENT_ID = "patientId";

    @Mock
    private AllLabResults allLabResults;

    @Mock
    private AllVitalStatistics allVitalStatistics;

    @Mock
    private AllClinicVisits allClinicVisits;

    private ContinueToHealthTipsCriteria continueToHealthTipsCriteria;

    @Before
    public void setup() {
        initMocks(this);
        continueToHealthTipsCriteria = new ContinueToHealthTipsCriteria(allLabResults, allVitalStatistics, allClinicVisits);
    }

    @Test
    public void shouldReturnFalseWhenNoLabResultsHaveBeenCapturedForPatient() {
        LabResults emptyLabResults = new LabResults();
        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(emptyLabResults);
        assertFalse(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldReturnFalseWhenLabResultsForPatientIsNull() {
        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(null);
        assertFalse(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldReturnFalseWhenLabResultHasOnlySinglePVLResult() {
        LabResults results = new LabResults();
        results.add(result(TAMAConstants.LabTestType.PVL, 0));

        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(results);
        assertFalse(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldReturnFalseWhenLabResultHasMultiplePVLResults() {
        LabResults results = new LabResults();
        results.addAll(asList(result(TAMAConstants.LabTestType.PVL, 0), result(TAMAConstants.LabTestType.PVL, 1)));

        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(results);
        assertFalse(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldReturnFalseWhenLabResultHasCD4Result() {
        LabResults results = new LabResults();
        results.addAll(asList(result(TAMAConstants.LabTestType.CD4, 0), result(TAMAConstants.LabTestType.PVL, 1)));

        when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(results);
        assertTrue(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }

    private LabResult result(TAMAConstants.LabTestType type, int daysFromToday) {
        LabResult result = new LabResult();
        LabTest test = test(type);
        result.setLabTest(test);
        result.setResult("1");
        result.setTestDate(today().plusDays(daysFromToday));
        return result;
    }

    private LabTest test(TAMAConstants.LabTestType type) {
        LabTest test = new LabTest();
        test.setName(type.getName());
        return test;
    }
}
