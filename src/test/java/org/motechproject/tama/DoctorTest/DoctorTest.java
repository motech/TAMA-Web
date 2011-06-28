package org.motechproject.tama.DoctorTest;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.Doctor;
import org.motechproject.tama.builders.DoctorBuilder;

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
