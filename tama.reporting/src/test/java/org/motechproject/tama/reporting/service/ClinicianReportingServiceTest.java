package org.motechproject.tama.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.ClinicianRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicianReportingServiceTest {

    public static final String REPORTS_URL = "http://localhost:9999/tama-reports/";

    @Mock
    private ReportingProperties reportingProperties;

    @Mock
    private HttpClientService httpClientService;

    private ClinicianReportingService clinicianReportingService;

    @Before
    public void setup() {
        initMocks(this);
        when(reportingProperties.reportingURL()).thenReturn(REPORTS_URL);
        clinicianReportingService = new ClinicianReportingService(httpClientService, reportingProperties);
    }

    @Test
    public void shouldReportClinicianSave() {
        ClinicianRequest request = new ClinicianRequest();
        clinicianReportingService.save(request);
        verify(httpClientService).post(REPORTS_URL + "clinician", request);
    }

    @Test
    public void shouldReportClinicianUpdate() {
        ClinicianRequest request = new ClinicianRequest();
        clinicianReportingService.update(request);
        verify(httpClientService).post(REPORTS_URL + "clinician/update", request);
    }
}
