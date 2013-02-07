package org.motechproject.tama.web.model;


import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class FilterWithPatientIDAndDateRangeTest {

    @Test
    public void testFilterIsGreaterThanOneMonth() {
        LocalDate startDate = DateUtil.today();
        LocalDate endDate = startDate.plusDays(31);

        FilterWithPatientIDAndDateRange filter = new FilterWithPatientIDAndDateRange();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        assertTrue(filter.isMoreThanOneMonth());
    }

    @Test
    public void testFilterIsNotGreaterThanOneMonth() {
        LocalDate startDate = DateUtil.today();
        LocalDate endDate = startDate.plusDays(30);

        FilterWithPatientIDAndDateRange filter = new FilterWithPatientIDAndDateRange();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        assertFalse(filter.isMoreThanOneMonth());
    }
}
