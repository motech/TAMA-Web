package org.motechproject.tama.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class DrugDosageTest {
    @Test
    public void shouldNotSetDefaultEndDate() {
        LocalDate startDate = DateUtil.newDate(2010, 10, 10);

        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setStartDate(startDate);

        assertEquals(null, drugDosage.getEndDate());
    }

    @Test
    public void shouldReturnNonEmptySchedules() {
        DrugDosage dosageWithoutEveningTime = new DrugDosage() {{
        	setMorningTime("10:00am");
        }};
        DrugDosage dosageWithoutMorningTime = new DrugDosage() {{
        	setEveningTime("10:00pm");
        }};
        DrugDosage dosage = new DrugDosage() {{
        	setMorningTime("10:00am");
        	setEveningTime("10:00pm");
        }};

        assertEquals(1, dosageWithoutEveningTime.getNonEmptyDosageSchedules().size());
        assertEquals(1, dosageWithoutMorningTime.getNonEmptyDosageSchedules().size());
        assertEquals(2, dosage.getNonEmptyDosageSchedules().size());
    }
}
