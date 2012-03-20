package org.motechproject.tama.patient.domain;

import org.junit.Test;
import org.motechproject.tama.patient.builder.PatientBuilder;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class PatientsTest {

    @Test
    public void getById(){
        Patient patient1 = PatientBuilder.startRecording().withId("id1").build();
        Patient patient2 = PatientBuilder.startRecording().withId("id2").build();
        Patients patients = new Patients(Arrays.asList(patient1, patient2));

        assertEquals("id1", patients.getBy("id1").getId());
        assertEquals("id2", patients.getBy("id2").getId());
        assertNull(patients.getBy("id3"));
    }
}
