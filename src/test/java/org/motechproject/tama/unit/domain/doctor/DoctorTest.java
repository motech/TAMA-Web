package org.motechproject.tama.unit.domain.doctor;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.builder.DoctorBuilder;
import org.motechproject.tama.domain.Doctor;

public class DoctorTest {

    @Test
    public void testEquals() {
        Doctor doctorBen = DoctorBuilder.startRecording().withId("1").withFirstName("Ben").build();
        Doctor doctorKen = DoctorBuilder.startRecording().withId("2").withFirstName("Ken").build();
        Doctor anotherDoctorBen = DoctorBuilder.startRecording().withId("1").withFirstName("Ben").build();

        Assert.assertFalse(doctorBen.equals(doctorKen));
        Assert.assertTrue(doctorBen.equals(anotherDoctorBen));
    }
}
