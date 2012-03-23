package org.motechproject.tama.web.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllCallLogSummariesTest {

    public static final String PATIENT_DOC_ID = "patientDocId";

    @Mock
    private AllCallLogs allCallLogs;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllIVRLanguagesCache allIVRLanguages;

    private AllCallLogSummaries allCallLogSummaries;

    private int pageNumber = 0;
    private int pageSize = 0;

    @Before
    public void setup() {
        initMocks(this);
        when(allPatients.getAll()).thenReturn(new ArrayList<Patient>());
        when(allIVRLanguages.getAll()).thenReturn(new ArrayList<IVRLanguage>());
        allCallLogSummaries = new AllCallLogSummaries(allCallLogs, allPatients, allIVRLanguages);
    }

    @Test
    public void shouldReturnCallLogSummaries() {
        LocalDate startDate = DateUtil.today();
        LocalDate endDate = DateUtil.today();

        CallLog callLog1 = mock(CallLog.class);
        CallLog callLog2 = mock(CallLog.class);

        when(callLog1.getStartTime()).thenReturn(DateUtil.now());
        when(callLog1.getEndTime()).thenReturn(DateUtil.now());
        when(callLog2.getStartTime()).thenReturn(DateUtil.now());
        when(callLog2.getEndTime()).thenReturn(DateUtil.now());
        when(allCallLogs.findAllCallLogsForDateRange(DateUtil.newDateTime(startDate, 0, 0, 0),
                DateUtil.newDateTime(endDate, 23, 59, 59),
                pageNumber,
                pageSize)
        ).thenReturn(Arrays.asList(callLog1, callLog2));

        List<CallLogSummary> allCallLogSummariesBetween = allCallLogSummaries.getAllCallLogSummariesBetween(startDate, endDate, pageNumber, pageSize);

        assertNotNull(allCallLogSummariesBetween);
    }
}