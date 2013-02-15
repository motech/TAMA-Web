package org.motechproject.tama.migration.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationMigrationContext.xml")
public class PagedPatientsRepositoryIT extends SpringIntegrationTest {

    @Autowired
    @Qualifier("tamaDbConnector")
    CouchDbConnector tamaCouchDbConnector;

    @Autowired
    PagedPatientsRepository pagedPatientsRepository;

    @Autowired
    AllClinics allClinics;

    Clinic clinic;

    @Override
    public CouchDbConnector getDBConnector() {
        return tamaCouchDbConnector;
    }

    @Before
    public void setup() {
        clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinic, "admin");
        markForDeletion(clinic);
    }

    @Test
    public void shouldLoadPatientInPages() {
        List<Patient> patients = asList(
                PatientBuilder.startRecording().withDefaults().withPatientId("patientId1").withClinic(clinic).build(),
                PatientBuilder.startRecording().withDefaults().withPatientId("patientId2").withClinic(clinic).build()
        );
        pagedPatientsRepository.add(patients.get(0), "userName");
        pagedPatientsRepository.add(patients.get(1), "userName");
        markForDeletion(patients);

        assertEquals("patientId1", pagedPatientsRepository.get(0, 1).get(0).getPatientId());
        assertEquals("patientId2", pagedPatientsRepository.get(1, 1).get(0).getPatientId());
        assertPatientDependencies(patients);
    }

    private void assertPatientDependencies(List<Patient> patients) {
        for (Patient patient : patients) {
            assertNotNull(patient.getClinic());
            assertNotNull(patient.getGender());
        }
    }
}
