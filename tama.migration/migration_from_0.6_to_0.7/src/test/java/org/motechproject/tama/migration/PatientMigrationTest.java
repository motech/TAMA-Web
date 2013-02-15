package org.motechproject.tama.migration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.tama.migration.repository.PagedPatientsRepository;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.motechproject.tama.reporting.service.PatientReportingService;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;
import org.motechproject.tama.reports.contract.PatientRequest;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PatientMigrationTest {

    @Mock
    private PatientReportingService reportingService;
    @Mock
    private AllHIVTestReasonsCache allHIVTestReasons;
    @Mock
    private AllModesOfTransmissionCache allModesOfTransmission;
    @Mock
    private AllGendersCache allGenders;
    @Mock
    private AllIVRLanguagesCache allIVRLanguages;
    @Mock
    private PagedPatientsRepository patientRepository;

    private PatientMigration patientMigration;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        patientMigration = new PatientMigration(patientRepository, allIVRLanguages, allGenders, allModesOfTransmission, allHIVTestReasons, reportingService);
    }

    @Test
    public void shouldMigratePatientAlongWithMedicalHistory() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        when(patientRepository.get(0, 100)).thenReturn(asList(patient));

        patientMigration.migrate();
        verify(reportingService).save(any(PatientRequest.class), any(MedicalHistoryRequest.class));
    }
}
