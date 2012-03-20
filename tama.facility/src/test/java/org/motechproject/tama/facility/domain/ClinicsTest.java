package org.motechproject.tama.facility.domain;

import org.junit.Test;
import org.motechproject.tama.facility.builder.ClinicBuilder;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ClinicsTest {

    @Test
    public void getById(){
        Clinic clinic1 = ClinicBuilder.startRecording().withId("id1").build();
        Clinic clinic2 = ClinicBuilder.startRecording().withId("id2").build();
        Clinics clinics = new Clinics(Arrays.asList(clinic1, clinic2));

        assertEquals("id1", clinics.getBy("id1").getId());
        assertEquals("id2", clinics.getBy("id2").getId());
        assertNull(clinics.getBy("id3"));
    }
}
