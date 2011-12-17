package org.motechproject.tamacallflow.ivr.command;

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
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.repository.AllDosageAdherenceLogs;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
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
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Mock
    private DailyReminderAdherenceService dailyReminderAdherenceService;

    private MissedPillFeedbackCommand forMissedPillFeedbackCommand;

    @Before
    public void setup() {
        initMocks(this);
        forMissedPillFeedbackCommand = new MissedPillFeedbackCommand(allDosageAdherenceLogs, null, dailyReminderAdherenceTrendService, dailyReminderAdherenceService);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheFirstTime() {
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        dosageResponses.add(new DosageResponse("d1", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), DateUtil.today(), null));
        dosageResponses.add(new DosageResponse("d2", new Time(15, 5), new LocalDate(2011, 7, 5), new LocalDate(2012, 7, 5), DateUtil.today(), null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        TAMAIVRContextForTest context = new TAMAIVRContextForTest().callStartTime(new DateTime(2011, 8, 4, 12, 0)).patientId("p1").dosageId("d1").pillRegimen(pillRegimenResponse);

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(64);

        assertArrayEquals(new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_FIRST_TIME}, forMissedPillFeedbackCommand.executeCommand(context));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheSecondTime() {
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        dosageResponses.add(new DosageResponse("d1", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), DateUtil.today(), null));
        dosageResponses.add(new DosageResponse("d2", new Time(15, 5), new LocalDate(2011, 7, 5), new LocalDate(2012, 7, 5), DateUtil.today(), null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        TAMAIVRContextForTest context = new TAMAIVRContextForTest().callStartTime(new DateTime(2011, 8, 4, 12, 0)).patientId("p1").dosageId("d1").pillRegimen(pillRegimenResponse);

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(63);

        assertArrayEquals(new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME}, forMissedPillFeedbackCommand.executeCommand(context));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceMoreThan90() {
        final LocalDate dosage1StartDate = new LocalDate(2011, 7, 1);
        final LocalDate dosage1EndDate = dosage1StartDate.plusWeeks(4);
        final LocalDate dosage2StartDate = dosage1StartDate.plusDays(4);
        final LocalDate dosage2EndDate = dosage2StartDate.plusWeeks(4);
        List<DosageResponse> dosageResponses = Arrays.asList(
                new DosageResponse("d1", new Time(9, 5), dosage1StartDate, dosage1EndDate, DateUtil.today(), null),
                new DosageResponse("d2", new Time(15, 5), dosage2StartDate, dosage2EndDate, DateUtil.today(), null)
        );
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        TAMAIVRContextForTest context = new TAMAIVRContextForTest().callStartTime(new DateTime(2012, 8, 4, 12, 0, 0, 0)).patientId("p1").dosageId("d1").pillRegimen(pillRegimenResponse);

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(51);
        when(dailyReminderAdherenceService.getAdherenceInPercentage(same("p1"), Matchers.<DateTime>any())).thenReturn(91.0);

        assertArrayEquals(new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_MORE_THAN_90}, forMissedPillFeedbackCommand.executeCommand(context));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceBetween70And90() {
        final LocalDate dosage1StartDate = new LocalDate(2011, 7, 1);
        final LocalDate dosage1EndDate = dosage1StartDate.plusWeeks(4);
        final LocalDate dosage2StartDate = dosage1StartDate.plusDays(4);
        final LocalDate dosage2EndDate = dosage2StartDate.plusWeeks(4);
        List<DosageResponse> dosageResponses = Arrays.asList(
                new DosageResponse("d1", new Time(9, 5), dosage1StartDate, dosage1EndDate, DateUtil.today(), null),
                new DosageResponse("d2", new Time(15, 5), dosage2StartDate, dosage2EndDate, DateUtil.today(), null)
        );
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        TAMAIVRContextForTest context = new TAMAIVRContextForTest().callStartTime(new DateTime(2012, 8, 4, 12, 0, 0, 0)).patientId("p1").dosageId("d1").pillRegimen(pillRegimenResponse);

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(51);
        when(dailyReminderAdherenceService.getAdherenceInPercentage(same("p1"), Matchers.<DateTime>any())).thenReturn(89.0);

        assertArrayEquals(new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90}, forMissedPillFeedbackCommand.executeCommand(context));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceLessThan70() {
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        dosageResponses.add(new DosageResponse("d1", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), DateUtil.today(), null));
        dosageResponses.add(new DosageResponse("d2", new Time(15, 5), new LocalDate(2011, 7, 5), new LocalDate(2012, 7, 5), DateUtil.today(), null));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "p1", 0, 0, dosageResponses);

        TAMAIVRContextForTest context = new TAMAIVRContextForTest().callStartTime(new DateTime(2011, 8, 4, 12, 0)).patientId("p1").dosageId("d1").pillRegimen(pillRegimenResponse);

        when(allDosageAdherenceLogs.getDosageTakenCount("regimen_id")).thenReturn(60);
        LocalDate today = DateUtil.today();
        when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount("regimen_id", today, today.minusDays(28))).thenReturn(56 * 69 / 100);
        String[] message = forMissedPillFeedbackCommand.executeCommand(context);
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(TamaIVRMessage.MISSED_PILL_FEEDBACK_LESS_THAN_70, message[0]);
    }
}