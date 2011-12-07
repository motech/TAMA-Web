package org.motechproject.tamahealthtip.integration.repository;

import org.drools.runtime.StatelessKnowledgeSession;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.service.AdherenceService;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.domain.CallPreference;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamahealthtip.service.HealthTipRuleService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationContext-TAMAHealthTip.xml")
public class HealthTipRuleServiceTest_WhenDoseMissedInLastWeek {

    @Autowired
    private StatelessKnowledgeSession healthTipsSession;

    @Mock
    private AdherenceService adherenceService;

    HealthTipRuleService healthTipRuleService;

    @Autowired
    AllPatients allPatients;
    @Autowired
    PillReminderService pillReminderService;

    private Patient patient;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldReturnHealthTipWithPriorityWhenPatientOnDailyReminder() {
        patient = PatientBuilder.startRecording().withPatientId("pid").withCallPreference(CallPreference.DailyPillReminder).build();
        LocalDate today = DateUtil.today();
        when(adherenceService.isDosageMissedLastWeek(patient)).thenReturn(true);
        healthTipRuleService = new HealthTipRuleService(healthTipsSession, adherenceService);

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
        patient = PatientBuilder.startRecording().withPatientId("pid").withCallPreference(CallPreference.FourDayRecall).build();
        LocalDate today = DateUtil.today();
        when(adherenceService.isDosageMissedLastWeek(patient)).thenReturn(true);
        healthTipRuleService = new HealthTipRuleService(healthTipsSession, adherenceService);

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
        patient = PatientBuilder.startRecording().withPatientId("pid").withCallPreference(CallPreference.FourDayRecall).build();
        LocalDate today = DateUtil.today();
        when(adherenceService.isDosageMissedLastWeek(patient)).thenReturn(true);
        healthTipRuleService = new HealthTipRuleService(healthTipsSession, adherenceService);

        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);

        assertEquals("1", healthTips.get("HT002a"));
        assertEquals("1", healthTips.get("HT004a"));

        assertEquals("1", healthTips.get("HT011a"));
        assertEquals("1", healthTips.get("HT012a"));
        assertEquals("1", healthTips.get("HT022a"));
        assertEquals("1", healthTips.get("HT015a"));

    }


}
