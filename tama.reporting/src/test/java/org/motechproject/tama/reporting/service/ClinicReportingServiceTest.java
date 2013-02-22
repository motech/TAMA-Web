package org.motechproject.tama.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.ClinicReportingRequest;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.ClinicRequest;
import org.motechproject.tama.reports.contract.ClinicianContactRequest;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicReportingServiceTest {

    public static final String REPORTS_URL = "http://localhost:9999/tama-reports";

    @Mock
    private HttpClientService httpClientService;
    @Mock
    private ReportingProperties reportingProperties;
    private ClinicReportingService clinicReportingService;

    @Before
    public void setup() {
        initMocks(this);
        when(reportingProperties.reportingURL()).thenReturn(REPORTS_URL);
        clinicReportingService = new ClinicReportingService(httpClientService, reportingProperties);
    }

    @Test
    public void shouldPublishClinicSave() {
        ClinicRequest request = new ClinicRequest();
        ClinicReportingRequest reportingRequest = new ClinicReportingRequest(request, Collections.<ClinicianContactRequest>emptyList());

        clinicReportingService.save(reportingRequest);
        verify(httpClientService).post(REPORTS_URL + "clinic", request);
    }

    @Test
    public void shouldPublishClinicianContactSave() {
        ClinicRequest request = new ClinicRequest();
        List<ClinicianContactRequest> contactRequests = Collections.emptyList();

        ClinicReportingRequest reportingRequest = new ClinicReportingRequest(request, contactRequests);
        clinicReportingService.save(reportingRequest);
        verify(httpClientService).post(REPORTS_URL + "clinicianContact", contactRequests);
    }

    @Test
    public void shouldPublishClinicianContactUpdateAsSave() {
        ClinicRequest request = new ClinicRequest();
        List<ClinicianContactRequest> contactRequests = Collections.emptyList();

        ClinicReportingRequest reportingRequest = new ClinicReportingRequest(request, contactRequests);
        clinicReportingService.update(reportingRequest);
        verify(httpClientService).put(REPORTS_URL + "clinicianContact", contactRequests);
    }

    @Test
    public void shouldPublishClinicUpdate() {
        ClinicRequest request = new ClinicRequest();
        ClinicReportingRequest reportingRequest = new ClinicReportingRequest(request, Collections.<ClinicianContactRequest>emptyList());

        clinicReportingService.update(reportingRequest);
        verify(httpClientService).put(REPORTS_URL + "clinic", request);
    }
}
