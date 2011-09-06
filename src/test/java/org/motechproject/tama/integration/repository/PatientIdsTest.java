package org.motechproject.tama.integration.repository;

import org.ektorp.DocumentNotFoundException;
import org.junit.Test;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientId;
import org.motechproject.tama.repository.AllPatientIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import static junit.framework.Assert.assertEquals;

public class PatientIdsTest extends SpringIntegrationTest {
    @Autowired
    private AllPatientIds allPatientIds;

    @Test
    public void shouldPersistPatientId() {
        Clinic clinic = new Clinic("C1");
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("P1").withClinic(clinic).build();

        allPatientIds.add(patient);

        PatientId patientId = allPatientIds.get(patient);
        assertEquals("C1_P1", patientId.getId());
        markForDeletion(patientId);
    }

    @Test
    @ExpectedException(DocumentNotFoundException.class)
    public void shouldRemovePatientId() {
        Clinic clinic = new Clinic("C1");
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("P1").withClinic(clinic).build();

        allPatientIds.add(patient);
        allPatientIds.remove(patient);
        allPatientIds.get(patient);
    }

}
