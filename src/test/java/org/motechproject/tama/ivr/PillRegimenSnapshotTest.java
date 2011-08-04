package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
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

        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrContext.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimen);
        when(ivrContext.ivrRequest()).thenReturn(ivrRequest);
        Map<String, String > map = new HashMap();
        map.put(PillReminderCall.DOSAGE_ID, "currentDosageId");
        when(ivrRequest.getTamaParams()).thenReturn(map);

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
    }

    @Test
    public void shouldGetListOfMedicinesForCurrentDosage() {
        List<String> medicines = pillRegimenSnapshot.medicinesForCurrentDosage();
        assertEquals(2, medicines.size());
        assertEquals("medicine1", medicines.get(0));
        assertEquals("medicine2", medicines.get(1));
    }

    @Test
    public void shouldGetListOfMedicinesForPreviousDosage() {
        List<String> medicines = pillRegimenSnapshot.medicinesForPreviousDosage();
        assertEquals(1, medicines.size());
        assertEquals("medicine3", medicines.get(0));
    }

    @Test
    public void shouldGetPreviousDosage() {
        DosageResponse previousDosage = pillRegimenSnapshot.getPreviousDosage();
        assertEquals("previousDosageId", previousDosage.getDosageId());
    }

    @Test
    public void shouldGetNextDosage() {
        DosageResponse previousDosage = pillRegimenSnapshot.getNextDosage();
        assertEquals("nextDosageId", previousDosage.getDosageId());
    }

    @Test
    public void shouldGetNextDosageTime() {
        DateTime nextDosageTime = pillRegimenSnapshot.getNextDosageTime();

        assertEquals(22, nextDosageTime.getHourOfDay());
        assertEquals(05, nextDosageTime.getMinuteOfHour());
    }

    @Test
    public void previousAndNextDosagesIsSameAsCurrentDosageWhenItsTheOnlyDosageForARegimen() {
        pillRegimen.getDosages().remove(0);
        pillRegimen.getDosages().remove(1);

        assertEquals("currentDosageId", pillRegimenSnapshot.getPreviousDosage().getDosageId());
        assertEquals("currentDosageId", pillRegimenSnapshot.getNextDosage().getDosageId());
    }
}
