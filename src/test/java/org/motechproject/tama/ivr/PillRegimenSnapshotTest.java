package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillRegimenSnapshotTest {
    @Mock
    private IVRSession ivrSession;
    @Mock
    private IVRContext ivrContext;
    @Mock
    private IVRRequest ivrRequest;

    private PillRegimenResponse pillRegimen;
    private PillRegimenSnapshot pillRegimenSnapshot;

    @Before
    public void setUp() {
        initMocks(this);

        when(ivrContext.ivrSession()).thenReturn(ivrSession);
        when(ivrContext.ivrRequest()).thenReturn(ivrRequest);
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
    public void shouldGetNextDosageTime() {
        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        DateTime nextDosageTime = pillRegimenSnapshot.getNextDosageTime();

        assertEquals(22, nextDosageTime.getHourOfDay());
        assertEquals(05, nextDosageTime.getMinuteOfHour());
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

        Date lastTakenDate = null;
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, lastTakenDate, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertTrue(pillRegimenSnapshot.isPreviousDosageTaken());
    }

    @Test
    public void previousDosageIsNotTakenWhenNotTakenThePreviousDay() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        DateTime dosageLastTakenDate = new DateTime().minusDays(2);

        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, dosageLastTakenDate.toDate(), new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertFalse(pillRegimenSnapshot.isPreviousDosageTaken());
    }

    @Test
    public void previousDosageIsNotTakenWhenNotTakenTheLastNightDoseWasNotTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        DateTime currentDosageLastTakenDate = new DateTime();
        DateTime previousDosageLastTakenDate = currentDosageLastTakenDate.minusDays(2);

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), null, null, previousDosageLastTakenDate.toDate(), new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, currentDosageLastTakenDate.toDate(), new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertFalse(pillRegimenSnapshot.isPreviousDosageTaken());
    }

    @Test
    public void previousDosageIsTakenWhenTheLastNightDoseWasTaken() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        DateTime currentDosageLastTakenDate = new DateTime();
        DateTime previousDosageLastTakenDate = currentDosageLastTakenDate.minusDays(1).withHourOfDay(22).withMinuteOfHour(05);

        dosages.add(new DosageResponse("previousDosageId", new Time(22, 5), null, null, previousDosageLastTakenDate.toDate(), new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, currentDosageLastTakenDate.toDate(), new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        assertTrue(pillRegimenSnapshot.isPreviousDosageTaken());
    }
}
