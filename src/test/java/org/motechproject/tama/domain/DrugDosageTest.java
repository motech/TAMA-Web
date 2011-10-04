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
            setDosageSchedules(Arrays.asList("10:00am", ""));
        }};
        DrugDosage dosageWithoutMorningTime = new DrugDosage() {{
            setDosageSchedules(Arrays.asList("10:00pm", ""));
        }};
        DrugDosage dosage = new DrugDosage() {{
            setDosageSchedules(Arrays.asList("10:00pm", "10:00am"));
        }};

        assertEquals(1, dosageWithoutEveningTime.getNonEmptyDosageSchedules().size());
        assertEquals(1, dosageWithoutMorningTime.getNonEmptyDosageSchedules().size());
        assertEquals(2, dosage.getNonEmptyDosageSchedules().size());
    }
}
