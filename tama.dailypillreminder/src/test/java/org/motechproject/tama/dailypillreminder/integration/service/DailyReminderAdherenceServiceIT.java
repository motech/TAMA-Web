package org.motechproject.tama.dailypillreminder.integration.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.TAMAPillReminderService;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@PrepareForTest(DateUtil.class)
@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class DailyReminderAdherenceServiceIT extends SpringIntegrationTest {

    @Mock
    private TAMAPillReminderService pillReminderService;

    @Mock
    private PatientAlertService patientAlertService;

    @Autowired
    private AllPatients allPatients;

    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    @Qualifier("ivrProperties")
    private Properties properties;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() {
        initMocks(this);
        dailyReminderAdherenceService = new DailyPillReminderAdherenceService(allPatients, allDosageAdherenceLogs, pillReminderService, properties, new AdherenceService());
        setUpDate();
    }

    public void setUpDate() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        LocalDate today = now.toLocalDate();
        PowerMockito.stub(method(DateUtil.class, "today")).toReturn(today);
    }

    @Test
    public void adherenceWhenLessThan4WeeksIntoRegimen_ForSingleDosage() {
        PillRegimenResponse regimenStartingToday = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today().minusWeeks(1), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingToday));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today());
        addAdherenceLog("dosage1Id", DosageStatus.NOT_TAKEN, DateUtil.today().minusDays(1));
        addAdherenceLog("dosage1Id", DosageStatus.WILL_TAKE_LATER, DateUtil.today().minusDays(2));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusDays(3));

        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        final DateTime afterDoseTime = DateUtil.now().withHourOfDay(11).withMinuteOfHour(30);
        assertEquals(25.00, dailyReminderAdherenceService.getAdherencePercentage("patientId", afterDoseTime));
    }

    @Test
    public void adherenceWhenLessThan4WeeksIntoRegimen_ForMultipleDosages() {
        PillRegimenResponse regimenStartingToday = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today().minusWeeks(1), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(11, 30), DateUtil.today().minusWeeks(1), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingToday));

        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today());
        addAdherenceLog("dosage1Id", DosageStatus.NOT_TAKEN, DateUtil.today().minusDays(1));
        addAdherenceLog("dosage1Id", DosageStatus.WILL_TAKE_LATER, DateUtil.today().minusDays(2));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusDays(3));

        addAdherenceLog("dosage2Id", DosageStatus.TAKEN, DateUtil.today());
        addAdherenceLog("dosage2Id", DosageStatus.NOT_TAKEN, DateUtil.today().minusDays(1));
        addAdherenceLog("dosage2Id", DosageStatus.WILL_TAKE_LATER, DateUtil.today().minusDays(2));
        addAdherenceLog("dosage2Id", DosageStatus.TAKEN, DateUtil.today().minusDays(3));

        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        DateTime timeOfSecondDosage = DateUtil.now().withHourOfDay(11).withMinuteOfHour(30);
        assertEquals(25.0, dailyReminderAdherenceService.getAdherencePercentage("patientId", timeOfSecondDosage));
    }

    @Test
    public void adherenceAfterFourWeeksIntoRegimen_ForSingleDosage_AfterTodaysDoseTime() {
        PillRegimenResponse regimenStartingFiveWeeksAgo = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today().minusWeeks(5), null, null, null));
        }});

        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).minusDays(1));

        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).plusDays(1));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).plusDays(2));

        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today());

        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingFiveWeeksAgo));

        double totalNumberOfDoses = 29.0;
        assertEquals(4 * 100 / totalNumberOfDoses, dailyReminderAdherenceService.getAdherencePercentage("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceAfterFourWeeksIntoRegimen_ForSingleDosage_BeforeTodaysDoseTime() {
        PillRegimenResponse regimenStartingFiveWeeksAgo = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(11, 30), DateUtil.today().minusWeeks(5), null, null, null));
        }});

        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).minusDays(1));

        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).plusDays(1));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).plusDays(2));

        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingFiveWeeksAgo));

        double totalNumberOfDoses = 28.0;
        assertEquals(3 * 100 / totalNumberOfDoses, dailyReminderAdherenceService.getAdherencePercentage("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceAfterFourWeeksIntoRegimen_ForMultipleDosages_AfterTodaysFirstDoseTime() {
        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today().minusWeeks(5), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(16, 30), DateUtil.today().minusWeeks(5), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));
        // setting up adherence logs for first dosage
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).minusDays(1));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).plusDays(1));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).plusDays(2));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today());
        // setting up adherence logs for second dosage
        addAdherenceLog("dosage2Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).minusDays(1));
        addAdherenceLog("dosage2Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4));
        addAdherenceLog("dosage2Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).plusDays(1));
        addAdherenceLog("dosage2Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).plusDays(2));

        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        int dosesTakenTheLastFourWeeks = 7;
        double totalDosesInFourWeeks = 29 + 28;
        DateTime timeOfFirstDosage = DateUtil.now().withHourOfDay(11).withMinuteOfHour(30);
        assertEquals(dosesTakenTheLastFourWeeks * 100 / totalDosesInFourWeeks, dailyReminderAdherenceService.getAdherencePercentage("patientId", timeOfFirstDosage));
    }

    private void addAdherenceLog(String dosageId, DosageStatus dosageStatus, LocalDate dosageDate) {
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", dosageId, dosageStatus, dosageDate));
    }

}