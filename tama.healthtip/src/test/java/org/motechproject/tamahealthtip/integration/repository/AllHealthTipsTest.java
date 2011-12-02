package org.motechproject.tamahealthtip.integration.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.domain.CallPreference;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamahealthtip.repository.AllHealthTips;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationContext-TAMAHealthTip.xml")
public class AllHealthTipsTest {

    @Autowired
    AllHealthTips allHealthTips;

    private Patient patient;

    @Before
    public void setup() {
        patient = PatientBuilder.startRecording().withCallPreference(CallPreference.DailyPillReminder).build();
    }

    @Test
    public void shouldReturnRelevantPriority2HealthTipsWhenPatientIsLessThan1MonthIntoART() {
        Map<String, String> healthTips = allHealthTips.findBy(DateUtil.today().minusDays(10), patient);
        assertEquals("2", healthTips.get("HT002a"));
        assertEquals("2", healthTips.get("HT001a"));
        assertEquals("2", healthTips.get("HT003a"));
        assertEquals("2", healthTips.get("HT004a"));
        assertEquals("2", healthTips.get("HT005a"));
        assertEquals("2", healthTips.get("HT011a"));
        assertEquals("2", healthTips.get("HT012a"));
        assertEquals("2", healthTips.get("HT013a"));
        assertEquals("2", healthTips.get("HT017a"));
        assertEquals("2", healthTips.get("HT018a"));
    }

    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientIsLessThan1MonthIntoART() {
        Map<String, String> healthTips = allHealthTips.findBy(DateUtil.today().minusDays(10), patient);
        assertEquals("3", healthTips.get("HT033a"));
        assertEquals("3", healthTips.get("HT034a"));
        assertEquals("3", healthTips.get("HT035a"));
        assertEquals("3", healthTips.get("HT036a"));
        assertEquals("3", healthTips.get("HT037a"));
        assertEquals("3", healthTips.get("HT038a"));
        assertEquals("3", healthTips.get("HT039a"));
        assertEquals("3", healthTips.get("HT040a"));
    }

    @Test
    public void shouldReturnRelevantPriority2HealthTipsWhenPatientIsOnDailyPillReminder_AndLessThan1MonthIntoART() {
        Map<String, String> healthTips = allHealthTips.findBy(DateUtil.today().minusDays(10), patient);
        assertEquals("2", healthTips.get("HT014a"));
        assertEquals("2", healthTips.get("HT019a"));
        assertEquals("2", healthTips.get("HT021a"));
    }

    @Test
    public void shouldReturnRelevantPriority2HealthTipsWhenPatientIsNotOnDailyPillReminder_AndLessThan1MonthIntoART() {
        patient = PatientBuilder.startRecording().withCallPreference(CallPreference.FourDayRecall).build();
        Map<String, String> healthTips = allHealthTips.findBy(DateUtil.today().minusDays(10), patient);
        assertEquals("2", healthTips.get("HT015a"));
        assertEquals("2", healthTips.get("HT020a"));
        assertEquals("2", healthTips.get("HT022a"));
    }
}
