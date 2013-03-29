package org.motechproject.tama.migration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.migration.repository.PagedCallLogsRepository;
import org.motechproject.tama.reporting.service.CallLogReportingService;
import org.motechproject.tama.reports.contract.MessagesRequest;
import org.motechproject.util.DateUtil;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HealthTipsMigrationTest {

    @Mock
    private CallLogReportingService callLogReportingService;
    @Mock
    private PagedCallLogsRepository pagedCallLogsRepository;

    private HealthTipsMigration healthTipsMigration;

    @Before
    public void setup() {
        initMocks(this);
        healthTipsMigration = new HealthTipsMigration(pagedCallLogsRepository, callLogReportingService);
    }

    @Test
    public void shouldMigrateMessagesInCallLogs() {
        CallLog callLog = new CallLog("patientDocumentId");
        callLog.setStartTime(DateUtil.now());

        when(pagedCallLogsRepository.get(0, 100)).thenReturn(asList(callLog));
        healthTipsMigration.migrate();
        verify(callLogReportingService).reportMessages(any(MessagesRequest.class));
    }
}
