package org.motechproject.tama.healthtips.integration.service;

import org.drools.KnowledgeBase;
import org.joda.time.LocalDate;
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
public class HealthTipRuleServiceTest {

    @Autowired
    private KnowledgeBase healthTipsKnowledgeBase;
    @Mock
    private AdherenceService adherenceService;
    @Mock
    private AllLabResults allLabResults;

    private Patient patient;
    HealthTipRuleService healthTipRuleService;
    public LabResults labResults;

    @Before
    public void setup() {
        initMocks(this);

        setupLabResults(DateUtil.today().minusMonths(3), "300", DateUtil.today().minusMonths(5), "450");
        patient = PatientBuilder.startRecording().withId("patientDocId").withCallPreference(CallPreference.DailyPillReminder).build();

        when(adherenceService.lastWeekAdherence(patient)).thenReturn(new AdherenceComplianceReport(false, false));
        healthTipRuleService = new HealthTipRuleService(healthTipsKnowledgeBase, adherenceService, allLabResults);
    }

    private void setupLabResults(LocalDate testDate1, String testResult1, LocalDate testDate2, String testResult2) {
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withType(TAMAConstants.LabTestType.CD4).build();
        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withTestDate(testDate1).withResult(testResult1).withLabTest(labTest).build();
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withTestDate(testDate2).withResult(testResult2).withLabTest(labTest).build();
        labResults = new LabResults(Arrays.asList(labResult1, labResult2));
    }

    @Test
    public void shouldReturnRelevantPriority2HealthTipsWhenPatientIsLessThan1MonthIntoART() {
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
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
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
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
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertEquals("2", healthTips.get("HT014a"));
        assertEquals("2", healthTips.get("HT019a"));
        assertEquals("2", healthTips.get("HT021a"));
    }

    @Test
    public void shouldReturnRelevantPriority2HealthTipsWhenPatientIsNotOnDailyPillReminder_AndLessThan1MonthIntoART() {
        patient = PatientBuilder.startRecording().withCallPreference(CallPreference.FourDayRecall).withId("patientDocId").build();
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(10), patient);
        assertEquals("2", healthTips.get("HT015a"));
        assertEquals("2", healthTips.get("HT020a"));
        assertEquals("2", healthTips.get("HT022a"));
    }

    //  rule "Less than 2 months, but more than 1 month into ART and patient on daily pill reminder"
    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientIsMoreThan1MonthIntoART_AndOnDailyPillReminder() {
        setupLabResults(DateUtil.today().minusMonths(3), "300", DateUtil.today().minusMonths(5), "450");
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(31), patient);
        assertEquals("3", healthTips.get("HT021a"));
        assertEquals("3", healthTips.get("HT014a"));
        assertEquals("3", healthTips.get("HT019a"));
        assertOnPriority3HealthTipsForPatientLessThanTwoMonthsIntoART(healthTips);
    }

    //  rule "Less than 2 months, but more than 1 month into ART and patient not on daily pill reminder"
    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientIsMoreThan1MonthIntoART_AndNotOnDailyPillReminder() {
        setupLabResults(DateUtil.today().minusMonths(3), "300", DateUtil.today().minusMonths(5), "450");
        patient = PatientBuilder.startRecording().withCallPreference(CallPreference.FourDayRecall).withId("patientDocId").build();
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusDays(40), patient);
        assertEquals("3", healthTips.get("HT022a"));
        assertEquals("3", healthTips.get("HT015a"));
        assertEquals("3", healthTips.get("HT020a"));
        assertEquals("2", healthTips.get("HT006a"));
        assertEquals("2", healthTips.get("HT007a"));
        assertEquals("2", healthTips.get("HT008a"));
        assertEquals("2", healthTips.get("HT009a"));
        assertOnPriority3HealthTipsForPatientLessThanTwoMonthsIntoART(healthTips);
    }

    //  rule "Greater than 2 months into ART"
    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientMoreThanTwoMonthsIntoART() {
        setupLabResults(DateUtil.today().minusMonths(1), "300", DateUtil.today().minusMonths(2), "450");
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(4), patient);
        assertOnPriority3HealthTipsForPatientMoreThanTwoMonthsIntoART(healthTips);
    }

    //  rule "Greater than 8 weeks but less than 3 months into ART"
    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientMoreThan8WeeksAndLessThan3MonthsIntoART() {
        setupLabResults(DateUtil.today().minusMonths(1), "300", DateUtil.today().minusMonths(2), "450");
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(2).minusWeeks(1), patient);
        assertOnPriority3HealthTipsForPatientMoreThanTwoMonthsIntoART(healthTips);
    }

    //  rule "Greater than 2 months into ART and patient on Daily Pill Reminder"
    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientMoreThanTwoMonthsIntoART_AndPatientOnDailyPillReminder() {
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        patient = PatientBuilder.startRecording().withCallPreference(CallPreference.DailyPillReminder).withId("patientDocId").build();
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(4), patient);
        assertEquals("3", healthTips.get("HT021a"));
        assertEquals("3", healthTips.get("HT014a"));
        assertEquals("3", healthTips.get("HT019a"));
    }

    //  rule "Greater than 2 months into ART and patient not on Daily Pill Reminder"
    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientMoreThanTwoMonthsIntoART_AndPatientNotOnPillReminder() {
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        patient = PatientBuilder.startRecording().withCallPreference(CallPreference.FourDayRecall).withId("patientDocId").build();
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(4), patient);
        assertEquals("3", healthTips.get("HT022a"));
        assertEquals("3", healthTips.get("HT015a"));
        assertEquals("3", healthTips.get("HT020a"));
    }

    //  rule "Less than 12 months but more than 2 months into ART and date of baseline CD4 test more than two and half months ago"
    @Test
    public void shouldReturnRelevantPriority1HealthTipsWhenPatientIsLessThan12Months_AndMoreThan2MonthsIntoART_AndLastCD4TestWasMoreThanTwoAndHalfMonthsAgo() {
        setupLabResults(DateUtil.today().minusMonths(3), "300", DateUtil.today().minusMonths(5), "450");
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(6), patient);
        assertEquals("1", healthTips.get("HT008a"));
        assertEquals("1", healthTips.get("HT009a"));
    }

    //  rule "Less than 12 months but more than 2 months into ART and date of baseline CD4 test less than two and half months ago"
    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientIsLessThan12Months_AndMoreThan2MonthsIntoART_AndLastCD4TestWasLessThanTwoAndHalfMonthsAgo() {
        setupLabResults(DateUtil.today().minusMonths(1), "300", DateUtil.today().minusMonths(2), "450");
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(3), patient);
        assertEquals("3", healthTips.get("HT008a"));
        assertEquals("3", healthTips.get("HT009a"));
    }


    //  rule "Greater than 12 months into ART, baseline CD4 count < 350 and date of baseline CD4 test more than two and half months ago but less than five and a half months ago"
    @Test
    public void shouldReturnRelevantPriority1HealthTipsWhenPatientIsMoreThan12MonthsIntoART_AndLatestCD4LessThan350_AndLastCD4TestWasMoreThanTwoAndHalfMonthsAgo_AndLessThanFiveAndHalfMonths() {
        setupLabResults(DateUtil.today().minusMonths(3), "300", DateUtil.today().minusMonths(4), "325");
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(15), patient);
        assertEquals("1", healthTips.get("HT010a"));
    }

    //  rule "Greater than 12 months into ART, baseline CD4 count < 350 and date of baseline CD4 test less than two and half months ago"
    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientIsMoreThan12MonthsIntoART_AndLatestCD4LessThan350_AndLastCD4TestWasLessThanTwoAndHalfMonthsAgo() {
        setupLabResults(DateUtil.today().minusMonths(2), "300", DateUtil.today().minusMonths(1), "300");
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(15), patient);
        assertEquals("3", healthTips.get("HT010a"));
    }

    //  rule "Greater than 12 months into ART, baseline CD4 count > 350 and date of baseline CD4 test more than five and a half months ago"
    @Test
    public void shouldReturnRelevantPriority1HealthTipsWhenPatientIsMoreThan12MonthsIntoART_AndLatestCD4MoreThan350_AndLastCD4TestWasMoreThanFiveAndHalfMonthsAgo() {
        setupLabResults(DateUtil.today().minusMonths(9), "150", DateUtil.today().minusMonths(10), "700");
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(15), patient);
        assertEquals("1", healthTips.get("HT010a"));
    }

    //  rule "Greater than 12 months into ART, baseline CD4 count > 350 and date of baseline CD4 test less than less than five and a half months ago but more than two and a half months ago"
    @Test
    public void shouldReturnRelevantPriority3HealthTipsWhenPatientIsMoreThan12MonthsIntoART_AndLatestCD4MoreThan350_AndLastCD4TestWasLessThanFiveAndHalfMonthsAgo_AndMoreThanTwoAndHalfMonthsAgo() {
        setupLabResults(DateUtil.today().minusMonths(3), "150", DateUtil.today().minusMonths(4), "500");
        when(allLabResults.findLatestLabResultsByPatientId("patientDocId")).thenReturn(labResults);
        Map<String, String> healthTips = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today().minusMonths(15), patient);
        assertEquals("3", healthTips.get("HT010a"));
    }

    public void assertOnPriority3HealthTipsForPatientLessThanTwoMonthsIntoART(Map<String, String> healthTips) {
        assertEquals("3", healthTips.get("HT001a"));
        assertEquals("3", healthTips.get("HT002a"));
        assertEquals("3", healthTips.get("HT003a"));
        assertEquals("3", healthTips.get("HT004a"));
        assertEquals("3", healthTips.get("HT005a"));
        assertEquals("3", healthTips.get("HT011a"));
        assertEquals("3", healthTips.get("HT012a"));
        assertEquals("3", healthTips.get("HT013a"));
        assertEquals("3", healthTips.get("HT017a"));
        assertEquals("3", healthTips.get("HT018a"));
        assertEquals("3", healthTips.get("HT033a"));
        assertEquals("3", healthTips.get("HT034a"));
        assertEquals("3", healthTips.get("HT035a"));
        assertEquals("3", healthTips.get("HT036a"));
        assertEquals("3", healthTips.get("HT037a"));
        assertEquals("3", healthTips.get("HT038a"));
        assertEquals("3", healthTips.get("HT039a"));
        assertEquals("3", healthTips.get("HT040a"));
    }

    public void assertOnPriority3HealthTipsForPatientMoreThanTwoMonthsIntoART(Map<String, String> healthTips) {
        assertOnPriority3HealthTipsForPatientLessThanTwoMonthsIntoART(healthTips);
        assertEquals("3", healthTips.get("HT006a"));
        assertEquals("3", healthTips.get("HT007a"));
        assertEquals("3", healthTips.get("HT008a"));
        assertEquals("3", healthTips.get("HT009a"));
    }
}
