package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PillRegimenTest extends BaseUnitTest {
    private DailyPillReminderContextForTest ivrContext;

    @Before
    public void setUp() {
        ivrContext = new DailyPillReminderContextForTest(new TAMAIVRContextForTest());
    }

    @Test
    public void shouldGetListOfMedicinesForCurrentDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(new DateTime(2012, 1, 5, 15, 30, 0));
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        List<String> medicines = pillRegimen.getDoseAt(ivrContext.callStartTime()).medicineNames();
        assertEquals(2, medicines.size());
        assertEquals("pillmedicine1", medicines.get(0));
        assertEquals("pillmedicine2", medicines.get(1));
    }

    @Test
    public void shouldGetListOfMedicinesForPreviousDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(new DateTime(2012, 1, 5, 15, 30, 0));
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        List<String> medicines = pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()).medicineNames();
        assertEquals(1, medicines.size());
        assertEquals("pillmedicine3", medicines.get(0));
    }

    @Test
    public void shouldGetPreviousDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(new DateTime(2012, 1, 5, 15, 30, 0));
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        Dose previousDosage = pillRegimen.getPreviousDoseAt(ivrContext.callStartTime());
        assertEquals("previousDosageId", previousDosage.getDosageId());
    }

    @Test
    public void shouldGetNextDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(new DateTime(2012, 1, 5, 15, 30, 0));
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        Dose nextDose= pillRegimen.getNextDoseAt(ivrContext.callStartTime());
        assertEquals("nextDosageId", nextDose.getDosageId());
    }

    @Test
    public void shouldReturnNextDoseAsTheFirstDoseWhenNeitherOfTheDosagesHaveStarted() {
        Time dosage1Time = new Time(18, 0);
        Time dosage2Time = new Time(20, 30);
        LocalDate dosage1StartDate = DateUtil.newDate(2011, 10, 10);
        LocalDate dosage2StartDate = DateUtil.newDate(2011, 10, 10);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withTwoDosages(dosage1Time, dosage1StartDate, "dosage1Id", dosage2Time, dosage2StartDate, "dosage2Id").build();
        DateTime callStartTime = DateUtil.newDateTime(DateUtil.newDate(2011, 10, 10), 14, 0, 0);
        ivrContext.callStartTime(callStartTime);

        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        Dose nextDose= pillRegimen.getNextDoseAt(ivrContext.callStartTime());

        assertEquals("dosage1Id", nextDose.getDosageId());
        assertEquals(18, nextDose.getDosageHour());
        assertEquals(0, nextDose.getDosageMinute());
        assertEquals(dosage1StartDate, nextDose.getDate());
    }

    @Test
    public void previousAndNextDosagesIsSameAsCurrentDosageWhenItsTheOnlyDosageForARegimen() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        pillRegimenResponse.getDosages().remove(0);
        pillRegimenResponse.getDosages().remove(1);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertEquals("currentDosageId", pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()).getDosageId());
        assertEquals("currentDosageId", pillRegimen.getNextDoseAt(ivrContext.callStartTime()).getDosageId());
    }

    @Test
    public void previousDosageIs_WhenTheCurrentDosageIsTheVeryFirstDosageOfTheRegimen() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate lastTakenDate = null;
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, lastTakenDate, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertNull(pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()));
    }

    @Test
    public void previousDosageIsNotTakenWhenNotTakenThePreviousDay() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.newDateTime(DateUtil.today(), 10, 15, 0));
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate startDate = DateUtil.today().minusDays(2);

        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), startDate, null, startDate, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        assertFalse(pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()).isTaken());
    }

    @Test
    public void previousDosageIsNotTakenWhenYesterdaysDoseWasNotTaken() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today().minusDays(3), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today().minusDays(3), null, null, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertFalse(pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()).isTaken());
    }

    @Test
    public void previousDosageIsTakenWhenYesterdaysDoseWasTaken() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate currentDosageLastTakenDate = DateUtil.today();
        LocalDate previousDosageLastTakenDate = currentDosageLastTakenDate.minusDays(1);

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today().minusDays(3), null, previousDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today().minusDays(3), null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        assertTrue(pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()).isTaken());
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

        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertNotNull(pillRegimen.getDoseAt(ivrContext.callStartTime()));
        assertEquals(10, pillRegimen.getDoseAt(ivrContext.callStartTime()).getDosageHour());
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
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertEquals(dosages.get(0).getDosageHour(), pillRegimen.getDoseAt(ivrContext.callStartTime()).getDosageHour());
    }

    @Test
    public void getCurrentDosageShouldReturnFirstDosageIfCallIsMade_AfterLastDosage_WithinPillWindowOfTomorrowsFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(1, 5), DateUtil.today().minusDays(2), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        assertEquals(DateUtil.today().plusDays(1), pillRegimen.getDoseAt(ivrContext.callStartTime()).getDate());
    }

    @Test
    public void isTimeToTakeCurrentPillShouldReturnTrueIfNowBeforeDosageHour_WithinPillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).minusHours(2).withMinuteOfHour(6).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertTrue(pillRegimen.isNowWithinCurrentDosePillWindow(ivrContext.callStartTime()));
    }

    @Test
    public void isTimeToTakeCurrentPillShouldReturnTrueIfNowAfterDosageHour_WithinPillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).plusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertTrue(pillRegimen.isNowWithinCurrentDosePillWindow(ivrContext.callStartTime()));
    }

    @Test
    public void isTimeToTakeCurrentPillShouldReturnFalseIfNowBeforeDosageHour_OutsidePillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).minusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertFalse(pillRegimen.isNowWithinCurrentDosePillWindow(ivrContext.callStartTime()));
    }

    @Test
    public void isTimeToTakeCurrentPillShouldReturnFalseIfNowAfterDosageHour_OutsidePillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).plusHours(2).withMinuteOfHour(6).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertFalse(pillRegimen.isNowWithinCurrentDosePillWindow(ivrContext.callStartTime()));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnTrueIfNowBeforeDosageHour_OutSideDosageInterval_WithinPillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).withMinuteOfHour(5).minusMinutes(16).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertTrue(pillRegimen.isEarlyToTakeDose(ivrContext.callStartTime(), 15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnTrueWhenDosagesStartToday_AndCallTimeIsBeforePillWindowForEitherDosages() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).minusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertTrue(pillRegimen.isEarlyToTakeDose(ivrContext.callStartTime(), 15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnFalseIfNowBeforeDosageHour_WithinDosageInterval() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).withMinuteOfHour(5).minusMinutes(14).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertFalse(pillRegimen.isEarlyToTakeDose(ivrContext.callStartTime(), 15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnTrue_NextDoseTimeIsTomorrow_AndCallTimeWithinDosageInterval() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(1, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().minusDays(1).withHourOfDay(23).withMinuteOfHour(59);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertTrue(pillRegimen.isEarlyToTakeDose(ivrContext.callStartTime(), 15));
    }

    @Test
    public void isLateToTakeDosageShouldReturnTrueIfNowAfterDosageHour_OutsidePillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).plusHours(2).withMinuteOfHour(6).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertTrue(pillRegimen.isLateToTakeDose(ivrContext.callStartTime(), 15));
    }

    @Test
    public void isLateToTakeDosageShouldReturnFalseIfNowAfterDosageHour_WithinPillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).withMinuteOfHour(19).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertFalse(pillRegimen.isLateToTakeDose(ivrContext.callStartTime(), 15));
    }

    @Test
    public void isLateToTakeTheDosageShouldReturnTrue_WhenDosageDateIsYesterday() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(23, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(1).withMinuteOfHour(15).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertTrue(pillRegimen.isLateToTakeDose(ivrContext.callStartTime(), 15));
    }

    @Test
    public void shouldReturnTrueIfCallTimeIsWithinDosageInterval() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(21, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(20).withMinuteOfHour(50).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertTrue(pillRegimen.isNowWithinCurrentDosageInterval(ivrContext.callStartTime(), 15));
    }

    @Test
    public void shouldReturnFalseIfCallTimeIsNotWithinDosageInterval() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(21, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(20).withMinuteOfHour(40).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertFalse(pillRegimen.isNowWithinCurrentDosageInterval(ivrContext.callStartTime(), 15));
    }

    @Test
    public void getNextDosageTimeShouldReturnToday_WhenCallMadeBeforeVeryFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        dosages.add(new DosageResponse("currentDosageId", new Time(21, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));
        DateTime testCallTime = DateUtil.now().withHourOfDay(15).withMinuteOfHour(40).withSecondOfMinute(0);

        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertEquals(DateUtil.today(), new LocalDate(pillRegimen.getNextDoseAt(ivrContext.callStartTime()).getDoseTime()));
    }

    @Test
    public void previousDosageIsCapturedWhenDoseStarts() throws Exception {
        List<DosageResponse> dosages = Arrays.asList((new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 10), null, null, new ArrayList<MedicineResponse>())));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        ivrContext.pillRegimen(pillRegimenResponse).callDirection(CallDirection.Inbound).callStartTime(DateUtil.newDateTime(DateUtil.newDate(2010, 10, 11), 15, 40, 0));
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        assertNull(pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()));
    }

    @Test
    public void previousDosageIsCaptured() throws Exception {
        List<DosageResponse> dosages = Arrays.asList((new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 9), null, null, new ArrayList<MedicineResponse>())));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        ivrContext.pillRegimen(pillRegimenResponse).callDirection(CallDirection.Inbound).callStartTime(DateUtil.newDateTime(DateUtil.newDate(2010, 10, 11), 15, 40, 0));
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);
        assertFalse(pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()).isTaken());
    }

    @Test
    public void dosagesForLastFourWeeksShouldBe28ForSingleDoseRegimen() {
        final DateTime dosageStartDate = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage_id", new Time(dosageStartDate.getHourOfDay(), dosageStartDate.getMinuteOfHour()), dosageStartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime today = dosageStartDate.plusWeeks(4).minusDays(1);
        assertEquals(28, pillRegimen.getDosesBetween(dosageStartDate.toLocalDate(), today));
    }

    @Test
    public void dosagesShouldBe28ForSingleDoseRegimenStarting4WeeksBack() {
        final DateTime dosageStartDate = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage_id", new Time(dosageStartDate.getHourOfDay(), dosageStartDate.getMinuteOfHour()), dosageStartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime today = dosageStartDate.plusWeeks(4).minusDays(1);
        assertEquals(28, pillRegimen.getNumberOfDosesAsOf(today));
    }

    @Test
    public void dosagesForLastFourWeeksShouldBe51ForTwoDoseRegimen() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 7, 13, 0, 0, 0);
        final DateTime today = new DateTime(2011, 10, 30, 13, 0, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertEquals(51, pillRegimen.getDosesBetween(dosage1StartDate.toLocalDate(), today.minusDays(1)));
    }

    @Test
    public void shouldReturnTodaysDoseWhenPatientIsOnSingleDosage_AndTimeSpecifiedIsAfterPillTime() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 1, 0, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 11, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage1_id", 10, 30, givenDateTime.toLocalDate(), dose);
    }

    @Test
    public void shouldReturnTodaysDoseWhenPatientIsOnSingleDosage_AndTimeSpecifiedIsBeforePillTime_ButWithinPillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 10, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage1_id", 10, 30, givenDateTime.toLocalDate(), dose);
    }


    @Test
    public void shouldReturnPreviousDaysDoseWhenPatientIsOnSingleDosage_AndTimeSpecifiedIsBeforePillTime_AndBeforePillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 9, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage1_id", 10, 30, givenDateTime.minusDays(1).toLocalDate(), dose);
    }

    @Test
    public void shouldReturnTodaysFirstDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsAfterFirstDosePillTime_ButBeforeSecondDosagesPillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 2, 13, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 12, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage1_id", 10, 30, givenDateTime.toLocalDate(), dose);
    }

    @Test
    public void shouldReturnTodaysSecondDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsBeforeSecondDosePillTime_AndWithinSecondDosagesPillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 2, 13, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 12, 45, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage2_id", 13, 30, givenDateTime.toLocalDate(), dose);
    }

    @Test
    public void shouldReturnTodaysSecondDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsAfterSecondPillTime() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 2, 13, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 18, 30, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage2_id", 13, 30, givenDateTime.toLocalDate(), dose);
    }

    @Test
    public void shouldReturnPreviousDaysLastDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsBeforeFirstDosePillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 2, 13, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 8, 30, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage2_id", 13, 30, givenDateTime.minusDays(1).toLocalDate(), dose);
    }

    @Test
    public void shouldNotReturnDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsBeforeStartOfEitherDosagesPillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 22, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 3, 16, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertNull(dose);
    }

    private void assertDose(String expectedDosageId, int expectedDosageHour, int expectedDosageMinute, LocalDate expectedDoseDate, Dose actualDose) {
        assertEquals(expectedDosageId, actualDose.getDosage().getDosageId());
        assertEquals(expectedDosageHour, actualDose.getDosage().getDosageHour());
        assertEquals(expectedDosageMinute, actualDose.getDosage().getDosageMinute());
        assertEquals(expectedDoseDate, actualDose.getDate());
    }

    @Test
    public void shouldReturnFirstDoseWhenPillRegimenHasTwoDosages() {
        mockCurrentDate(DateUtil.newDateTime(DateUtil.today(), 14, 0, 0));
        Time dosage1Time = new Time(10, 0);
        Time dosage2Time = new Time(20, 30);
        LocalDate dosage1StartDate = DateUtil.newDate(2011, 10, 11);
        LocalDate dosage2StartDate = DateUtil.newDate(2011, 10, 10);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withTwoDosages(dosage1Time, dosage1StartDate, "dosage1Id", dosage2Time, dosage2StartDate, "dosage2Id").build();

        Dose dose = new PillRegimen(pillRegimenResponse).veryFirstDose();

        assertEquals(20, dose.getDoseTime().getHourOfDay());
        assertEquals(30, dose.getDoseTime().getMinuteOfHour());
        assertEquals(dosage2StartDate, dose.getDate());
    }
}
