package org.motechproject.tama.integration.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ClinicianIntegrationTest extends SpringIntegrationTest{

    @Autowired
    private Clinicians clinicians;

    @Before
    public void setUp() {
    }

    @Test
    public void testShouldPersistClinician() {
        String name = "testName";
        Clinician testClinician = ClinicianBuilder.startRecording().withName(name).build();
        clinicians.add(testClinician);

        Clinician clinician = clinicians.get(testClinician.getId());

        assertNotNull(clinician);
        markForDeletion(clinician);
    }

    @Test
    public void testShouldMergeClinic() {
        String testName = "testName";
        String newName = "newName";
        Clinician testClinician = ClinicianBuilder.startRecording().withName(testName).build();
        clinicians.add(testClinician);

        testClinician.setName(newName);
        clinicians.update(testClinician);

        Clinician clinician = clinicians.get(testClinician.getId());

        assertNotNull(clinician);
        assertEquals(newName, clinician.getName());
        markForDeletion(testClinician);
    }

}
