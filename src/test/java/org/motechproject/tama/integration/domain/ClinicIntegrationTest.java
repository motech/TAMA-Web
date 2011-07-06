package org.motechproject.tama.integration.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ClinicIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Clinics clinics;

    @Before
    public void setUp() {
    }

    @Test
    public void testShouldPersistClinic() {
        String name = "testName";
        Clinic testClinic = ClinicBuilder.startRecording().withName(name).build();
        clinics.add(testClinic);

        Clinic clinic = clinics.get(testClinic.getId());

        assertNotNull(clinic);
        markForDeletion(testClinic);
    }

    @Test
    public void testShouldMergeClinic() {
        String testName = "testName";
        String newName = "newName";
        Clinic testClinic = ClinicBuilder.startRecording().withName(testName).build();
        clinics.add(testClinic);

        testClinic.setName(newName);
        clinics.update(testClinic);

        Clinic clinic = clinics.get(testClinic.getId());

        assertNotNull(clinic);
        assertEquals(newName, clinic.getName());
        markForDeletion(testClinic);
    }
}
