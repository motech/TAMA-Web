package org.motechproject.tama.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;
import org.motechproject.tama.reports.contract.PatientRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientReportingServiceTest {

    public static final String REPORTS_URL = "http://localhost:9999/tama-reports";

    @Mock
    private HttpClientService httpClientService;
    @Mock
    private ReportingProperties reportingProperties;
    @Mock
    private MedicalHistoryReportingService medicalHistoryReportingService;
    private PatientReportingService patientReportingService;

    @Before
    public void setup() {
        initMocks(this);
        when(reportingProperties.reportingURL()).thenReturn(REPORTS_URL);
        patientReportingService = new PatientReportingService(httpClientService, reportingProperties, medicalHistoryReportingService);
    }

    @Test
    public void shouldPublishPatientSave() {
        PatientRequest request = new PatientRequest();
        MedicalHistoryRequest medicalHistoryRequest = new MedicalHistoryRequest();
        patientReportingService.save(request, medicalHistoryRequest);
        verify(medicalHistoryReportingService).save(medicalHistoryRequest);
        verify(httpClientService).post(REPORTS_URL + "patient", request);
    }

    @Test
    public void shouldPublishPatientUpdate() {
        PatientRequest request = new PatientRequest();
        MedicalHistoryRequest medicalHistoryRequest = new MedicalHistoryRequest();
        patientReportingService.update(request, medicalHistoryRequest);
        verify(medicalHistoryReportingService).update(medicalHistoryRequest);
        verify(httpClientService).post(REPORTS_URL + "patient/update", request);
    }
}
