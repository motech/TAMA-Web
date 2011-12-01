package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MissedPillFeedbackCommandTest {
    @Mock
    AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Mock
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    private MissedPillFeedbackCommand forMissedPillFeedbackCommand;
    private final String REGIMEN_ID = "r1";
    private final int TOTAL_DOSAGE_COUNT = 56;
    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 4, 12, 0));
        when(DateUtil.today()).thenReturn(new LocalDate(2011, 8, 4));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 9, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 5), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 5, 15, 5, 0));

        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        dosageResponses.add(new DosageResponse("d1", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), DateUtil.today(), medicineResponses));
        dosageResponses.add(new DosageResponse("d2", new Time(15, 5), new LocalDate(2011, 7, 5), new LocalDate(2012, 7, 5), DateUtil.today(), medicineResponses));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse(REGIMEN_ID, "p1", 0, 0, dosageResponses);

        context = new TAMAIVRContextForTest().callStartTime(new DateTime(2011, 8, 4, 12, 0)).patientId("p1").dosageId("d1").pillRegimen(pillRegimenResponse);
        forMissedPillFeedbackCommand = new MissedPillFeedbackCommand(allDosageAdherenceLogs, null, dailyReminderAdherenceTrendService);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheFirstTime() {
        when(allDosageAdherenceLogs.getDosageTakenCount(REGIMEN_ID)).thenReturn(64);
        assertArrayEquals(new String[]{TamaIVRMessage.MISSED_PILL_FEEDBACK_FIRST_TIME}, forMissedPillFeedbackCommand.executeCommand(context));
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForTheSecondTime() {
        when(allDosageAdherenceLogs.getDosageTakenCount(REGIMEN_ID)).thenReturn(63);
        String[] message = forMissedPillFeedbackCommand.executeCommand(context);
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(TamaIVRMessage.MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME, message[0]);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceMoreThan90() {
        when(allDosageAdherenceLogs.getDosageTakenCount(REGIMEN_ID)).thenReturn(60);
        when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(TOTAL_DOSAGE_COUNT * 95 / 100);
        String[] message = forMissedPillFeedbackCommand.executeCommand(context);
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(TamaIVRMessage.MISSED_PILL_FEEDBACK_MORE_THAN_90, message[0]);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceBetween70And90() {
        when(allDosageAdherenceLogs.getDosageTakenCount(REGIMEN_ID)).thenReturn(60);
        when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(TOTAL_DOSAGE_COUNT * 90 / 100);
        String[] message = forMissedPillFeedbackCommand.executeCommand(context);
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(TamaIVRMessage.MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90, message[0]);
    }

    @Test
    public void shouldReturnMissedPillFeedbackWhenDosageMissedForMoreThanFourTimesAndAdherenceLessThan70() {
        when(allDosageAdherenceLogs.getDosageTakenCount(REGIMEN_ID)).thenReturn(60);
        LocalDate today = DateUtil.today();
        when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(REGIMEN_ID, today, today.minusDays(28))).thenReturn(TOTAL_DOSAGE_COUNT * 69 / 100);
        String[] message = forMissedPillFeedbackCommand.executeCommand(context);
        Assert.assertEquals(1, message.length);
        Assert.assertEquals(TamaIVRMessage.MISSED_PILL_FEEDBACK_LESS_THAN_70, message[0]);
    }
}