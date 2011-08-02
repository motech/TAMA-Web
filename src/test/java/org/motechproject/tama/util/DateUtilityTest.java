package org.motechproject.tama.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class DateUtilityTest {

    @Test
    public void testShouldGetDate(){
        Date date = DateUtility.newDate(2011, 4, 21);
        Calendar calendar = wrapDate(date);
        Assert.assertEquals(2011, calendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.APRIL, calendar.get(Calendar.MONTH));
        Assert.assertEquals(21, calendar.get(Calendar.DATE));
    }

    private Calendar wrapDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    @Test
    public void shouldAddDate(){
        Date date = DateUtility.newDate(2011, Calendar.APRIL, 21);
        Date newDate =  DateUtility.addDate(date,2);
        Assert.assertEquals(23,wrapDate(newDate).get(Calendar.DATE));
    }
}
