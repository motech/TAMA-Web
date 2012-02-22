package org.motechproject.tama.web.model;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.util.DateUtil;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class CallLogPageNavigatorTest {

    private CallLogPageNavigator callLogPageNavigator;
    private Date startDate;
    private Date endDate;

    @Before
    public void setUp() {
        startDate = DateUtil.now().toDate();
        endDate = DateUtil.now().plusDays(10).toDate();
    }

    @Test
    public void shouldReturnNextPageLinkWhenNextPageExists() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(1, startDate, endDate, 2);
        String nextLink = callLogPageNavigator.getNextPageLink();
        assertEquals(buildExpectedLink("2"), nextLink);

    }

    @Test
    public void shouldReturnNullWhenNextPageDoesNotExist() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(2, startDate, endDate, 2);
        assertNull(callLogPageNavigator.getNextPageLink());
    }

    @Test
    public void shouldReturnPreviousPageLinkWhenPreviousPageExists() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(2, startDate, endDate, 2);
        String previousLink = callLogPageNavigator.getPreviousPageLink();
        assertEquals(buildExpectedLink("1"), previousLink);
    }

    @Test
    public void shouldReturnNullWhenPreviousPageDoesNotExist() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(1, startDate, endDate, 1);
        assertNull(callLogPageNavigator.getPreviousPageLink());
    }

    @Test
    public void shouldReturnLinkToFirstPage() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(2, startDate, endDate, 3);
        assertEquals(buildExpectedLink("1"), callLogPageNavigator.getFirstPageLink());
    }

    @Test
    public void shouldReturnNullForFirstPageLinkWhenAlreadyOnFirstPage() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(1, startDate, endDate, 3);
        assertNull(callLogPageNavigator.getFirstPageLink());
    }

    @Test
    public void shouldReturnLinkToLastPage() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(2, startDate, endDate, 3);
        assertEquals(buildExpectedLink("3"), callLogPageNavigator.getLastPageLink());
    }

    @Test
    public void shouldReturnNullForLastPageLinkWhenAlreadyOnLastPage() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(3, startDate, endDate, 3);
        assertNull(callLogPageNavigator.getLastPageLink());
    }

    private String buildExpectedLink(String pageNumber) throws UnsupportedEncodingException {
        return "callsummary?callLogStartDate=" + getFormattedDate(startDate) + "&callLogEndDate=" + getFormattedDate(endDate) + "&pageNumber=" + pageNumber;
    }

    private String getFormattedDate(Date date) {
        return DateUtil.newDate(date).toString(TAMAConstants.DATE_FORMAT);
    }
}
