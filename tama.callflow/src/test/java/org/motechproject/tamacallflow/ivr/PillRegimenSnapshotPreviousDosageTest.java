package org.motechproject.tamacallflow.ivr;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.builder.PillRegimenResponseBuilder;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillRegimenSnapshotPreviousDosageTest {
    private TAMAIVRContextForTest ivrContext;
    private PillRegimenResponse pillRegimen;
    @Mock
    private PillRegimenSnapshot pillRegimenSnapshot;
    private String currentDosageId;

    @Before
    public void setUp() {
        initMocks(this);

        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        ivrContext = new TAMAIVRContextForTest();

        currentDosageId = "currentDosageId";
    }

    @Test
    public void shouldGetPreviousDosageTimeWhenPreviousDosageIsYesterday_AndCurrentDoseIsInTheMorning() {
        ivrContext.callDirection(CallDirection.Outbound);
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate startDate = new LocalDate(2010, 1, 1);
        dosages.add(new DosageResponse(currentDosageId, new Time(10, 5), startDate, null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("previousDosageId", new Time(20, 5), startDate, null, null, new ArrayList<MedicineResponse>()));
        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        int dayOfTheMonth = 10;
        ivrContext.callStartTime(new DateTime(2010, 1, dayOfTheMonth, 9, 0, 0)).dosageId(currentDosageId);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        DateTime previousDosageTime = pillRegimenSnapshot.getPreviousDoseTime();

        assertEquals(20, previousDosageTime.getHourOfDay());
        assertEquals(5, previousDosageTime.getMinuteOfHour());
        assertEquals(dayOfTheMonth - 1, previousDosageTime.getDayOfMonth());
    }

    @Test
    public void shouldGetPreviousDosageTimeWhenPreviousDosageIsToday_AndCurrentDoseIsInTheEvening() {
        ivrContext.callDirection(CallDirection.Outbound);
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate startDate = new LocalDate(2010, 1, 1);
        dosages.add(new DosageResponse(currentDosageId, new Time(20, 5), startDate, null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("previousDosageId", new Time(10, 5), startDate, null, null, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        int dayOfTheMonth = 10;
        ivrContext.callStartTime(new DateTime(2010, 10, dayOfTheMonth, 21, 0, 0)).dosageId(currentDosageId);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        DateTime previousDosageTime = pillRegimenSnapshot.getPreviousDoseTime();

        assertEquals(10, previousDosageTime.getHourOfDay());
        assertEquals(5, previousDosageTime.getMinuteOfHour());
        assertEquals(dayOfTheMonth, previousDosageTime.getDayOfMonth());
    }

    @Test
    public void shouldGetPreviousDosageTimeWhenPreviousDosageIsYesterday_AndCurrentDoseIsInTheMorningForDailyDosage() {
        ivrContext.callDirection(CallDirection.Outbound);
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate startDate = new LocalDate(2010, 1, 1);
        dosages.add(new DosageResponse(currentDosageId, new Time(10, 5), startDate, null, null, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        int dayOfTheMonth = 10;
        ivrContext.callStartTime(new DateTime(2010, 10, dayOfTheMonth, 9, 0, 0)).dosageId(currentDosageId);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        DateTime previousDosageTime = pillRegimenSnapshot.getPreviousDoseTime();

        assertEquals(10, previousDosageTime.getHourOfDay());
        assertEquals(5, previousDosageTime.getMinuteOfHour());
        assertEquals(dayOfTheMonth - 1, previousDosageTime.getDayOfMonth());
    }
}
