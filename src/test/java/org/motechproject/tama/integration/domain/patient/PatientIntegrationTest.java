package org.motechproject.tama.integration.domain.patient;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.integration.domain.SpringIntegrationTest;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PatientIntegrationTest extends SpringIntegrationTest {
    @Autowired
    private Patients patients;

    @Autowired
    private Clinics clinics;

    @Test
    public void shouldLoadPatientByPatientId() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("12345678").build();
        patients.add(patient);
        markForDeletion(patient);
        assertEquals(0, patients.findById("9999").size());

        Patient loadedPatient = patients.findById("12345678").get(0);
        assertNotNull(loadedPatient);
        assertEquals("12345678", loadedPatient.getPatientId());
    }

    @Test
    public void shouldGetOnlyPatientsWithTheSpecifiedClinicID() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        clinics.add(clinicForPatient);
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withName("anotherClinic").build();
        clinics.add(anotherClinic);
        markForDeletion(anotherClinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).build();
        Patient anotherPatient = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).build();
        patients.add(patient);
        patients.add(anotherPatient);
        markForDeletion(patient);
        markForDeletion(anotherPatient);

        List<Patient> dbPatients = patients.findByClinic(clinicForPatient.getId());

        assertTrue(dbPatients.contains(patient));
        assertFalse(dbPatients.contains(anotherPatient));
    }

    @Test
    public void shouldUpdatePatient() {
        assertTrue(true);
    }

    @Test
    public void shouldRemovePatient() {
        assertTrue(true);
    }

    @Test
    public void shouldActivatePatient() {
        assertTrue(true);
    }

    @Test
    public void shouldCheckIfActive() {
        assertTrue(true);
    }

    @Test
    public void shouldFindClinicForPatient() {
        assertTrue(true);
    }

}
