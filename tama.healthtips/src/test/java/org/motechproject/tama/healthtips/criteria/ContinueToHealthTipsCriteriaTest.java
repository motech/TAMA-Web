package org.motechproject.tama.healthtips.criteria;

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
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
    public void shouldNotContinueToSymptomsFlowWhenLabResultsAreEmpty() {
        ClinicVisit clinicVisit = mock(ClinicVisit.class);
        when(clinicVisit.getLabResultIds()).thenReturn(Collections.<String>emptyList());
        when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
        assertFalse(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldContinueToHealthTipsEvenWhenOnlyCD4CountAvailable() {
        final ClinicVisit clinicVisit = mock(ClinicVisit.class);
        when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
        when(clinicVisit.getLabResultIds()).thenReturn(Arrays.asList("test1", "test2"));
        final LabResult labResult = new LabResult();
        labResult.setId("test1");
        labResult.setLabTest(LabTest.newLabTest(TAMAConstants.LabTestType.CD4, "500"));
        labResult.setResult("100");
        when(allLabResults.get("test1")).thenReturn(labResult);

        assertTrue(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }

    @Test
    public void shouldContinueToSymptomsFlowWhenVitalStatisticsAndLabResultsArePresent() {
        final LabResult labResult = new LabResult();
        labResult.setLabTest(LabTest.newLabTest(TAMAConstants.LabTestType.CD4, "500"));
        labResult.setResult("100");

        when(allLabResults.get(anyString())).thenReturn(labResult);
        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(new VitalStatistics());
        ClinicVisit clinicVisit = mock(ClinicVisit.class);
        when(clinicVisit.getLabResultIds()).thenReturn(Arrays.asList("labResultId"));
        when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);

        assertTrue(continueToHealthTipsCriteria.shouldContinue(PATIENT_ID));
    }
}
