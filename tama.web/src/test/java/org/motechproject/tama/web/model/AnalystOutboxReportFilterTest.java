package org.motechproject.tama.web.model;


import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class AnalystOutboxReportFilterTest {

    @Test
    public void testFilterIsGreaterThanOneMonth() {
        LocalDate startDate = DateUtil.today();
        LocalDate endDate = startDate.plusDays(31);

        AnalystOutboxReportFilter filter = new AnalystOutboxReportFilter();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        assertTrue(filter.isMoreThanOneMonth());
    }

    @Test
    public void testFilterIsNotGreaterThanOneMonth() {
        LocalDate startDate = DateUtil.today();
        LocalDate endDate = startDate.plusDays(30);

        AnalystOutboxReportFilter filter = new AnalystOutboxReportFilter();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        assertFalse(filter.isMoreThanOneMonth());
    }
}
