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

public class PatientIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Patients patients;

    @Autowired
    private Clinics clinics;


    @Test
    public void shouldLoadPatientByPatientId() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("12345678").build();
        patient.persist();

        Assert.assertEquals(0, patient.findByPatientId("9999").size());

        Patient loadedPatient = patient.findByPatientId("12345678").get(0);
        Assert.assertNotNull(loadedPatient);
        Assert.assertEquals("12345678", loadedPatient.getPatientId());

        loadedPatient.remove();
        loadedPatient.flush();
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
        patient.persist();
        markForDeletion(patient);
        anotherPatient.persist();
        markForDeletion(anotherPatient);

        List<Patient> results = patients.findByClinicId(clinicForPatient.getId());

        assertTrue(results.contains(patient));
        assertFalse(results.contains(anotherPatient));
        System.out.println(results);

    }

}
