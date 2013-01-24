package org.motechproject.tama.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reporting.mapper.PatientRequestMapper;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.PatientRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientReportingServiceTest {

    public static final String REPORTS_URL = "http://localhost:9999/";

    @Mock
    private HttpClientService httpClientService;
    @Mock
    private ReportingProperties reportingProperties;
    private ArgumentCaptor<PatientRequest> requestCaptor;
    private PatientReportingService patientReportingService;

    @Before
    public void setup() {
        initMocks(this);
        requestCaptor = ArgumentCaptor.forClass(PatientRequest.class);
        when(reportingProperties.reportingURL()).thenReturn(REPORTS_URL);
        patientReportingService = new PatientReportingService(httpClientService, reportingProperties);
    }

    @Test
    public void shouldPublishPatientSave() {
        Patient patient = new Patient();
        patientReportingService.save(patient);
        verify(httpClientService).post(eq(REPORTS_URL + "patient"), any(PatientRequest.class));
    }

    @Test
    public void shouldMapPatientBeforePublishingSave() {
        Patient patient = new Patient();
        patientReportingService.save(patient);

        verify(httpClientService).post(anyString(), requestCaptor.capture());
        assertEquals(new PatientRequestMapper(patient).map(), requestCaptor.getValue());
    }
}
