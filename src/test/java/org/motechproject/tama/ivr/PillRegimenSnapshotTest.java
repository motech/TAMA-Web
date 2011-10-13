package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
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
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class PillRegimenSnapshotTest {
    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        ivrContext = new TAMAIVRContextForTest();
        ivrContext.dosageId("currentDosageId");
    }

    @Test
    public void shouldGetListOfMedicinesForCurrentDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        List<String> medicines = pillRegimenSnapshot.medicinesForCurrentDosage();
        assertEquals(2, medicines.size());
        assertEquals("pillmedicine1", medicines.get(0));
        assertEquals("pillmedicine2", medicines.get(1));
    }

    @Test
    public void shouldGetListOfMedicinesForPreviousDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        List<String> medicines = pillRegimenSnapshot.medicinesForPreviousDosage();
        assertEquals(1, medicines.size());
        assertEquals("pillmedicine3", medicines.get(0));
    }

    @Test
    public void shouldGetPreviousDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        DosageResponse previousDosage = pillRegimenSnapshot.getPreviousDosage();
        assertEquals("previousDosageId", previousDosage.getDosageId());
    }

    @Test
    public void shouldGetNextDosage() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        DosageResponse previousDosage = pillRegimenSnapshot.getNextDosage();
        assertEquals("nextDosageId", previousDosage.getDosageId());
    }

    @Test
    public void previousAndNextDosagesIsSameAsCurrentDosageWhenItsTheOnlyDosageForARegimen() {
        ivrContext.callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
        PillRegimenResponse pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        pillRegimen.getDosages().remove(0);
        pillRegimen.getDosages().remove(1);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertEquals("currentDosageId", pillRegimenSnapshot.getPreviousDosage().getDosageId());
        assertEquals("currentDosageId", pillRegimenSnapshot.getNextDosage().getDosageId());
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
    public void previousDosageIsNotTakenWhenTheLastNightDoseWasNotTaken() {
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
    public void previousDosageIsTakenWhenTheLastNightDoseWasTaken() {
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
    public void shouldGetTotalCountOfScheduledDosagesForARegimenWhenWeeksLessThanFour() {
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 1, 12, 0, 0)); // TotalCount = 32 + 22 = 28 + 22 = 50
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 9, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 10), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 10, 15, 5, 0));

        DateTime testCallTime = new DateTime(2011, 8, 1, 12, 0, 0);
        ivrContext.callStartTime(testCallTime);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, getPillRegimenResponse());
        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCountForLastFourWeeks();
        assertEquals(50, totalCount);
    }

    @Test
    public void shouldGetTotalCountOfScheduledDosagesForARegimenTillThePreviousWeek() {
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 1, 12, 0, 0)); // TotalCount = 25 + 15 = 40
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 9, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 10), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 10, 15, 5, 0));

        DateTime testCallTime = new DateTime(2011, 8, 1, 12, 0, 0);
        ivrContext.callStartTime(testCallTime);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, getPillRegimenResponse());

        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCountForLastFourWeeks(DateUtil.now().minusWeeks(1));
        assertEquals(40, totalCount);
    }

    @Test
    public void totalCountShouldIncludeADosageIfNowIsAfterThePillWindowStartHour() {
        int dosageYear = 2011;
        int dosageMonth = 7;
        int dosageDate = 1;
        int dosageHour = 9;
        int dosageMinute = 5;

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(dosageYear, dosageMonth, dosageDate));
        when(DateUtil.newDateTime(new LocalDate(dosageYear, dosageMonth, dosageDate), dosageHour, dosageMinute, 0))
                .thenReturn(new DateTime(dosageYear, dosageMonth, dosageDate, dosageHour, dosageMinute, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 10), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 10, 15, 5, 0));

        PillRegimenResponse pillRegimenResponse = getPillRegimenResponse();

        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        int timeWithinPillWindow = dosage.getDosageHour() - 1;
        DateTime testCallTime = new DateTime(dosageYear, dosageMonth, dosageDate, timeWithinPillWindow, 0, 0);
        ivrContext.callStartTime(testCallTime);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimenResponse);

        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCountForLastFourWeeks();
        assertEquals(1, totalCount);
    }

    @Test
    public void shouldGetTotalCountOfScheduledDosagesForARegimenWithMultipleDosagesInTheSameDay() {
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 10, 12, 0, 0)); // TotalCount = 1 + 0 = 1
        when(DateUtil.newDateTime(new LocalDate(2011, 8, 10), 9, 5, 0)).thenReturn(new DateTime(2011, 8, 10, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 8, 10), 15, 5, 0)).thenReturn(new DateTime(2011, 8, 10, 15, 5, 0));

        DateTime testCallTime = new DateTime(2011, 8, 10, 12, 0, 0);
        ivrContext.callStartTime(testCallTime);
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        dosageResponses.add(new DosageResponse("currentDosageId", new Time(9, 5), new LocalDate(2011, 8, 10), new LocalDate(2012, 7, 1), new LocalDate(2011, 8, 4), medicineResponses));
        dosageResponses.add(new DosageResponse("previousDosageId", new Time(15, 5), new LocalDate(2011, 8, 10), new LocalDate(2012, 7, 10), new LocalDate(2011, 8, 4), medicineResponses));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("r1", "p1", 0, 0, dosageResponses);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimenResponse);

        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCountForLastFourWeeks();
        assertEquals(1, totalCount);
    }

    @Test
    public void shouldGetTotalCountOfScheduledDosagesForARegimenWhenWeeksGreaterThanFour() {
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 10, 1, 12, 0, 0)); // TotalCount = 93 + 89 = 182; capped to 56
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 9, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 10), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 10, 15, 5, 0));

        DateTime testCallTime = new DateTime(2011, 10, 1, 12, 0, 0);
        ivrContext.callStartTime(testCallTime);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, getPillRegimenResponse());
        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCountForLastFourWeeks();
        assertEquals(56, totalCount);
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

        assertNotNull(pillRegimenSnapshot.getCurrentDosage());
        assertEquals(10, pillRegimenSnapshot.getCurrentDosage().getDosageHour());
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

        assertTrue(pillRegimenSnapshot.isCurrentDosageTaken());
    }

    @Test
    public void currentDosageIsCapturedTheNextDay() throws Exception {
        List<DosageResponse> dosages = Arrays.asList((new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 10), null, new LocalDate(2010, 10, 10), new ArrayList<MedicineResponse>())));
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        ivrContext.pillRegimen(pillRegimen).callDirection(CallDirection.Inbound).callStartTime(DateUtil.newDateTime(DateUtil.newDate(2010, 10, 11), 15, 40, 0));
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        assertTrue(pillRegimenSnapshot.isCurrentDosageTaken());
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

        assertTrue(pillRegimenSnapshot.isCurrentDosageTaken());
    }

    @Test
    public void getCurrentDosageShouldReturnLastDosageIfCallIsMadeBeforeTimeForFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate currentDosageLastTakenDate = DateUtil.today().minusDays(1);
        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today(), null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).minusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertEquals(dosages.get(0).getDosageHour(), pillRegimenSnapshot.getCurrentDosage().getDosageHour());
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
        assertEquals(DateUtil.today().plusDays(1), pillRegimenSnapshot.getCurrentDosage().getDosageDate());
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

        assertTrue(pillRegimenSnapshot.isEarlyToTakeDosage(15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnFalseIfNowBeforeDosageHour_OutSideDosageInterval_OutsidePillWindow() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().withHourOfDay(10).minusHours(2).withMinuteOfHour(4).withSecondOfMinute(0);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertFalse(pillRegimenSnapshot.isEarlyToTakeDosage(15));
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

        assertFalse(pillRegimenSnapshot.isEarlyToTakeDosage(15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnTrue_DosageDateIsTomorrow_NowBeforeDosageHour_WithinDosageInterval() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(1, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>()));

        DateTime testCallTime = DateUtil.now().minusDays(1).withHourOfDay(23).withMinuteOfHour(59);
        ivrContext.callStartTime(testCallTime);
        ivrContext.callDirection(CallDirection.Inbound);
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        assertTrue(pillRegimenSnapshot.isEarlyToTakeDosage(15));
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

        assertTrue(pillRegimenSnapshot.isLateToTakeDosage());
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

        assertFalse(pillRegimenSnapshot.isLateToTakeDosage());
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

        assertTrue(pillRegimenSnapshot.isLateToTakeDosage());
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

        assertEquals(DateUtil.today(), new LocalDate(pillRegimenSnapshot.getNextDosageTime()));

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
