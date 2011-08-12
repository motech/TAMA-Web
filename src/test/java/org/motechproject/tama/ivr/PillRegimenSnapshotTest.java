package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Mock
    private IVRSession ivrSession;
    @Mock
    private IVRRequest ivrRequest;

    private IVRContext ivrContext;
    private PillRegimenResponse pillRegimen;
    private PillRegimenSnapshot pillRegimenSnapshot;

    @Before
    public void setUp() {
        initMocks(this);

        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        ivrContext = new IVRContext(ivrRequest, ivrSession);

        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        Map<String, String> map = new HashMap();
        map.put(PillReminderCall.DOSAGE_ID, "currentDosageId");
        when(ivrRequest.getTamaParams()).thenReturn(map);
    }

    @Test
    public void shouldGetListOfMedicinesForCurrentDosage() {
        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        List<String> medicines = pillRegimenSnapshot.medicinesForCurrentDosage();
        assertEquals(2, medicines.size());
        assertEquals("medicine1", medicines.get(0));
        assertEquals("medicine2", medicines.get(1));
    }

    @Test
    public void shouldGetListOfMedicinesForPreviousDosage() {
        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        List<String> medicines = pillRegimenSnapshot.medicinesForPreviousDosage();
        assertEquals(1, medicines.size());
        assertEquals("medicine3", medicines.get(0));
    }

    @Test
    public void shouldGetPreviousDosage() {
        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        DosageResponse previousDosage = pillRegimenSnapshot.getPreviousDosage();
        assertEquals("previousDosageId", previousDosage.getDosageId());
    }

    @Test
    public void shouldGetNextDosage() {
        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        DosageResponse previousDosage = pillRegimenSnapshot.getNextDosage();
        assertEquals("nextDosageId", previousDosage.getDosageId());
    }

    @Test
    public void previousAndNextDosagesIsSameAsCurrentDosageWhenItsTheOnlyDosageForARegimen() {
        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        pillRegimen.getDosages().remove(0);
        pillRegimen.getDosages().remove(1);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);

        assertEquals("currentDosageId", pillRegimenSnapshot.getPreviousDosage().getDosageId());
        assertEquals("currentDosageId", pillRegimenSnapshot.getNextDosage().getDosageId());
    }

    @Test
    public void previousDosageIsTakenWhenTheCurrentDosageIsTheVeryFirstDosageOfTheRegimen() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        LocalDate lastTakenDate = null;
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, lastTakenDate, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertTrue(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void previousDosageIsNotTakenWhenNotTakenThePreviousDay() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate dosageLastTakenDate = DateUtil.today().minusDays(2);

        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, dosageLastTakenDate, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertFalse(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void previousDosageIsNotTakenWhenNotTakenTheLastNightDoseWasNotTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate currentDosageLastTakenDate = DateUtil.today();
        LocalDate previousDosageLastTakenDate = currentDosageLastTakenDate.minusDays(2);

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today().minusDays(3), null, previousDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today().minusDays(3), null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertFalse(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void previousDosageIsTakenWhenTheLastNightDoseWasTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate currentDosageLastTakenDate = DateUtil.today();
        LocalDate previousDosageLastTakenDate = currentDosageLastTakenDate.minusDays(1);

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), DateUtil.today().minusDays(3), null, previousDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today().minusDays(3), null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertTrue(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void shouldGetTotalCountOfScheduledDosagesForARegimenWhenWeeksLessThanFour() {
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 1, 12, 0, 0)); // TotalCount = 32 + 22 = 28 + 22 = 50
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 9, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 10), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 10, 15, 5, 0));

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(getPillRegimenResponse());
        pillRegimenSnapshot = new PillRegimenSnapshot(new IVRContext(ivrRequest, ivrSession));

        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCount();
        assertEquals(50, totalCount);
    }

    @Test
    public void shouldGetTotalCountOfScheduledDosagesForARegimenWithMultipleDosagesInTheSameDay() {
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 10, 12, 0, 0)); // TotalCount = 1 + 0 = 1
        when(DateUtil.newDateTime(new LocalDate(2011, 8, 10), 9, 5, 0)).thenReturn(new DateTime(2011, 8, 10, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 8, 10), 15, 5, 0)).thenReturn(new DateTime(2011, 8, 10, 15, 5, 0));

        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        dosageResponses.add(new DosageResponse("currentDosageId", new Time(9, 5), new LocalDate(2011, 8, 10), new LocalDate(2012, 7, 1), new LocalDate(2011, 8, 4), medicineResponses));
        dosageResponses.add(new DosageResponse("previousDosageId", new Time(15, 5), new LocalDate(2011, 8, 10), new LocalDate(2012, 7, 10), new LocalDate(2011, 8, 4), medicineResponses));
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("r1", "p1", 0, 0, dosageResponses);

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);
        pillRegimenSnapshot = new PillRegimenSnapshot(new IVRContext(ivrRequest, ivrSession));

        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCount();
        assertEquals(1, totalCount);
    }

    @Test
    public void shouldGetTotalCountOfScheduledDosagesForARegimenWhenWeeksGreaterThanFour() {
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 10, 1, 12, 0, 0)); // TotalCount = 93 + 89 = 182; capped to 56
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 1), 9, 5, 0)).thenReturn(new DateTime(2011, 7, 1, 9, 5, 0));
        when(DateUtil.newDateTime(new LocalDate(2011, 7, 10), 15, 5, 0)).thenReturn(new DateTime(2011, 7, 10, 15, 5, 0));

        Mockito.when(ivrSession.getPillRegimen()).thenReturn(getPillRegimenResponse());
        pillRegimenSnapshot = new PillRegimenSnapshot(new IVRContext(ivrRequest, ivrSession));

        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCount();
        assertEquals(56, totalCount);
    }

    @Test
    public void shouldGetTheCurrentDosageFromTheRegimenWhenParametersAreEmpty() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate currentDosageLastTakenDate = DateUtil.today().minusDays(1);
        LocalDate previousDosageLastTakenDate = currentDosageLastTakenDate;
        DateTime testCallTime = DateUtil.now().withHourOfDay(8).withMinuteOfHour(6).withSecondOfMinute(0);

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), null, null, previousDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);

        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        Mockito.when(ivrRequest.hasNoTamaData()).thenReturn(true);

        pillRegimenSnapshot = new PillRegimenSnapshot(new IVRContext(ivrRequest, ivrSession), testCallTime);
        assertNotNull(pillRegimenSnapshot.getCurrentDosage());
        assertEquals(10, pillRegimenSnapshot.getCurrentDosage().getDosageHour());
    }

    @Test
    public void isTakenShouldReturnTrueIfAResponseToTheDosageWasCapturedToday() {
        List<DosageResponse> dosages = new ArrayList<DosageResponse>() {{
            add(new DosageResponse("currentDosageId", new Time(22, 5), null, null, DateUtil.today(), new ArrayList<MedicineResponse>()));
        }};

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(new IVRContext(ivrRequest, ivrSession));
        assertTrue(new PillRegimenSnapshot(new IVRContext(ivrRequest, ivrSession)).isCurrentDosageTaken());
    }

    @Test
    public void getCurrentDosageShouldReturnLastDosageIfCallIsMadeIsBeforeTimeForFirstDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate currentDosageLastTakenDate = DateUtil.today().minusDays(1);
        LocalDate previousDosageLastTakenDate = currentDosageLastTakenDate;
        int firstDosageHour = 10;
        DateTime testCallTime = DateUtil.now().withHourOfDay(firstDosageHour - 2).withMinuteOfHour(4).withSecondOfMinute(0);

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), null, null, previousDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(firstDosageHour, 5), null, null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);

        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        Mockito.when(ivrRequest.hasNoTamaData()).thenReturn(true);

        pillRegimenSnapshot = new PillRegimenSnapshot(new IVRContext(ivrRequest, ivrSession), testCallTime);
        assertEquals(dosages.get(0).getDosageHour(), pillRegimenSnapshot.getCurrentDosage().getDosageHour());
    }

    private PillRegimenResponse getPillRegimenResponse() {
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        dosageResponses.add(new DosageResponse("currentDosageId", new Time(9, 5), new LocalDate(2011, 7, 1), new LocalDate(2012, 7, 1), new LocalDate(2011, 8, 4), medicineResponses));
        dosageResponses.add(new DosageResponse("previousDosageId", new Time(15, 5), new LocalDate(2011, 7, 10), new LocalDate(2012, 7, 10), new LocalDate(2011, 8, 4), medicineResponses));
        return new PillRegimenResponse("r1", "p1", 0, 0, dosageResponses);
    }
}
