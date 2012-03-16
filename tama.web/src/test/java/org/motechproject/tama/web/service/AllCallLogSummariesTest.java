package org.motechproject.tama.web.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.web.builder.CallLogSummaryBuilder;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllCallLogSummariesTest {

    public static final String PATIENT_DOC_ID = "patientDocId";

    @Mock
    private AllCallLogs allCallLogs;
    @Mock
    private CallLogSummaryBuilder callLogSummaryBuilder;

    private AllCallLogSummaries allCallLogSummaries;

    @Before
    public void setup() {
        initMocks(this);
        allCallLogSummaries = new AllCallLogSummaries(allCallLogs, callLogSummaryBuilder);
    }

    @Test
    public void shouldReturnCallLogSummaries() {
        LocalDate startDate = DateUtil.today();
        LocalDate endDate = DateUtil.today();

        CallLog callLog1 = mock(CallLog.class);
        CallLog callLog2 = mock(CallLog.class);

        when(allCallLogs.findAllCallLogsForDateRange(DateUtil.newDateTime(startDate, 0, 0, 0), DateUtil.newDateTime(endDate, 23, 59, 59))).thenReturn(Arrays.asList(callLog1, callLog2));

        List<CallLogSummary> allCallLogSummariesBetween = allCallLogSummaries.getAllCallLogSummariesBetween(startDate, endDate);
        
        assertNotNull(allCallLogSummariesBetween);
        verify(callLogSummaryBuilder).build(callLog1);
        verify(callLogSummaryBuilder).build(callLog2);
    }
}