package org.motechproject.tama.integration.domain.patient;

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
        assertEquals(0, patients.findByPatientId("9999").size());

        Patient loadedPatient = patients.findByPatientId("12345678").get(0);
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
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("1234").build();
        patients.add(patient);

        String mobilePhoneNumber = "9986573310";
        patient.setMobilePhoneNumber(mobilePhoneNumber);
        patients.merge(patient);
        markForDeletion(patient);

        List<Patient> dbPatients = patients.findByPatientId("1234");
        assertEquals(mobilePhoneNumber, dbPatients.get(0).getMobilePhoneNumber());
    }

    @Test
    public void shouldRemovePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("5678").build();
        patients.add(patient);
        markForDeletion(patient);

        patients.remove(patient);
        List<Patient> dbPatients = patients.findByPatientId("5678");
        assertTrue(dbPatients.isEmpty());
    }

    @Test
    public void shouldActivatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("6666").build();
        patients.add(patient);

        patients.activate(patient.getId());
        markForDeletion(patient);

        List<Patient> dbPatients = patients.findByPatientId("6666");
        assertTrue(dbPatients.get(0).isActive());
    }

    @Test
    public void shouldCheckIfActive() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("8888").withStatus(Patient.Status.Active).build();
        patients.add(patient);
        markForDeletion(patient);

        assertTrue(patients.checkIfActive(patient));
    }

    @Test
    public void shouldFindClinicForPatient() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        clinics.add(clinicForPatient);
        markForDeletion(clinicForPatient);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).build();
        patients.add(patient);
        markForDeletion(patient);

        assertEquals(clinicForPatient.getId(), patients.findClinicFor(patient));
    }

}
