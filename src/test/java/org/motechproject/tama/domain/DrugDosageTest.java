package org.motechproject.tama.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;

public class DrugDosageTest {

    @Test
    public void shouldNotSetDefaultEndDate() {
        LocalDate startDate = DateUtil.newDate(2010, 10, 10);

        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setStartDate(startDate);

        assertEquals(null, drugDosage.getEndDate());
    }
}
