package org.motechproject.tama.dailypillreminder.integration.listener;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.listener.AdherenceQualityListener;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.dailypillreminder.service.TAMAPillReminderService;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@PrepareForTest(DateUtil.class)
@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class AdherenceQualityListenerIT extends SpringIntegrationTest {

    private static final String PATIENT_ID = "patientId";
    private static final String ADHERENCE_THRESHOLD = "70";

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Mock
    private TAMAPillReminderService pillReminderService;

    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;

    @Mock
    private AllPatients allPatients;

    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    private AdherenceQualityListener adherenceQualityListener;

    private void setUpTime() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();
        PowerMockito.stub(method(DateUtil.class, "today")).toReturn(today);
    }

    @Before
    public void setUp() {
        initMocks(this);
        setUpTime();
        properties.setProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE, ADHERENCE_THRESHOLD);
        dailyReminderAdherenceService = new DailyPillReminderAdherenceService(allPatients, allDosageAdherenceLogs, pillReminderService, properties, new AdherenceService());
        adherenceQualityListener = new AdherenceQualityListener(dailyReminderAdherenceTrendService, properties, dailyReminderAdherenceService, allPatients);
        Patient patient = new PatientBuilder().withDefaults().withStatus(Status.Active).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
    }

    public void setUpAdherenceBelowThreshold() {
        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", PATIENT_ID, 2, 5, Arrays.asList(new DosageResponse("dosage1Id", new Time(5, 30), DateUtil.today().minusWeeks(5), null, null, null)));
        when(pillReminderService.getPillRegimen(PATIENT_ID)).thenReturn(new PillRegimen(pillRegimen));
    }

    @Test
    public void shouldRaiseAdherenceInRedAlert() {
        setUpAdherenceBelowThreshold();
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(PATIENT_ID)
                .withExternalId(PATIENT_ID)
                .payload();
        MotechEvent eventToDetermineAdherenceInRed = new MotechEvent(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT, eventParams);
        adherenceQualityListener.determineAdherenceQualityAndRaiseAlert(eventToDetermineAdherenceInRed);
        double expectedAdherencePercentage = 0.0;
        verify(dailyReminderAdherenceTrendService).raiseAdherenceInRedAlert(PATIENT_ID, expectedAdherencePercentage);
    }

    private void setUpAdherenceAboveThreshold() {
        int totalDoses = 28;
        int threshold = Integer.parseInt(ADHERENCE_THRESHOLD);
        double dosesToBeTaken = (((double) threshold / 100) * totalDoses) + 1;
        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", PATIENT_ID, 2, 5, Arrays.asList(new DosageResponse("dosage1Id", new Time(5, 30), DateUtil.today().minusWeeks(5), null, null, null)));
        when(pillReminderService.getPillRegimen(PATIENT_ID)).thenReturn(new PillRegimen(pillRegimen));
        for (int dosesTaken = 0; dosesTaken < dosesToBeTaken; dosesTaken++) {
            DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusDays(dosesTaken));
            allDosageAdherenceLogs.add(dosageAdherenceLog);
            markForDeletion(dosageAdherenceLog);
        }
    }

    @Test
    public void shouldNotRaiseAdherenceInRedAlert() {
        setUpAdherenceAboveThreshold();
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(PATIENT_ID)
                .withExternalId(PATIENT_ID)
                .payload();
        MotechEvent eventToDetermineAdherenceInRed = new MotechEvent(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT, eventParams);
        adherenceQualityListener.determineAdherenceQualityAndRaiseAlert(eventToDetermineAdherenceInRed);
        verify(dailyReminderAdherenceTrendService, never()).raiseAdherenceInRedAlert(same(PATIENT_ID), Matchers.<Double>any());
    }
}
