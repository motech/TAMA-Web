package org.motechproject.tama.web.model;


import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class FilterWithPatientIDAndDateRangeTest {

    @Test
    public void testFilterIsGreaterThanOneYear() {
        LocalDate startDate = DateUtil.today();
        LocalDate endDate = startDate.plusYears(1).plusDays(1);

        FilterWithPatientIDAndDateRange filter = new FilterWithPatientIDAndDateRange();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        assertTrue(filter.isMoreThanOneYear());
    }

    @Test
    public void testFilterIsNotGreaterThanOneYear() {
        LocalDate startDate = DateUtil.today();
        LocalDate endDate = startDate.plusYears(1).minusDays(1);

        FilterWithPatientIDAndDateRange filter = new FilterWithPatientIDAndDateRange();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        assertFalse(filter.isMoreThanOneYear());
    }
}
