package org.motechproject.tama.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.reports.contract.HealthTipsRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogReportingServiceTest {

    @Mock
    private HealthTipsReportingService healthTipsReportingService;
    private CallLogReportingService callLogReportingService;

    @Before
    public void setup() {
        initMocks(this);
        callLogReportingService = new CallLogReportingService(healthTipsReportingService);
    }

    @Test
    public void shouldReportHealthTips() {
        HealthTipsRequest request = new HealthTipsRequest();
        callLogReportingService.reportHealthTips(request);
        verify(healthTipsReportingService).save(request);
    }
}
