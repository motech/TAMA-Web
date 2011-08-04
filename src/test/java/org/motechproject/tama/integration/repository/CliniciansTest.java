package org.motechproject.tama.integration.repository;

import org.ektorp.UpdateConflictException;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Cities;
import org.motechproject.tama.repository.ClinicianIds;
import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CliniciansTest extends SpringIntegrationTest {

    @Autowired
    private Clinicians clinicians;

    @Autowired
    private ClinicianIds cliniciansIds;

    @Autowired
    private Clinics clinics;

    @Autowired
    private Cities cities;

    @Before
    public void setUp() {
    }

    @Test
    public void testShouldPersistClinician() {
        String name = "testName1";
        Clinician testClinician = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        clinicians.add(testClinician);

        Clinician clinician = clinicians.get(testClinician.getId());

        assertNotNull(clinician);
        markForDeletion(clinician);
        markForDeletion(cliniciansIds.get(clinician));
    }

    @Test
    @ExpectedException(UpdateConflictException.class)
    public void testNotShouldPersistClinicianWithNonUniqueUserName() {
        String name = "testName1";
        Clinician clinician = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        clinicians.add(clinician);

        Clinician dbClinician = clinicians.get(clinician.getId());
        markForDeletion(dbClinician);
        markForDeletion(cliniciansIds.get(dbClinician));

        Clinician clinicianWithSameName = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        clinicians.add(clinicianWithSameName);
        markForDeletion(clinicianWithSameName);
    }


    @Test
    public void testShouldMergeClinic() {
        String testName = "testName2";
        String newName = "newName";
        Clinician testClinician = ClinicianBuilder.startRecording().withName(testName).withUserName(testName).build();
        clinicians.add(testClinician);

        testClinician.setName(newName);
        clinicians.update(testClinician);

        Clinician clinician = clinicians.get(testClinician.getId());

        assertNotNull(clinician);
        assertEquals(newName, clinician.getName());
        markForDeletion(testClinician);
        markForDeletion(cliniciansIds.get(clinician));
    }

    @Test
    public void shouldFindByUserNameAndPassword() {
        City city = City.newCity("Test");
        cities.add(city);
        Clinic testClinic = ClinicBuilder.startRecording().withName("testClinic").withCity(city).build();
        clinics.add(testClinic);
        Clinician testClinician = ClinicianBuilder.startRecording().withName("testName").
                withUserName("jack").withPassword("samurai").withClinic(testClinic).build();
        clinicians.add(testClinician);

        Clinician byUserNameAndPassword = clinicians.findByUserNameAndPassword("jack", "samurai");
        assertNotNull(byUserNameAndPassword);
        markForDeletion(city);
        markForDeletion(testClinic);
        markForDeletion(testClinician);
        markForDeletion(cliniciansIds.get(testClinician));
    }

    @Test
    public void shouldLoadCorrespondingClinicWhenQueryingClinician() {
        City city = City.newCity("Test");
        cities.add(city);
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).build();
        clinics.add(clinic);
        Clinician clinician = ClinicianBuilder.startRecording().withClinic(clinic).withUserName("foo").build();
        clinicians.add(clinician);
        Clinician returnedClinician = clinicians.findByUsername("foo");
        assertNotNull(returnedClinician);
        assertNotNull(returnedClinician.getClinic());
        assertNotNull(returnedClinician.getClinic().getName().equals(clinic.getName()));

        markForDeletion(city);
        markForDeletion(clinic);
        markForDeletion(clinician);
        markForDeletion(cliniciansIds.get(clinician));
    }

    @Test
    public void shouldUpdateClinicianPassword() {
       City city = City.newCity("Test");
        cities.add(city);
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).build();
        clinics.add(clinic);
        Clinician clinician = ClinicianBuilder.startRecording().withClinic(clinic).withPassword("bar").withUserName("foo").build();
        clinicians.add(clinician);
        Clinician returnedClinician = clinicians.findByUsername("foo");
        assertEquals("bar",returnedClinician.getPassword());

        returnedClinician.setPassword("foobar");
        clinicians.updatePassword(returnedClinician);
        returnedClinician = clinicians.findByUsername("foo");
        assertEquals("foobar",returnedClinician.getPassword());

        markForDeletion(city);
        markForDeletion(clinic);
        markForDeletion(returnedClinician);
    }
}
