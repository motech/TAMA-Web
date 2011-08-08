package org.motechproject.tama.ivr;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, lastTakenDate, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertTrue(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void previousDosageIsNotTakenWhenNotTakenThePreviousDay() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate dosageLastTakenDate = DateUtil.today().minusDays(2);

        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, dosageLastTakenDate, new ArrayList<MedicineResponse>()));

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

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), null, null, previousDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

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

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), null, null, previousDosageLastTakenDate, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, currentDosageLastTakenDate, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertTrue(pillRegimenSnapshot.isPreviousDosageCaptured());
    }

    @Test
    public void shouldGetTotalCountOfScheduledDosagesForARegimenWhenWeeksLessThanFour() {
        Mockito.when(ivrSession.getPillRegimen()).thenReturn(getPillRegimenResponse());
        LocalDate toDate = DateUtil.newDate(2011, 8, 1); // TotalCount = 32 + 23 = 28 + 23 = 51
        pillRegimenSnapshot = new PillRegimenSnapshot(new IVRContext(ivrRequest, ivrSession));

        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCount(toDate);
        assertEquals(51, totalCount);
    }

    @Test
    public void shouldGetTotalCountOfScheduledDosagesForARegimenWhenWeeksGreaterThanFour() {
        Mockito.when(ivrSession.getPillRegimen()).thenReturn(getPillRegimenResponse());
        LocalDate toDate = DateUtil.newDate(2011, 10, 1); // TotalCount = 93 + 89 = 182; capped to 56
        pillRegimenSnapshot = new PillRegimenSnapshot(new IVRContext(ivrRequest, ivrSession));

        int totalCount = pillRegimenSnapshot.getScheduledDosagesTotalCount(toDate);
        assertEquals(56, totalCount);
    }

    private PillRegimenResponse getPillRegimenResponse() {
        ArrayList<DosageResponse> dosageResponses = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> medicineResponses = new ArrayList<MedicineResponse>();
        medicineResponses.add(new MedicineResponse("med1", null, null));
        dosageResponses.add(new DosageResponse("currentDosageId", new Time(9, 5), DateUtil.newDate(2011, 7, 1), DateUtil.newDate(2012, 7, 1), DateUtil.today(), medicineResponses));
        dosageResponses.add(new DosageResponse("previousDosageId", new Time(15, 5), DateUtil.newDate(2011, 7, 10), DateUtil.newDate(2012, 7, 10), DateUtil.today(), medicineResponses));
        return new PillRegimenResponse("r1", "p1", 0, 0, dosageResponses);
    }
}
