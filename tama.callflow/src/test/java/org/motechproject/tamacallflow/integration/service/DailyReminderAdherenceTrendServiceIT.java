package org.motechproject.tamacallflow.integration.service;

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
import org.motechproject.tamacallflow.domain.DosageAdherenceLog;
import org.motechproject.tamacallflow.domain.DosageStatus;
import org.motechproject.tamacallflow.domain.PillRegimen;
import org.motechproject.tamacallflow.repository.AllDosageAdherenceLogs;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.tamacallflow.service.PatientAlertService;
import org.motechproject.tamacallflow.service.TAMAPillReminderService;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Properties;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@PrepareForTest(DateUtil.class)
@ContextConfiguration(locations = "classpath*:applicationCallFlowContext.xml", inheritLocations = false)
public class DailyReminderAdherenceTrendServiceIT extends SpringIntegrationTest {

    @Mock
    private TAMAPillReminderService pillReminderService;

    @Mock
    private PatientAlertService patientAlertService;

    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Autowired
    @Qualifier("ivrProperties")
    private Properties ivrProperties;

    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    private DailyReminderAdherenceService dailyReminderAdherenceService;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() {
        initMocks(this);
        dailyReminderAdherenceService = new DailyReminderAdherenceService(allDosageAdherenceLogs, pillReminderService, ivrProperties);
        dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(patientAlertService, dailyReminderAdherenceService);
    }


    @Test
    public void adherenceTrendIsFallingWhenAdherenceTodayIsLessThanAdherenceOnLastWeek_SingleDoseRegimen() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();

        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), today.minusWeeks(5), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(5)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(5).plusDays(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(5).plusDays(2)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(4)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(4).plusDays(2)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(3)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(3).plusDays(2)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(2)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today));
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        assertTrue(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceTrendIsFallingWhenAdherenceTodayIsLessThanAdherenceOnLastWeek_DoubleDoseRegimen() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();

        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), today.minusWeeks(5), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(16, 30), today.minusWeeks(5), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(5)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.NOT_TAKEN, today.minusWeeks(5)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, today.minusWeeks(5).plusDays(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(4)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, today.minusWeeks(4)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(3)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, today.minusWeeks(3)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.NOT_TAKEN, today.minusWeeks(2)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, today.minusWeeks(2)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.WILL_TAKE_LATER, today.minusWeeks(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.WILL_TAKE_LATER, today));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, today));
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        assertTrue(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceTrendIsNotFallingWhenAdherenceTodayIsNotLessThanAdherenceOnLastWeek_SingleDoseRegimen() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();

        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), today.minusWeeks(5), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(5)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(4)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(2)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(1).plusDays(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today));
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        assertFalse(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceTrendIsNotFallingWhenAdherenceTodayIsNotLessThanAdherenceOnLastWeek_DoubleDoseRegimen() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();

        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), today.minusWeeks(5), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(16, 30), today.minusWeeks(5), null, null, null));
        }});
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(5)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.NOT_TAKEN, today.minusWeeks(5)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(4)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", DosageStatus.TAKEN, today.minusWeeks(3)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.WILL_TAKE_LATER, today.minusWeeks(1)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", DosageStatus.TAKEN, today));
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        assertFalse(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf("patientId", DateUtil.now()));
    }
}
