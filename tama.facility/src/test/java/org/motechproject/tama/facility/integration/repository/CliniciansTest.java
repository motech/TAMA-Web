package org.motechproject.tama.facility.integration.repository;

import org.ektorp.UpdateConflictException;
import org.junit.Test;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.repository.AllClinicianIds;
import org.motechproject.tama.facility.repository.AllClinicians;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.repository.AllCities;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath*:applicationFacilityContext.xml", inheritLocations = false)
public class CliniciansTest extends SpringIntegrationTest {

    @Autowired
    private AllClinicians allClinicians;

    @Autowired
    private AllClinicianIds allCliniciansIds;

    @Autowired
    private AllClinics allClinics;

    @Autowired
    private AllCities allCities;

    @Test
    public void testShouldPersistClinician() {
        String name = unique("testName1");
        Clinician testClinician = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        allClinicians.add(testClinician);

        Clinician clinician = allClinicians.get(testClinician.getId());

        assertNotNull(clinician);
        markForDeletion(clinician, allCliniciansIds.get(clinician));
    }

    @Test
    @ExpectedException(UpdateConflictException.class)
    public void testNotShouldPersistClinicianWithNonUniqueUserName() {
        String name = unique("testName1");
        Clinician clinician = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        allClinicians.add(clinician);

        Clinician dbClinician = allClinicians.get(clinician.getId());

        Clinician clinicianWithSameName = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        allClinicians.add(clinicianWithSameName);
        markForDeletion(dbClinician, allCliniciansIds.get(dbClinician), clinicianWithSameName);
    }


    @Test
    public void testShouldMergeClinic() {
        String testName = unique("testName2");
        String newName = unique("newName");
        Clinician testClinician = ClinicianBuilder.startRecording().withName(testName).withUserName(testName).build();
        allClinicians.add(testClinician);

        testClinician.setName(newName);
        allClinicians.update(testClinician);

        Clinician clinician = allClinicians.get(testClinician.getId());

        assertNotNull(clinician);
        assertEquals(newName, clinician.getName());
        markForDeletion(testClinician, allCliniciansIds.get(clinician));
    }

    @Test
    public void shouldFindByUserNameAndPassword() {
        City city = City.newCity("Test");
        allCities.add(city);
        Clinic testClinic = ClinicBuilder.startRecording().withName("testClinic").withCity(city).build();
        allClinics.add(testClinic);
        Clinician testClinician = ClinicianBuilder.startRecording().withName("testName").
                withUserName("jack").withPassword("samurai").withClinic(testClinic).build();
        allClinicians.add(testClinician);

        Clinician byUserNameAndPassword = allClinicians.findByUserNameAndPassword("jack", "samurai");
        assertNotNull(byUserNameAndPassword);
        markForDeletion(city, testClinic, testClinician, allCliniciansIds.get(testClinician));
    }

    @Test
    public void shouldLoadCorrespondingClinicWhenQueryingClinician() {
        City city = City.newCity("Test");
        allCities.add(city);
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).build();
        allClinics.add(clinic);
        String clinicianId = unique("foo");
        Clinician clinician = ClinicianBuilder.startRecording().withClinic(clinic).withUserName(clinicianId).build();
        allClinicians.add(clinician);
        Clinician returnedClinician = allClinicians.findByUsername(clinicianId);
        assertNotNull(returnedClinician);
        assertNotNull(returnedClinician.getClinic());
        assertNotNull(returnedClinician.getClinic().getName().equals(clinic.getName()));

        markForDeletion(city, clinic, clinician, allCliniciansIds.get(clinician));
    }

    @Test
    public void shouldUpdateClinicianPassword() {
        City city = City.newCity("Test");
        allCities.add(city);
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).build();
        allClinics.add(clinic);
        String clinicianId = unique("foo");
        Clinician clinician = ClinicianBuilder.startRecording().withClinic(clinic).withPassword("bar").withUserName(clinicianId).build();
        allClinicians.add(clinician);
        Clinician returnedClinician = allClinicians.findByUsername(clinicianId);
        assertEquals("bar", returnedClinician.getPassword());

        returnedClinician.setPassword("foobar");
        allClinicians.updatePassword(returnedClinician);
        returnedClinician = allClinicians.findByUsername(clinicianId);
        assertEquals("foobar", returnedClinician.getPassword());

        markForDeletion(city, clinic, returnedClinician);
    }
}
