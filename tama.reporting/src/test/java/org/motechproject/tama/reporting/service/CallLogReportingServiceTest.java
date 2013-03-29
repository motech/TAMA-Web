package org.motechproject.tama.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.reports.contract.MessagesRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogReportingServiceTest {

    @Mock
    private MessagesReportingService messagesReportingService;
    private CallLogReportingService callLogReportingService;

    @Before
    public void setup() {
        initMocks(this);
        callLogReportingService = new CallLogReportingService(messagesReportingService);
    }

    @Test
    public void shouldReportMessages() {
        MessagesRequest request = new MessagesRequest();
        callLogReportingService.reportMessages(request);
        verify(messagesReportingService).save(request);
    }
}
