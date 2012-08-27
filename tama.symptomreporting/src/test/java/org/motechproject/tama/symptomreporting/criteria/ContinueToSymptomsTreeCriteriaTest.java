package org.motechproject.tama.symptomreporting.criteria;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.appointments.api.service.contract.VisitResponse;
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
    public void shouldContinueToSymptomsFlowWhenVitalStatisticsAndBaselineCD4CountArePresent() {
        final LabResult labResult = new LabResult();
        labResult.setLabTest(LabTest.newLabTest(TAMAConstants.LabTestType.CD4, "500"));
        labResult.setResult("100");

        when(allLabResults.get(anyString())).thenReturn(labResult);
        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(new VitalStatistics());
        ClinicVisit clinicVisit = mock(ClinicVisit.class);
        when(clinicVisit.getLabResultIds()).thenReturn(Arrays.asList("labResultId"));
        when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);

        assertTrue(continueToSymptomsTreeCriteria.shouldContinue(PATIENT_ID));
    }
}
