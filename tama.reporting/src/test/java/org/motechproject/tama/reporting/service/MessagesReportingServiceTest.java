package org.motechproject.tama.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.MessagesRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessagesReportingServiceTest {

    public static final String REPORTS_URL = "http://localhost:9999/tama-reports/";

    @Mock
    private HttpClientService httpClientService;
    @Mock
    private ReportingProperties reportingProperties;
    private MessagesReportingService messagesReportingService;

    @Before
    public void setup() {
        initMocks(this);
        when(reportingProperties.reportingURL()).thenReturn(REPORTS_URL);
        messagesReportingService = new MessagesReportingService(httpClientService, reportingProperties);
    }

    @Test
    public void shouldPublishMessagesSave() {
        MessagesRequest request = new MessagesRequest();
        messagesReportingService.save(request);
        verify(httpClientService).post(REPORTS_URL + "messages", request);
    }
}
