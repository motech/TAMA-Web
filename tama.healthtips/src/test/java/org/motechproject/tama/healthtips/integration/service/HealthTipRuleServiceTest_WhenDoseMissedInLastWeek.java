package org.motechproject.tama.healthtips.integration.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationHealthTipsContext.xml")
public class HealthTipRuleServiceTest_WhenDoseMissedInLastWeek {
    @Mock
    private AdherenceService adherenceService;
    @Mock
    private AllLabResults allLabResults;

    @Autowired
    AllPatients allPatients;
    @Autowired
    PillReminderService pillReminderService;
    @Autowired
    private StatelessKnowledgeSession healthTipsSession;

    private Patient patient;
    private LabResults labResults;
    HealthTipRuleService healthTipRuleService;

    @Before
    public void setup() {
        initMocks(this);
        setupLabResults(DateUtil.today().minusMonths(3), "300", DateUtil.today().minusMonths(5), "450");
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        when(allLabResults.findByPatientId("pid")).thenReturn(labResults);
        healthTipRuleService = new HealthTipRuleService(healthTipsSession, adherenceService, allLabResults);
    }

    private void setupLabResults(LocalDate testDate1, String testResult1, LocalDate testDate2, String testResult2) {
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withType(TAMAConstants.LabTestType.CD4).build();
        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withTestDate(testDate1).withResult(testResult1).withLabTest(labTest).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withTestDate(testDate2).withResult(testResult2).withLabTest(labTest).build();
        labResults = new LabResults(Arrays.asList(labResult1, labResult2));
    }

    @Test
    public void shouldReturnHealthTipWithPriorityWhenPatientOnDailyReminder() {
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        when(adherenceService.lastWeekAdherence(patient)).thenReturn(new AdherenceComplianceReport(false, true));
        healthTipRuleService = new HealthTipRuleService(healthTipsSession, adherenceService, allLabResults);

        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertEquals("1", healthTips.get("HT002a"));
        assertEquals("1", healthTips.get("HT004a"));
        assertEquals("1", healthTips.get("HT011a"));
        assertEquals("1", healthTips.get("HT012a"));
        assertEquals("1", healthTips.get("HT021a"));
        assertEquals("1", healthTips.get("HT014a"));
    }

    @Test
    public void shouldReturnHealthTipWithPriorityWhenPatientOnFourDayRecall() {
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.FourDayRecall).build();
        when(adherenceService.lastWeekAdherence(patient)).thenReturn(new AdherenceComplianceReport(false, true));
        healthTipRuleService = new HealthTipRuleService(healthTipsSession, adherenceService, allLabResults);

        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertEquals("1", healthTips.get("HT002a"));
        assertEquals("1", healthTips.get("HT004a"));
        assertEquals("1", healthTips.get("HT011a"));
        assertEquals("1", healthTips.get("HT012a"));
        assertEquals("1", healthTips.get("HT022a"));
        assertEquals("1", healthTips.get("HT015a"));
    }

    @Test
    public void shouldReturnHealthTipWithPriorityWhenPatientOnFourDayRecallAndPatientOnRegimenLessThanAMonth() {
        patient = PatientBuilder.startRecording().withId("pid").withCallPreference(CallPreference.FourDayRecall).build();
        when(adherenceService.lastWeekAdherence(patient)).thenReturn(new AdherenceComplianceReport(false, true));
        healthTipRuleService = new HealthTipRuleService(healthTipsSession, adherenceService, allLabResults);

        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);

        assertEquals("1", healthTips.get("HT002a"));
        assertEquals("1", healthTips.get("HT004a"));

        assertEquals("1", healthTips.get("HT011a"));
        assertEquals("1", healthTips.get("HT012a"));
        assertEquals("1", healthTips.get("HT022a"));
        assertEquals("1", healthTips.get("HT015a"));
    }
}
