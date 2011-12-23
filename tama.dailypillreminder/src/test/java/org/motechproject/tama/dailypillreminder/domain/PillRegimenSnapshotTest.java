package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class PillRegimenSnapshotTest {
    private DailyPillReminderContextForTest ivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        ivrContext = new DailyPillReminderContextForTest(new TAMAIVRContextForTest());
        ivrContext.dosageId("currentDosageId");
    }

    @Test
    public void shouldGetListOfMedicinesForCurrentDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        List<String> medicines = pillRegimenSnapshot.medicinesForCurrentDose();
        assertEquals(2, medicines.size());
        assertEquals("pillmedicine1", medicines.get(0));
        assertEquals("pillmedicine2", medicines.get(1));
    }

    @Test
    public void shouldGetListOfMedicinesForPreviousDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        List<String> medicines = pillRegimenSnapshot.medicinesForPreviousDose();
        assertEquals(1, medicines.size());
        assertEquals("pillmedicine3", medicines.get(0));
    }

    @Test
    public void shouldGetPreviousDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        DosageResponse previousDosage = pillRegimenSnapshot.getPreviousDose();
        assertEquals("previousDosageId", previousDosage.getDosageId());
    }

    @Test
    public void shouldGetNextDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        DosageResponse previousDosage = pillRegimenSnapshot.getNextDose();
        assertEquals("nextDosageId", previousDosage.getDosageId());
    }

    @Test
    public void previousAndNextDosagesIsSameAsCurrentDosageWhenItsTheOnlyDosageForARegimen() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        pillRegimen.getDosages().remove(0);
        pillRegimen.getDosages().remove(1);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertEquals("currentDosageId", pillRegimenSnapshot.getPreviousDose().getDosageId());
        assertEquals("currentDosageId", pillRegimenSnapshot.getNextDose().getDosageId());
    }

    @Test
    public void previousDosageIsTakenWhenTheCurrentDosageIsTheVeryFirstDosageOfTheRegimen() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate lastTakenDate = null;
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, lastTakenDate, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void previousDosageIsNotTakenWhenNotTakenThePreviousDay() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.newDateTime(DateUtil.today(), 10, 15, 0));
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate startDate = DateUtil.today().minusDays(2);

        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), startDate, null, startDate, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        assertFalse(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void previousDosageIsNotTakenWhenYesterdaysDoseWasNotTaken() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate currentDosageLastTakenDate = DateUtil.today();
        LocalDate previousDosageLastTakenDate = currentDosageLastTakenDate.minusDays(2);

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today().minusDays(3), null, previousDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today().minusDays(3), null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertFalse(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void previousDosageIsTakenWhenYesterdaysDoseWasTaken() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate currentDosageLastTakenDate = DateUtil.today();
        LocalDate previousDosageLastTakenDate = currentDosageLastTakenDate.minusDays(1);

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today().minusDays(3), null, previousDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today().minusDays(3), null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        assertTrue(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void shouldGetTheCurrentDosageFromTheRegimenWhenParametersAreEmpty() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate currentDosageLastTakenDate = DateUtil.today().minusDays(1);
        DateTime testCallTime = DateUtil.now().withHourOfDay(8).withMinuteOfHour(6).withSecondOfMinute(0);
        LocalDate startDate = new LocalDate(2010, 1, 1);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), startDate, null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), startDate, null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertNotNull(pillRegimenSnapshot.getCurrentDose());
        assertEquals(10, pillRegimenSnapshot.getCurrentDose().getDosageHour());
    }

    @Test
    public void isTakenShouldReturnTrueIfAResponseToTheDosageWasCapturedToday() {
        ivrContext.callDirection(CallDirection.Outbound);
        List<DosageResponse> dosages = new ArrayList<DosageResponse>() {{
            add(new DosageResponse("currentDosageId", new Time(22, 5), DateUtil.today(), null, DateUtil.today(), new ArrayList<MedicineResponse>()));
        }};

        DateTime testCallTime = DateUtil.now().withHourOfDay(22).withMinuteOfHour(5).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isCurrentDoseTaken());
    }

    @Test
    public void currentDosageIsCapturedTheNextDay() throws Exception {
        List<DosageResponse> dosages = Arrays.asList((new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 10), null, new LocalDate(2010, 10, 10), new ArrayList<MedicineResponse>())));
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        ivrContext.pillRegimen(pillRegimen).callDirection(CallDirection.Inbound).callStartTime(DateUtil.newDateTime(DateUtil.newDate(2010, 10, 11), 15, 40, 0));
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        assertTrue(pillRegimenSnapshot.isCurrentDoseTaken());
    }

    @Test
    public void isTakenShouldReturnTrue_NowBeforePillWindowOfVeryFirstDosage() {
        List<DosageResponse> dosages = new ArrayList<DosageResponse>() {{
            add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));
        }};

        DateTime testCallTime = DateUtil.now().withHourOfDay(6).withMinuteOfHour(5).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isCurrentDoseTaken());
    }

    @Test
    public void getCurrentDosageShouldReturnLastDosageIfCallIsMadeBeforeTimeForFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate dosageStartDate = DateUtil.today().minusDays(3);
        LocalDate currentDosageLastTakenDate = dosageStartDate.minusDays(1);
        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), dosageStartDate, null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), dosageStartDate, null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).minusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertEquals(dosages.get(0).getDosageHour(), pillRegimenSnapshot.getCurrentDose().getDosageHour());
    }

    @Test
    public void getCurrentDosageShouldReturnFirstDosageIfCallIsMade_AfterLastDosage_WithinPillWindowOfTomorrowsFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(1, 5), DateUtil.today().minusDays(2), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        assertEquals(DateUtil.today().plusDays(1), pillRegimenSnapshot.getCurrentDose().getDate());
    }

    @Test
    public void isTimeToTakeCurrentPillShouldReturnTrueIfNowBeforeDosageHour_WithinPillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).minusHours(2).withMinuteOfHour(6).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isTimeToTakeCurrentPill());
    }

    @Test
    public void isTimeToTakeCurrentPillShouldReturnTrueIfNowAfterDosageHour_WithinPillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).plusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isTimeToTakeCurrentPill());
    }

    @Test
    public void isTimeToTakeCurrentPillShouldReturnFalseIfNowBeforeDosageHour_OutsidePillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).minusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertFalse(pillRegimenSnapshot.isTimeToTakeCurrentPill());
    }

    @Test
    public void isTimeToTakeCurrentPillShouldReturnFalseIfNowAfterDosageHour_OutsidePillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).plusHours(2).withMinuteOfHour(6).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertFalse(pillRegimenSnapshot.isTimeToTakeCurrentPill());
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnTrueIfNowBeforeDosageHour_OutSideDosageInterval_WithinPillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).withMinuteOfHour(5).minusMinutes(16).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isEarlyToTakeDose(15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnTrueWhenDosagesStartToday_AndCallTimeIsBeforePillWindowForEitherDosages() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).minusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isEarlyToTakeDose(15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnFalseIfNowBeforeDosageHour_WithinDosageInterval() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).withMinuteOfHour(5).minusMinutes(14).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertFalse(pillRegimenSnapshot.isEarlyToTakeDose(15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnTrue_NextDoseTimeIsTomorrow_AndCallTimeWithinDosageInterval() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(1, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().minusDays(1).withHourOfDay(23).withMinuteOfHour(59);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isEarlyToTakeDose(15));
    }

    @Test
    public void isLateToTakeDosageShouldReturnTrueIfNowAfterDosageHour_OutsidePillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).plusHours(2).withMinuteOfHour(6).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isLateToTakeDose());
    }

    @Test
    public void isLateToTakeDosageShouldReturnFalseIfNowAfterDosageHour_WithinPillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).plusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertFalse(pillRegimenSnapshot.isLateToTakeDose());
    }

    @Test
    public void isLateToTakeTheDosageShouldReturnTrue_WhenDosageDateIsYesterday() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(23, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(1).withMinuteOfHour(15).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isLateToTakeDose());
    }

    @Test
    public void shouldReturnTrueIfCallTimeIsWithinDosageInterval() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(21, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(20).withMinuteOfHour(50).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.hasTakenDosageOnTime(15));
    }

    @Test
    public void shouldReturnFalseIfCallTimeIsNotWithinDosageInterval() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(21, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(20).withMinuteOfHour(40).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertFalse(pillRegimenSnapshot.hasTakenDosageOnTime(15));
    }

    @Test
    public void getNextDosageTimeShouldReturnToday_WhenCallMadeBeforeVeryFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        dosages.add(new DosageResponse("currentDosageId", new Time(21, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        DateTime testCallTime = DateUtil.now().withHourOfDay(15).withMinuteOfHour(40).withSecondOfMinute(0);

        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertEquals(DateUtil.today(), new LocalDate(pillRegimenSnapshot.getNextDoseTime()));

    }

    @Test
    public void previousDosageIsCapturedWhenDoseStarts() throws Exception {
        List<DosageResponse> dosages = Arrays.asList((new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 10), null, null, new ArrayList<MedicineResponse>())));
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        ivrContext.pillRegimen(pillRegimen).callDirection(CallDirection.Inbound).callStartTime(DateUtil.newDateTime(DateUtil.newDate(2010, 10, 11), 15, 40, 0));
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        assertTrue(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void previousDosageIsCaptured() throws Exception {
        List<DosageResponse> dosages = Arrays.asList((new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 9), null, null, new ArrayList<MedicineResponse>())));
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        ivrContext.pillRegimen(pillRegimen).callDirection(CallDirection.Inbound).callStartTime(DateUtil.newDateTime(DateUtil.newDate(2010, 10, 11), 15, 40, 0));
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        assertFalse(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    private PillRegimenResponse getPillRegimenResponse() {
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        dosageResponses.add(new DosageResponse("currentDosageId", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), new LocalDate(2011, 8, 4), medicineResponses));
        dosageResponses.add(new DosageResponse("previousDosageId", new Time(15, 5), new LocalDate(2011, 7, 10), new LocalDate(2012, 7, 10), new LocalDate(2011, 8, 4), medicineResponses));
        return new PillRegimenResponse("r1", "p1", 2, 15, dosageResponses);
    }
}
