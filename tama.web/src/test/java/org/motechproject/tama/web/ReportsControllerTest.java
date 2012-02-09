package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.web.model.PatientReport;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class ReportsControllerTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private PatientService patientService;

    private ReportsController reportsController;

    private Clinic clinic;

    private Patient patient;

    @Before
    public void setUp() {
        initMocks(this);
        clinic = ClinicBuilder.startRecording().withDefaults().build();
        patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).build();
        reportsController = new ReportsController(allPatients, patientService);
    }

    @Test
    public void shouldReturnIndexPage() throws IOException {
        Regimen regimen = mock(Regimen.class);
        when(allPatients.get("patientDocumentId")).thenReturn(patient);
        when(patientService.currentRegimen(patient)).thenReturn(regimen);

        PatientReport patientReport = (PatientReport) reportsController.index("patientDocumentId");
        assertEquals("reports/index", patientReport.getViewName());
    }
}
