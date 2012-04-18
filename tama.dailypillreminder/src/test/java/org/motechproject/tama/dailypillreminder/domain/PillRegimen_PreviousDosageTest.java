package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.api.contract.DosageResponse;
import org.motechproject.server.pillreminder.api.contract.MedicineResponse;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class PillRegimen_PreviousDosageTest {
    private DailyPillReminderContextForTest ivrContext;
    private PillRegimenResponse pillRegimenResponse;
    private PillRegimen pillRegimen;
    private String currentDosageId;

    @Before
    public void setUp() {
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        ivrContext = new DailyPillReminderContextForTest(new TAMAIVRContextForTest());
        currentDosageId = "currentDosageId";
    }

    @Test
    public void shouldGetPreviousDosageTimeWhenPreviousDosageIsYesterday_AndCurrentDoseIsInTheMorning() {
        ivrContext.callDirection(CallDirection.Outbound);
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        LocalDate startDate = new LocalDate(2010, 1, 1);
        dosages.add(new DosageResponse(currentDosageId, new Time(10, 5), startDate, null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("previousDosageId", new Time(20, 5), startDate, null, null, new ArrayList<MedicineResponse>()));
        pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, 5, dosages);
        int dayOfTheMonth = 10;
        ivrContext.callStartTime(new DateTime(2010, 1, dayOfTheMonth, 9, 0, 0));
        pillRegimen = new PillRegimen(pillRegimenResponse);
        DateTime previousDosageTime = pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()).getDoseTime();

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

        pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, 5, dosages);
        int dayOfTheMonth = 10;
        ivrContext.callStartTime(new DateTime(2010, 10, dayOfTheMonth, 21, 0, 0));
        pillRegimen = new PillRegimen(pillRegimenResponse);
        DateTime previousDosageTime = pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()).getDoseTime();

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

        pillRegimenResponse = new PillRegimenResponse("regimenId", "patientId", 2, 5, 5, dosages);
        int dayOfTheMonth = 10;
        ivrContext.callStartTime(new DateTime(2010, 10, dayOfTheMonth, 9, 0, 0));
        pillRegimen = new PillRegimen(pillRegimenResponse);
        DateTime previousDosageTime = pillRegimen.getPreviousDoseAt(ivrContext.callStartTime()).getDoseTime();

        assertEquals(10, previousDosageTime.getHourOfDay());
        assertEquals(5, previousDosageTime.getMinuteOfHour());
        assertEquals(dayOfTheMonth - 1, previousDosageTime.getDayOfMonth());
    }
}
