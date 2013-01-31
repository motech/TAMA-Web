package org.motechproject.tama.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.PatientEventRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientEventReportingServiceTest {

    public static final String REPORTS_URL = "http://localhost:9999/tama-reports";

    @Mock
    private HttpClientService httpClientService;
    @Mock
    private ReportingProperties reportingProperties;

    private PatientEventReportingService patientEventReportingService;

    @Before
    public void setup() {
        initMocks(this);
        patientEventReportingService = new PatientEventReportingService(httpClientService, reportingProperties);
    }

    @Test
    public void shouldReportPatientEvent() {
        PatientEventRequest patientEventRequest = new PatientEventRequest();

        when(reportingProperties.reportingURL()).thenReturn(REPORTS_URL);
        patientEventReportingService.save(patientEventRequest);
        verify(httpClientService).post(REPORTS_URL + "/patientEvent", patientEventRequest);
    }
}
