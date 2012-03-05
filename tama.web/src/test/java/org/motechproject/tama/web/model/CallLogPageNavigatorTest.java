package org.motechproject.tama.web.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
import org.motechproject.util.DateUtil;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class CallLogPageNavigatorTest {

    private CallLogPageNavigator callLogPageNavigator;
    private DateTime startDate;
    private DateTime endDate;
    private CallLogSearch answeredLogFilter;
    private CallLogSearch missedLogFilter;
    private String patientId;

    @Before
    public void setUp() {
        startDate = DateUtil.now();
        endDate = DateUtil.now().plusDays(10);
        patientId = "patientId";
        answeredLogFilter = new CallLogSearch(startDate, endDate, CallLog.CallLogType.Answered, null, false, null);
        missedLogFilter = new CallLogSearch(startDate, endDate, CallLog.CallLogType.Missed, null, false, null);
    }

    @Test
    public void shouldReturnNextPageLinkWhenNextPageExists() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(answeredLogFilter, 1, 2, patientId);
        String nextLink = callLogPageNavigator.getNextPageLink();
        assertEquals(buildExpectedLink("2", "Answered"), nextLink);
    }

    @Test
    public void shouldReturnNullWhenNextPageDoesNotExist() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(answeredLogFilter, 2, 2, patientId);
        assertNull(callLogPageNavigator.getNextPageLink());
    }

    @Test
    public void shouldReturnPreviousPageLinkWhenPreviousPageExists() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(answeredLogFilter, 2, 2, patientId);
        String previousLink = callLogPageNavigator.getPreviousPageLink();
        assertEquals(buildExpectedLink("1", "Answered"), previousLink);
    }

    @Test
    public void shouldReturnNullWhenPreviousPageDoesNotExist() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(answeredLogFilter, 1, 1, patientId);
        assertNull(callLogPageNavigator.getPreviousPageLink());
    }

    @Test
    public void shouldReturnLinkToFirstPage() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(answeredLogFilter, 2, 3, patientId);
        assertEquals(buildExpectedLink("1", "Answered"), callLogPageNavigator.getFirstPageLink());
    }

    @Test
    public void shouldReturnNullForFirstPageLinkWhenAlreadyOnFirstPage() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(answeredLogFilter, 1, 3, patientId);
        assertNull(callLogPageNavigator.getFirstPageLink());
    }

    @Test
    public void shouldReturnLinkToLastPage() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(missedLogFilter, 2, 3, patientId);
        assertEquals(buildExpectedLink("3", "Missed"), callLogPageNavigator.getLastPageLink());
    }

    @Test
    public void shouldReturnNullForLastPageLinkWhenAlreadyOnLastPage() throws UnsupportedEncodingException {
        callLogPageNavigator = new CallLogPageNavigator(answeredLogFilter, 3, 3, patientId);
        assertNull(callLogPageNavigator.getLastPageLink());
    }

    private String buildExpectedLink(String pageNumber, String callType) throws UnsupportedEncodingException {
        return "callsummary?callLogStartDate=" + getFormattedDate(startDate.toDate()) + "&callLogEndDate=" +
                getFormattedDate(endDate.toDate()) + "&callType=" + callType + "&pageNumber=" + pageNumber
                + "&patientId=" + patientId;
    }

    private String getFormattedDate(Date date) {
        return DateUtil.newDate(date).toString(TAMAConstants.DATE_FORMAT);
    }
}
