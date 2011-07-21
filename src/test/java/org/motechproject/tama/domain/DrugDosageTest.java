package org.motechproject.tama.domain;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DrugDosageTest {

    @Test
    public void shouldSetDefaultEndDate() {
        Calendar startDate = Calendar.getInstance();
        startDate.clear(Calendar.MILLISECOND);
        startDate.set(2010, 10, 10, 0, 0, 0);
        Calendar expectedEndDate = Calendar.getInstance();
        expectedEndDate.set(2011, 10, 10, 0, 0, 0);
        expectedEndDate.clear(Calendar.MILLISECOND);

        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setStartDate(startDate.getTime());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");

        Assert.assertEquals(dateFormatter.format(expectedEndDate.getTime()), dateFormatter.format(drugDosage.getEndDate()));
    }
}
