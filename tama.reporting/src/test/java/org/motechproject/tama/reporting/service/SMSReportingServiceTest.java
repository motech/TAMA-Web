package org.motechproject.tama.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.SMSLogRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSReportingServiceTest {

    public static final String REPORTS_URL = "http://localhost:9999/tama-reports/";

    @Mock
    private HttpClientService httpClientService;

    @Mock
    private ReportingProperties reportingProperties;

    private SMSReportingService smsReportingService;

    @Before
    public void setup() {
        initMocks(this);
        smsReportingService = new SMSReportingService(reportingProperties, httpClientService);
        when(reportingProperties.reportingURL()).thenReturn(REPORTS_URL);
    }

    @Test
    public void shouldReportSMSSent() {
        SMSLogRequest request = new SMSLogRequest();
        smsReportingService.save(request);
        verify(httpClientService).post(REPORTS_URL + "smsLog", request);
    }
}
