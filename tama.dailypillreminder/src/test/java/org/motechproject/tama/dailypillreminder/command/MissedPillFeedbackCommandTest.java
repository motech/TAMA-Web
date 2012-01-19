package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MissedPillFeedbackCommandTest {
    @Mock
    AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Mock
    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Mock
    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    private MissedPillFeedbackCommand forMissedPillFeedbackCommand;

    @Before
    public void setup() {
        initMocks(this);
        forMissedPillFeedbackCommand = new MissedPillFeedbackCommand(allDosageAdherenceLogs, null, dailyReminderAdherenceTrendService, dailyReminderAdherenceService, null);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheFirstTime() {
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        dosageResponses.add(new DosageResponse("d1", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), DateUtil.today(), null));
        dosageResponses.add(new DosageResponse("d2", new Time(15, 5), new LocalDate(2011, 7, 5), new LocalDate(2012, 7, 5), DateUtil.today(), null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        LocalDate callDate = DateUtil.newDate(2011, 8, 4);
        DailyPillReminderContextForTest context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).pillRegimen(new PillRegimen(pillRegimenResponse)).callStartTime(DateUtil.newDateTime(callDate, 12, 0, 0)).patientDocumentId("p1");

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(64);
        when(allDosageAdherenceLogs.countByDosageDate("regimen_id", new LocalDate(0), callDate)).thenReturn(65);

        assertArrayEquals(new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_FIRST_TIME}, forMissedPillFeedbackCommand.executeCommand(context));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheSecondTime() {
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        dosageResponses.add(new DosageResponse("d1", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), DateUtil.today(), null));
        dosageResponses.add(new DosageResponse("d2", new Time(15, 5), new LocalDate(2011, 7, 5), new LocalDate(2012, 7, 5), DateUtil.today(), null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        LocalDate callDate = DateUtil.newDate(2011, 8, 4);
        DailyPillReminderContextForTest context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).pillRegimen(pillRegimenResponse).callStartTime(DateUtil.newDateTime(callDate, 12, 0, 0)).patientDocumentId("p1");

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(63);
        when(allDosageAdherenceLogs.countByDosageDate("regimen_id", new LocalDate(0), callDate)).thenReturn(65);

        assertArrayEquals(new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME}, forMissedPillFeedbackCommand.executeCommand(context));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceMoreThan90() throws NoAdherenceRecordedException {
        final LocalDate dosage1StartDate = new LocalDate(2011, 7, 1);
        final LocalDate dosage1EndDate = dosage1StartDate.plusWeeks(4);
        final LocalDate dosage2StartDate = dosage1StartDate.plusDays(4);
        final LocalDate dosage2EndDate = dosage2StartDate.plusWeeks(4);
        List<DosageResponse> dosageResponses = Arrays.asList(
                new DosageResponse("d1", new Time(9, 5), dosage1StartDate, dosage1EndDate, DateUtil.today(), null),
                new DosageResponse("d2", new Time(15, 5), dosage2StartDate, dosage2EndDate, DateUtil.today(), null)
        );
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        DailyPillReminderContextForTest context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).pillRegimen(pillRegimenResponse).callStartTime(new DateTime(2012, 8, 4, 12, 0, 0, 0)).patientDocumentId("p1");

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(51);
        when(dailyReminderAdherenceService.getAdherencePercentage(same("p1"), Matchers.<DateTime>any())).thenReturn(91.0);

        assertArrayEquals(new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_MORE_THAN_90}, forMissedPillFeedbackCommand.executeCommand(context));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceBetween70And90() throws NoAdherenceRecordedException {
        final LocalDate dosage1StartDate = new LocalDate(2011, 7, 1);
        final LocalDate dosage1EndDate = dosage1StartDate.plusWeeks(4);
        final LocalDate dosage2StartDate = dosage1StartDate.plusDays(4);
        final LocalDate dosage2EndDate = dosage2StartDate.plusWeeks(4);
        List<DosageResponse> dosageResponses = Arrays.asList(
                new DosageResponse("d1", new Time(9, 5), dosage1StartDate, dosage1EndDate, DateUtil.today(), null),
                new DosageResponse("d2", new Time(15, 5), dosage2StartDate, dosage2EndDate, DateUtil.today(), null)
        );
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        DailyPillReminderContextForTest context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).pillRegimen(pillRegimenResponse).callStartTime(new DateTime(2012, 8, 4, 12, 0, 0, 0)).patientDocumentId("p1");

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(51);
        when(dailyReminderAdherenceService.getAdherencePercentage(same("p1"), Matchers.<DateTime>any())).thenReturn(89.0);

        assertArrayEquals(new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90}, forMissedPillFeedbackCommand.executeCommand(context));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceLessThan70() {
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        dosageResponses.add(new DosageResponse("d1", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), DateUtil.today(), null));
        dosageResponses.add(new DosageResponse("d2", new Time(15, 5), new LocalDate(2011, 7, 5), new LocalDate(2012, 7, 5), DateUtil.today(), null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        DailyPillReminderContextForTest context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).pillRegimen(pillRegimenResponse).callStartTime(new DateTime(2011, 8, 4, 12, 0)).patientDocumentId("p1");

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(60);
        LocalDate today = DateUtil.today();
        String[] message = forMissedPillFeedbackCommand.executeCommand(context);
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(TamaIVRMessage.MISSED_PILL_FEEDBACK_LESS_THAN_70, message[0]);
    }
}