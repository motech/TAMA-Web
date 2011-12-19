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

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillRegimenSnapshotNextDosageTest {
    private DailyPillReminderContextForTest ivrContext;
    private PillRegimenResponse pillRegimen;
    private PillRegimenSnapshot pillRegimenSnapshot;
    private String currentDosageId = "currentDosageId";

    @Before
    public void setUp() {
        initMocks(this);
        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        ivrContext = new DailyPillReminderContextForTest(new TAMAIVRContextForTest());
        ivrContext.dosageId(currentDosageId);
    }

    @Test
    public void shouldGetNextDosageTimeWhenNextDosageIsTomorrow_AndCurrentDoseIsInTheEvening() {
        ivrContext.callDirection(CallDirection.Outbound);
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate startDate = new LocalDate(2010, 10, 9);
        dosages.add(new DosageResponse(currentDosageId, new Time(20, 5), startDate, null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("nextDosageId", new Time(10, 5), startDate, null, null, new ArrayList<MedicineResponse>()));

        ivrContext.callStartTime(new DateTime(2010, 10, 10, 19, 0, 0));
        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);
        DateTime nextDosageTime = pillRegimenSnapshot.getNextDoseTime();

        assertEquals(10, nextDosageTime.getHourOfDay());
        assertEquals(5, nextDosageTime.getMinuteOfHour());
        assertEquals(11, nextDosageTime.getDayOfMonth());
    }

    @Test
    public void shouldGetNextDosageTimeWhenNextDosageIsToday_AndCurrentDoseIsInTheMorning() {
        ivrContext.callDirection(CallDirection.Outbound);
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate startDate = new LocalDate(2010, 10, 9);
        dosages.add(new DosageResponse(currentDosageId, new Time(10, 5), startDate, null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("nextDosageId", new Time(20, 5), startDate, null, null, new ArrayList<MedicineResponse>()));

        ivrContext.callStartTime(new DateTime(2010, 10, 10, 12, 0, 0));
        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        DateTime nextDosageTime = pillRegimenSnapshot.getNextDoseTime();

        assertEquals(20, nextDosageTime.getHourOfDay());
        assertEquals(5, nextDosageTime.getMinuteOfHour());
        assertEquals(10, nextDosageTime.getDayOfMonth());
    }

    @Test
    public void shouldGetNextDosageTimeWhenNextDosageIsToday_AndCurrentDoseIsInTheMorningForDailyDosage() {
        ivrContext.callDirection(CallDirection.Outbound);
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate startDate = new LocalDate(2010, 10, 9);
        dosages.add(new DosageResponse(currentDosageId, new Time(10, 5), startDate, null, null, new ArrayList<MedicineResponse>()));

        ivrContext.callStartTime(new DateTime(2010, 10, 10, 9, 0, 0));
        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, pillRegimen);

        DateTime nextDosageTime = pillRegimenSnapshot.getNextDoseTime();

        assertEquals(10, nextDosageTime.getHourOfDay());
        assertEquals(5, nextDosageTime.getMinuteOfHour());
        assertEquals(11, nextDosageTime.getDayOfMonth());
    }
}
