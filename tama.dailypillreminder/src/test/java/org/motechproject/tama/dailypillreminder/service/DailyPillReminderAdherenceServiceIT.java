package org.motechproject.tama.dailypillreminder.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.dailypillreminder.builder.DosageAdherenceLogBuilder;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class DailyPillReminderAdherenceServiceIT extends SpringIntegrationTest {

    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    @Autowired
    private AdherenceService adherenceService;
    @Autowired
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private DailyPillReminderService dailyPillReminderService;

    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;

    @Before
    public void before() {
        super.before();
        initMocks(this);
        dailyPillReminderAdherenceService = new DailyPillReminderAdherenceService(allDosageAdherenceLogs, dailyPillReminderService, null, adherenceService, null, allTreatmentAdvices);
    }

    @After
    public void after() {
        for (DosageAdherenceLog log : allDosageAdherenceLogs.getAll())
            markForDeletion(log);
        super.after();
    }

    @Test
    public void anyDoseMissedLastWeekShouldReturnFalse_WhenNoLogsAreAvailableForPreviousWeek() {
        Patient patient = PatientBuilder.startRecording().withId("patientId").build();
        final PillRegimen pillRegimen = new PillRegimen(new PillRegimenResponse("regimenId", null, 1, 10, 5, null));
        when(dailyPillReminderService.getPillRegimen(patient.getId())).thenReturn(pillRegimen);
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusWeeks(1)).withDosageStatus(DosageStatus.TAKEN).build());

        final boolean missedDose = dailyPillReminderAdherenceService.wasAnyDoseMissedLastWeek(patient);
        assertFalse(missedDose);
    }

    @Test
    public void anyDoseMissedLastWeekShouldReturnTrue_WhenDoseIsMissedInThePreviousWeek() {
        Patient patient = PatientBuilder.startRecording().withId("patientId").build();
        final PillRegimen pillRegimen = new PillRegimen(new PillRegimenResponse("regimenId", null, 1, 10, 5, null));
        when(dailyPillReminderService.getPillRegimen(patient.getId())).thenReturn(pillRegimen);

        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(6)).withDosageStatus(DosageStatus.NOT_RECORDED).build());
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(5)).withDosageStatus(DosageStatus.TAKEN).build());

        assertTrue(dailyPillReminderAdherenceService.wasAnyDoseMissedLastWeek(patient));
    }

    @Test
    public void anyDoseMissedLastWeekShouldReturnFalse_WhenNoDoseIsMissedInThePreviousWeek() {
        Patient patient = PatientBuilder.startRecording().withId("patientId").build();
        final PillRegimen pillRegimen = new PillRegimen(new PillRegimenResponse("regimenId", null, 1, 10, 5, null));
        when(dailyPillReminderService.getPillRegimen(patient.getId())).thenReturn(pillRegimen);

        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(6)).withDosageStatus(DosageStatus.TAKEN).build());
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(5)).withDosageStatus(DosageStatus.TAKEN).build());
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(4)).withDosageStatus(DosageStatus.TAKEN).build());
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(3)).withDosageStatus(DosageStatus.TAKEN).build());

        assertFalse(dailyPillReminderAdherenceService.wasAnyDoseMissedLastWeek(patient));
    }

    @Test
    public void anyDoseTakenLateLastWeekShouldReturnFalse_WhenNoLogsAreAvailableForPreviousWeek() {
        Patient patient = PatientBuilder.startRecording().withId("patientId").build();
        final PillRegimen pillRegimen = new PillRegimen(new PillRegimenResponse("regimenId", null, 1, 10, 5, null));
        when(dailyPillReminderService.getPillRegimen(patient.getId())).thenReturn(pillRegimen);
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(7)).withDosageStatus(DosageStatus.TAKEN).withDoseTakenLate().build());

        assertFalse(dailyPillReminderAdherenceService.wasAnyDoseTakenLateLastWeek(patient));
    }

    @Test
    public void anyDoseTakenLateLastWeekShouldReturnTrue_WhenAtLeastOneDoseIsTakenLatePreviousWeek() {
        Patient patient = PatientBuilder.startRecording().withId("patientId").build();
        final PillRegimen pillRegimen = new PillRegimen(new PillRegimenResponse("regimenId", null, 1, 10, 5, null));
        when(dailyPillReminderService.getPillRegimen(patient.getId())).thenReturn(pillRegimen);
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(6)).withDosageStatus(DosageStatus.TAKEN).withDoseTakenLate().build());
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(5)).withDosageStatus(DosageStatus.NOT_RECORDED).build());
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(4)).withDosageStatus(DosageStatus.NOT_TAKEN).build());

        assertTrue(dailyPillReminderAdherenceService.wasAnyDoseTakenLateLastWeek(patient));
    }

    @Test
    public void anyDoseTakenLateLastWeekShouldReturnFalse_WhenNoDoseIsTakenLateLastWeek() {
        Patient patient = PatientBuilder.startRecording().withId("patientId").build();
        final PillRegimen pillRegimen = new PillRegimen(new PillRegimenResponse("regimenId", null, 1, 10, 5, null));
        when(dailyPillReminderService.getPillRegimen(patient.getId())).thenReturn(pillRegimen);
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(7)).withDosageStatus(DosageStatus.TAKEN).build());
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(7)).withDosageStatus(DosageStatus.NOT_RECORDED).build());
        allDosageAdherenceLogs.add(DosageAdherenceLogBuilder.startRecording().withDefaults().withRegimenId("regimenId").withDosageDate(DateUtil.today().minusDays(7)).withDosageStatus(DosageStatus.WILL_TAKE_LATER).build());

        assertFalse(dailyPillReminderAdherenceService.wasAnyDoseTakenLateLastWeek(patient));
    }
}
