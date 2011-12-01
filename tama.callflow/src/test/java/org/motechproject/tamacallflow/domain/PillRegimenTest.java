package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class PillRegimenTest {

    @Test
    public void dosagesForLastFourWeeksShouldBe28ForSingleDoseRegimen() {
        final DateTime regimenStartDate = new DateTime(2011, 10, 22, 10, 00);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>(){{
            add(new DosageResponse("dosage_id", new Time(5, 0), regimenStartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime today = regimenStartDate.plusWeeks(4);
        assertEquals(28, pillRegimen.getDosageCount(regimenStartDate, today));
    }

    @Test
    @Ignore // will be fixed by related test in DosageTimeLine
    public void dosagesForLastFourWeeksShouldBe51ForTwoDoseRegimen() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 22, 10, 00);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>(){{
            add(new DosageResponse("dosage1_id", new Time(5, 0), dosage1StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage2_id", new Time(13, 0), dosage1StartDate.plusDays(5).toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime today = dosage1StartDate.plusWeeks(4);
        assertEquals(51, pillRegimen.getDosageCount(dosage1StartDate, today));
    }

    @Test
    @Ignore // will be fixed by related test in DosageTimeLine
    public void dosagesForLastFourWeeksShouldBe28ForSingleDoseRegimen_() {
        final DateTime regimenStartDate = new DateTime(2011, 10, 22, 10, 00);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>(){{
            add(new DosageResponse("dosage_id", new Time(5, 0), regimenStartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime today = regimenStartDate.plusWeeks(4);
        assertEquals(28, pillRegimen.getDosageCount(today));
    }
}
