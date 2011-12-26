package org.motechproject.tama.healthtips.integration.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.healthtips.service.HealthTipRuleService;
import org.motechproject.tama.ivr.domain.AdherenceComplianceReport;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationHealthTipsContext.xml")
public class HealthTipRuleServiceTest_DoseTakenLateLastWeekTests {

    @Mock
    private AdherenceService adherenceService;

    @Mock
    private AllLabResults allLabResults;

    @Autowired
    private AllPatients allPatients;

    @Autowired
    private StatelessKnowledgeSession healthTipsSession;

    private Patient patient;

    private LabResults labResults;

    private HealthTipRuleService healthTipRuleService;

    public void patientHasLabResults() {
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withType(TAMAConstants.LabTestType.CD4).build();
        List<LabResult> labResultsForPatient = Arrays.asList(
                LabResultBuilder.startRecording().withDefaults().withTestDate(DateUtil.today().minusMonths(3)).withResult("300").withLabTest(labTest).build(),
                LabResultBuilder.startRecording().withDefaults().withTestDate(DateUtil.today().minusMonths(5)).withResult("400").withLabTest(labTest).build());
        labResults = new LabResults(labResultsForPatient);
        when(allLabResults.findByPatientId("pid")).thenReturn(labResults);
    }

    @Before
    public void setup() {
        initMocks(this);
        patientHasLabResults();
        healthTipRuleService = new HealthTipRuleService(healthTipsSession, adherenceService, allLabResults);
    }

    @Test
    public void playsHealthTip13WhenDoseTakenLateLastWeek() {
        /*When Patient Is On Daily PillReminder*/
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        /*When Patient took a dose late last week*/
        when(adherenceService.lastWeekAdherence(patient)).thenReturn(new AdherenceComplianceReport(true, false));

        Map<String, String> relevantHealthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertNotNull(relevantHealthTips.get("HT013a"));
    }

    @Test
    public void healthTip13HasPriority1WhenDoseTakenLateLastWeek() {
        /*When Patient Is On Daily PillReminder*/
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        /*When Patient took a dose late last week*/
        when(adherenceService.lastWeekAdherence(patient)).thenReturn(new AdherenceComplianceReport(true, false));

        Map<String, String> relevantHealthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertEquals("1", relevantHealthTips.get("HT013a"));
    }

    @Test
    public void playsHealthTip18WhenDoseTakenLateLastWeek() {
        /*When Patient Is On Daily PillReminder*/
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        /*When Patient took a dose late last week*/
        when(adherenceService.lastWeekAdherence(patient)).thenReturn(new AdherenceComplianceReport(true, false));

        Map<String, String> relevantHealthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertNotNull(relevantHealthTips.get("HT018a"));
    }

    @Test
    public void healthTip18HasPriority1WhenDoseTakenLateLastWeek() {
        /*When Patient Is On Daily PillReminder*/
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        /*When Patient took a dose late last week*/
        when(adherenceService.lastWeekAdherence(patient)).thenReturn(new AdherenceComplianceReport(true, false));

        Map<String, String> relevantHealthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertEquals("1", relevantHealthTips.get("HT018a"));
    }
}