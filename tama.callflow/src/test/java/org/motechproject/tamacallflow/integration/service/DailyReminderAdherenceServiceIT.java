package org.motechproject.tamacallflow.integration.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.domain.PillRegimen;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.PatientAlertService;
import org.motechproject.tamacallflow.service.TAMAPillReminderService;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamadomain.domain.DosageAdherenceLog;
import org.motechproject.tamadomain.domain.DosageStatus;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
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
@ContextConfiguration(locations = "classpath*:applicationContext-TAMACallFlow.xml", inheritLocations = false)
public class DailyReminderAdherenceServiceIT extends SpringIntegrationTest {

    @Mock
    private TAMAPillReminderService pillReminderService;

    @Mock
    private PatientAlertService patientAlertService;

    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    private DailyReminderAdherenceService dailyReminderAdherenceService;

    @Qualifier("ivrProperties")
    private Properties properties;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() {
        initMocks(this);
        dailyReminderAdherenceService = new DailyReminderAdherenceService(allDosageAdherenceLogs, pillReminderService, properties);
        setUpDate();
    }

    public void setUpDate(){
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();
        PowerMockito.stub(method(DateUtil.class, "today")).toReturn(today);
    }

    @Test
    public void adherenceAsOfToday_ForSingleDoseRegimen() {
        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), DateUtil.today().minusWeeks(5), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

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

        int dosesTakenTheLastForWeeks = 7;
        double totalDoses = 28.0;
        assertEquals(dosesTakenTheLastForWeeks / totalDoses, dailyReminderAdherenceService.getAdherence("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceAsOfToday_ForTwoDoseRegimen() {
        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), DateUtil.today().minusWeeks(5), null, null, null));
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

        int dosagesTakenTheLastFourWeeks = 7;
        double totalDoses = 56.0;
        assertEquals(dosagesTakenTheLastFourWeeks / totalDoses, dailyReminderAdherenceService.getAdherence("patientId", DateUtil.now()));
    }
}
