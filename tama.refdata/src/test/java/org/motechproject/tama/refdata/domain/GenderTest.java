package org.motechproject.tama.refdata.domain;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.refdata.builder.GenderBuilder;

public class GenderTest {

    @Test
    public void testEquals() {
        Gender male = GenderBuilder.startRecording().withId("1").withType("Male").build();
        Gender female = GenderBuilder.startRecording().withId("2").withType("Female").build();
        Gender anotherMale = GenderBuilder.startRecording().withId("1").withType("Male").build();

        Assert.assertFalse(male.equals(female));
        Assert.assertTrue(male.equals(anotherMale));
    }
}
