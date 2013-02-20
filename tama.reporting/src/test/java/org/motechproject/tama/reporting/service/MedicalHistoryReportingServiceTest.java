package org.motechproject.tama.reporting.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MedicalHistoryReportingServiceTest {

    public static final String REPORTS_URL = "http://localhost:9999/tama-reports";

    @Mock
    private HttpClientService httpClientService;
    @Mock
    private ReportingProperties reportingProperties;
    private MedicalHistoryReportingService medicalHistoryReportingService;

    @Before
    public void setup() {
        initMocks(this);
        when(reportingProperties.reportingURL()).thenReturn(REPORTS_URL);
        medicalHistoryReportingService = new MedicalHistoryReportingService(httpClientService, reportingProperties);
    }

    @Test
    public void shouldPublishPatientSave() {
        MedicalHistoryRequest request = new MedicalHistoryRequest();
        medicalHistoryReportingService.save(request);
        verify(httpClientService).post(REPORTS_URL + "medicalHistory", request);
    }

    @Test
    public void shouldPublishPatientUpdate() {
        MedicalHistoryRequest request = new MedicalHistoryRequest();
        medicalHistoryReportingService.update(request);
        verify(httpClientService).put(REPORTS_URL + "medicalHistory", request);
    }
}
