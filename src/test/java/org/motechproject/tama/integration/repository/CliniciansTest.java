package org.motechproject.tama.integration.repository;

import org.ektorp.UpdateConflictException;
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

    @Test
    public void testShouldPersistClinician() {
        String name = unique("testName1");
        Clinician testClinician = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        clinicians.add(testClinician);

        Clinician clinician = clinicians.get(testClinician.getId());

        assertNotNull(clinician);
        markForDeletion(clinician, cliniciansIds.get(clinician));
    }

    @Test
    @ExpectedException(UpdateConflictException.class)
    public void testNotShouldPersistClinicianWithNonUniqueUserName() {
        String name = unique("testName1");
        Clinician clinician = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        clinicians.add(clinician);

        Clinician dbClinician = clinicians.get(clinician.getId());

        Clinician clinicianWithSameName = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        clinicians.add(clinicianWithSameName);
        markForDeletion(dbClinician, cliniciansIds.get(dbClinician), clinicianWithSameName);
    }


    @Test
    public void testShouldMergeClinic() {
        String testName = unique("testName2");
        String newName = unique("newName");
        Clinician testClinician = ClinicianBuilder.startRecording().withName(testName).withUserName(testName).build();
        clinicians.add(testClinician);

        testClinician.setName(newName);
        clinicians.update(testClinician);

        Clinician clinician = clinicians.get(testClinician.getId());

        assertNotNull(clinician);
        assertEquals(newName, clinician.getName());
        markForDeletion(testClinician, cliniciansIds.get(clinician));
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
        markForDeletion(city, testClinic, testClinician, cliniciansIds.get(testClinician));
    }

    @Test
    public void shouldLoadCorrespondingClinicWhenQueryingClinician() {
        City city = City.newCity("Test");
        cities.add(city);
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).build();
        clinics.add(clinic);
        String clinicianId = unique("foo");
        Clinician clinician = ClinicianBuilder.startRecording().withClinic(clinic).withUserName(clinicianId).build();
        clinicians.add(clinician);
        Clinician returnedClinician = clinicians.findByUsername(clinicianId);
        assertNotNull(returnedClinician);
        assertNotNull(returnedClinician.getClinic());
        assertNotNull(returnedClinician.getClinic().getName().equals(clinic.getName()));

        markForDeletion(city, clinic, clinician, cliniciansIds.get(clinician));
    }

    @Test
    public void shouldUpdateClinicianPassword() {
       City city = City.newCity("Test");
        cities.add(city);
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).build();
        clinics.add(clinic);
        String clinicianId = unique("foo");
        Clinician clinician = ClinicianBuilder.startRecording().withClinic(clinic).withPassword("bar").withUserName(clinicianId).build();
        clinicians.add(clinician);
        Clinician returnedClinician = clinicians.findByUsername(clinicianId);
        assertEquals("bar",returnedClinician.getPassword());

        returnedClinician.setPassword("foobar");
        clinicians.updatePassword(returnedClinician);
        returnedClinician = clinicians.findByUsername(clinicianId);
        assertEquals("foobar",returnedClinician.getPassword());

        markForDeletion(city, clinic, returnedClinician);
    }
}
