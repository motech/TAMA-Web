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
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@PrepareForTest(DateUtil.class)
@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class DailyReminderAdherenceTrendServiceIT extends SpringIntegrationTest {

    private static final String PATIENT_ID = "patientId";

    @Mock
    private DailyPillReminderService dailyPillReminderService;

    @Mock
    private PatientAlertService patientAlertService;

    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Mock
    private AllPatients allPatients;

    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    @Qualifier("ivrProperties")
    private Properties ivrProperties;

    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() {
        initMocks(this);
        dailyReminderAdherenceService = new DailyPillReminderAdherenceService(allDosageAdherenceLogs, dailyPillReminderService, ivrProperties, new AdherenceService(), allPatients, allTreatmentAdvices);
        dailyReminderAdherenceTrendService = new DailyPillReminderAdherenceTrendService(patientAlertService, dailyReminderAdherenceService);
        Patient patient = new PatientBuilder().withDefaults().withStatus(Status.Active).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
    }

    @Test
    public void adherenceTrendIsFallingWhenAdherenceTodayIsLessThanAdherenceOnLastWeek_SingleDoseRegimen() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();

        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), today.minusWeeks(5), null, null, null));
        }});
        when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(5), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(5), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(5).plusDays(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(5).plusDays(1), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(5).plusDays(2), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(5).plusDays(2), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(4), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(4), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(4).plusDays(2), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(4).plusDays(2), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(3), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(3), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(3).plusDays(2), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(3).plusDays(2), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(2), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(2), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.NOT_RECORDED, today.minusWeeks(1).plusDays(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1).plusDays(1), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.NOT_TAKEN, today.minusWeeks(1).plusDays(2), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1).plusDays(2), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.NOT_RECORDED, today.minusWeeks(1).plusDays(3), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1).plusDays(3), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today, new Time(10, 5), DateUtil.newDateTime(today, 0, 0, 0)));
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        assertTrue(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceTrendIsFallingWhenAdherenceTodayIsLessThanAdherenceOnLastWeek_DoubleDoseRegimen() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();

        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), today.minusWeeks(5), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(16, 30), today.minusWeeks(5), null, null, null));
        }});
        when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", null, DosageStatus.TAKEN, today.minusWeeks(5), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(5), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.NOT_TAKEN, today.minusWeeks(5), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(5), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.TAKEN, today.minusWeeks(5).plusDays(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(5).plusDays(1), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", null, DosageStatus.TAKEN, today.minusWeeks(4), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(4), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.TAKEN, today.minusWeeks(4), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(4), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", null, DosageStatus.TAKEN, today.minusWeeks(3), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(3), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.TAKEN, today.minusWeeks(3), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(3), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", null, DosageStatus.NOT_TAKEN, today.minusWeeks(2), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(2), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.TAKEN, today.minusWeeks(2), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(2), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", null, DosageStatus.TAKEN, today.minusWeeks(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.WILL_TAKE_LATER, today.minusWeeks(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id", null, DosageStatus.WILL_TAKE_LATER, today, new Time(10, 5), DateUtil.newDateTime(today, 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.TAKEN, today, new Time(10, 5), DateUtil.newDateTime(today, 0, 0, 0)));
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        assertTrue(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceTrendIsNotFallingWhenAdherenceTodayIsNotLessThanAdherenceOnLastWeek_SingleDoseRegimen() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();

        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), today.minusWeeks(5), null, null, null));
        }});
        when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(5), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(5), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(4), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(4), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(2), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(2), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(1).plusDays(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1).plusDays(1), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today, new Time(10, 5), DateUtil.newDateTime(today, 0, 0, 0)));
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        assertFalse(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf("patientId", DateUtil.now()));
    }

    @Test
    public void adherenceTrendIsNotFallingWhenAdherenceTodayIsNotLessThanAdherenceOnLastWeek_DoubleDoseRegimen() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();

        PillRegimenResponse pillRegimen = new PillRegimenResponse("pillRegimenId", "patientId", 2, 5, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(5, 30), today.minusWeeks(5), null, null, null));
            add(new DosageResponse("dosage2Id", new Time(16, 30), today.minusWeeks(5), null, null, null));
        }});
        when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(new PillRegimen(pillRegimen));

        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(5), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(5), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.NOT_TAKEN, today.minusWeeks(5), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(5), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(4), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(4), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage1Id",  null, DosageStatus.TAKEN, today.minusWeeks(3), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(3), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.WILL_TAKE_LATER, today.minusWeeks(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0)));
        allDosageAdherenceLogs.add(new DosageAdherenceLog("patientId", "pillRegimenId", "dosage2Id", null, DosageStatus.TAKEN, today, new Time(10, 5), DateUtil.newDateTime(today, 0, 0, 0)));
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());

        assertFalse(dailyReminderAdherenceTrendService.isAdherenceFallingAsOf("patientId", DateUtil.now()));
    }
}
