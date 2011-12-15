package org.motechproject.tamahealthtip.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.service.AdherenceService;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.builder.LabResultBuilder;
import org.motechproject.tamadomain.builder.LabTestBuilder;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.domain.*;
import org.motechproject.tamadomain.repository.AllLabResults;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationContext-TAMAHealthTip.xml")
public class HealthTipRuleServiceTest_DoseTakenLateLastWeekTests {

    @Mock
    private AdherenceService adherenceService;

    @Mock
    private AllLabResults allLabResults;

    @Autowired
    private AllPatients allPatients;

    @Autowired
    private PillReminderService pillReminderService;

    @Autowired
    private StatelessKnowledgeSession healthTipsSession;

    private Patient patient;

    private LabResults labResults;

    private HealthTipRuleService healthTipRuleService;

    public void patientHasLabResults(Patient patient) {
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
        patientHasLabResults(patient);
        healthTipRuleService = new HealthTipRuleService(healthTipsSession, adherenceService, allLabResults);
    }

    @Test
    public void playsHealthTip13WhenDoseTakenLateLastWeek() {
        /*When Patient Is On Daily PillReminder*/
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        /*When Patient took a dose late last week*/
        when(adherenceService.anyDoseTakenLateSince(patient, DateUtil.today().minusDays(6))).thenReturn(true);

        Map<String, String> relevantHealthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertNotNull(relevantHealthTips.get("HT013a"));
    }

    @Test
    public void healthTip13HasPriority1WhenDoseTakenLateLastWeek(){
        /*When Patient Is On Daily PillReminder*/
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        /*When Patient took a dose late last week*/
        when(adherenceService.anyDoseTakenLateSince(patient, DateUtil.today().minusDays(6))).thenReturn(true);

        Map<String, String> relevantHealthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertEquals("1", relevantHealthTips.get("HT013a"));
    }

    @Test
    public void playsHealthTip18WhenDoseTakenLateLastWeek() {
        /*When Patient Is On Daily PillReminder*/
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        /*When Patient took a dose late last week*/
        when(adherenceService.anyDoseTakenLateSince(patient, DateUtil.today().minusDays(6))).thenReturn(true);

        Map<String, String> relevantHealthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertNotNull(relevantHealthTips.get("HT018a"));
    }

    @Test
    public void healthTip18HasPriority1WhenDoseTakenLateLastWeek(){
        /*When Patient Is On Daily PillReminder*/
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        /*When Patient took a dose late last week*/
        when(adherenceService.anyDoseTakenLateSince(patient, DateUtil.today().minusDays(6))).thenReturn(true);

        Map<String, String> relevantHealthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertEquals("1", relevantHealthTips.get("HT018a"));
    }
}

