package org.motechproject.tama.domain;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static junit.framework.Assert.assertEquals;

public class DrugDosageTest {

    @Test
    public void shouldNotSetDefaultEndDate() {
        Calendar startDate = Calendar.getInstance();
        startDate.clear(Calendar.MILLISECOND);
        startDate.set(2010, 10, 10, 0, 0, 0);

        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setStartDate(startDate.getTime());

        assertEquals(null, drugDosage.getEndDate());
    }
}
