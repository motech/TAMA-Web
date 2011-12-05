package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class PillRegimenTest {

    @Test
    public void dosagesForLastFourWeeksShouldBe28ForSingleDoseRegimen() {
        final DateTime dosageStartDate = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>(){{
            add(new DosageResponse("dosage_id", new Time(dosageStartDate.getHourOfDay(), dosageStartDate.getMinuteOfHour()), dosageStartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime today = dosageStartDate.plusWeeks(4);
        assertEquals(28, pillRegimen.getNumberOfDosesBetween(dosageStartDate, today));
    }

    @Test
    public void dosagesShouldBe28ForSingleDoseRegimenStarting4WeeksBack() {
        final DateTime dosageStartDate = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>(){{
            add(new DosageResponse("dosage_id", new Time(dosageStartDate.getHourOfDay(), dosageStartDate.getMinuteOfHour()), dosageStartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime today = dosageStartDate.plusWeeks(4);
        assertEquals(28, pillRegimen.getNumberOfDosesAsOf(today));
    }

    @Test
    public void dosagesForLastFourWeeksShouldBe51ForTwoDoseRegimen() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 7, 13, 0, 0, 0);
        final DateTime today = new DateTime(2011, 10, 30, 12, 0, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>(){{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertEquals(51, pillRegimen.getNumberOfDosesBetween(dosage1StartDate, today));
    }

}
