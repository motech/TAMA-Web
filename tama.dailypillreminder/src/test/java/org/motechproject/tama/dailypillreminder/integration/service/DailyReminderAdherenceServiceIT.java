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
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@PrepareForTest(DateUtil.class)
@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class DailyReminderAdherenceServiceIT extends SpringIntegrationTest {

    private static final double DOSES_IN_FOUR_WEEKS = 28.0;
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
        final LocalDate today = now.toLocalDate();
        PowerMockito.stub(method(DateUtil.class, "today")).toReturn(today);
    }

    @Test
    public void adherenceWhenADoseIsTaken() {
        PillRegimenResponse regimenStartingToday = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosageId", new Time(10, 30), DateUtil.today(), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingToday));

        DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog("patientId", "pillRegimenId", "dosageId", DosageStatus.TAKEN, DateUtil.today());
        allDosageAdherenceLogs.add(dosageAdherenceLog);
        markForDeletion(dosageAdherenceLog);

        assertEquals(100.0, dailyReminderAdherenceService.getAdherenceInPercentage("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceWhenADoseIsMissed() {
        PillRegimenResponse regimenStartingToday = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosageId", new Time(10, 30), DateUtil.today(), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingToday));

        /*A dose is missed*/
        DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog("patientId", "pillRegimenId", "dosageId", DosageStatus.NOT_TAKEN, DateUtil.today());
        allDosageAdherenceLogs.add(dosageAdherenceLog);
        markForDeletion(dosageAdherenceLog);

        assertEquals(0.0, dailyReminderAdherenceService.getAdherenceInPercentage("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceWhenTwoDosesAreTaken() {
        PillRegimenResponse regimenStartingYesterday = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosageId", new Time(10, 30), DateUtil.today().minusDays(1), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingYesterday));

        List<DosageAdherenceLog> dosageAdherenceLogs = new ArrayList<DosageAdherenceLog>();
        /*Dose two days ago was taken*/
        dosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosageId", DosageStatus.TAKEN, DateUtil.today().minusDays(1)));

        /*Yesterday's dose was taken*/
        dosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosageId", DosageStatus.TAKEN, DateUtil.today()));

        for (DosageAdherenceLog dosageAdherenceLog : dosageAdherenceLogs) {
            allDosageAdherenceLogs.add(dosageAdherenceLog);
            markForDeletion(dosageAdherenceLog);
        }

        assertEquals(100.0, dailyReminderAdherenceService.getAdherenceInPercentage("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceWhenAllDosesOfAKindAreTaken() {
        PillRegimenResponse regimenStartingToday = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today(), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(11, 30), DateUtil.today(), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingToday));

        /*Today's doses are taken*/
        List<DosageAdherenceLog> dosageAdherenceLogs = new ArrayList<DosageAdherenceLog>();
        dosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today()));
        dosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, DateUtil.today()));

        for (DosageAdherenceLog dosageAdherenceLog : dosageAdherenceLogs) {
            allDosageAdherenceLogs.add(dosageAdherenceLog);
            markForDeletion(dosageAdherenceLog);
        }

        DateTime timeOfSecondDose = DateUtil.now().withHourOfDay(11).withMinuteOfHour(30);
        assertEquals(100.0, dailyReminderAdherenceService.getAdherenceInPercentage("patientId", timeOfSecondDose));
    }

    @Test
    public void adherenceWhenOneDoseOfAKindIsTaken() {
        PillRegimenResponse regimenStartingToday = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today(), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(11, 30), DateUtil.today(), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingToday));

        DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today());
        allDosageAdherenceLogs.add(dosageAdherenceLog);
        markForDeletion(dosageAdherenceLog);

        DateTime timeOfFirstDose = DateUtil.now().withHourOfDay(10).withMinuteOfHour(30);
        assertEquals(100.0, dailyReminderAdherenceService.getAdherenceInPercentage("patientId", timeOfFirstDose));
    }

    @Test
    public void adherenceWhenADoseOfAKindIsMissed() {
        PillRegimenResponse regimenStartingToday = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today(), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(11, 30), DateUtil.today(), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingToday));

        /*Only one dose is taken*/
        DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today());
        allDosageAdherenceLogs.add(dosageAdherenceLog);
        markForDeletion(dosageAdherenceLog);

        DateTime timeOfSecondDose = DateUtil.now().withHourOfDay(11).withMinuteOfHour(30);
        assertEquals(50.0, dailyReminderAdherenceService.getAdherenceInPercentage("patientId", timeOfSecondDose));
    }

    @Test
    public void adherenceAfterTheFourthWeekOfADosage() {
        PillRegimenResponse regimenStartingFiveWeeksAgo = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today().minusWeeks(5), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(regimenStartingFiveWeeksAgo));

        /*Dose status five weeks back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(5)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(5).plusDays(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(5).plusDays(2)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.NOT_TAKEN, DateUtil.today().minusWeeks(5).plusDays(3)));

        /*Dose status four weeks back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.NOT_TAKEN, DateUtil.today().minusWeeks(4).plusDays(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4).plusDays(2)));

        /*Dose status three weeks back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(3)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.WILL_TAKE_LATER, DateUtil.today().minusWeeks(3).plusDays(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(3).plusDays(2)));

        /*Dose status two weeks back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(2)));

        /*Dose status a week back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(1)));

        /*Dose status this week*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today()));

        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        // 7/28
        assertEquals(25.0, dailyReminderAdherenceService.getAdherenceInPercentage("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceAfterTheFourthWeekForMoreThanOneDosage() {
        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today().minusWeeks(5), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(16, 30), DateUtil.today().minusWeeks(5), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

        /*Dose status five weeks back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(5)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.NOT_TAKEN, DateUtil.today().minusWeeks(5)));

        /*Dose status four weeks back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(4)));

        /*Dose status three weeks back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(3)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(3)));

        /*Dose status two weeks back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.NOT_TAKEN, DateUtil.today().minusWeeks(2)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(2)));

        /*Dose status a week back*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusWeeks(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.WILL_TAKE_LATER, DateUtil.today().minusWeeks(1)));

        /*Dose status this week*/
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.WILL_TAKE_LATER, DateUtil.today()));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, DateUtil.today()));

        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        int dosesTakenTheLastFourWeeks = 7;
        int numberOfDosages = 2;
        double totalDosesInFourWeeks = DOSES_IN_FOUR_WEEKS * numberOfDosages;
        DateTime timeOfSecondDosage = DateUtil.now().withHourOfDay(16).withMinuteOfHour(30);
        assertEquals(dosesTakenTheLastFourWeeks / totalDosesInFourWeeks * 100, dailyReminderAdherenceService.getAdherenceInPercentage("patientId", timeOfSecondDosage));
    }

}
