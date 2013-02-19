package org.motechproject.tama.facility.integration.repository;

import org.ektorp.UpdateConflictException;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.repository.AllClinicianIds;
import org.motechproject.tama.facility.repository.AllClinicians;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.refdata.repository.AllCities;
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

    @Autowired
    private AllCitiesCache allCitiesCache;

    @Test
    public void testShouldPersistClinician() {
        String name = unique("testName1");
        Clinician testClinician = ClinicianBuilder.startRecording().withDefaults().withName(name).withUserName(name).build();
        allClinicians.add(testClinician, "admin");

        Clinician clinician = allClinicians.get(testClinician.getId());
        markForDeletion(clinician, allCliniciansIds.get(clinician));

        assertNotNull(clinician);
    }

    @Test
    @ExpectedException(UpdateConflictException.class)
    public void testNotShouldPersistClinicianWithNonUniqueUserName() {
        String name = unique("testName1");
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withName(name).withUserName(name).build();
        allClinicians.add(clinician, "admin");

        Clinician dbClinician = allClinicians.get(clinician.getId());

        Clinician clinicianWithSameName = ClinicianBuilder.startRecording().withName(name).withUserName(name).build();
        markForDeletion(dbClinician, allCliniciansIds.get(dbClinician));
        allClinicians.add(clinicianWithSameName, "admin");
    }

    @Test
    public void testShouldMergeClinic() {
        String testName = unique("testName2");
        String newName = unique("newName");
        Clinician testClinician = ClinicianBuilder.startRecording().withDefaults().withName(testName).withUserName(testName).build();
        allClinicians.add(testClinician, "admin");

        testClinician.setName(newName);
        allClinicians.update(testClinician, "admin");
        markForDeletion(testClinician, allCliniciansIds.get(testClinician));

        Clinician clinician = allClinicians.get(testClinician.getId());

        assertNotNull(clinician);
        assertEquals(newName, clinician.getName());
    }

    @Test
    public void shouldFindByUserNameAndPassword() {
        City city = City.newCity("Test");
        allCities.add(city);

        allCitiesCache.refresh();

        Clinic testClinic = ClinicBuilder.startRecording().withDefaults().withName("testClinic").withCity(city).build();
        allClinics.add(testClinic, "admin");
        Clinician testClinician = ClinicianBuilder.startRecording().withDefaults().withName("testName").
                withUserName("jack").withPassword("samurai").withClinic(testClinic).build();
        allClinicians.add(testClinician, "admin");
        markForDeletion(city, testClinic, testClinician, allCliniciansIds.get(testClinician));

        Clinician byUserNameAndPassword = allClinicians.findByUserNameAndPassword("jack", "samurai");
        assertNotNull(byUserNameAndPassword);
    }

    @Test
    public void shouldLoadCorrespondingClinicWhenQueryingClinician() {
        City city = City.newCity("Test");
        allCities.add(city);

        allCitiesCache.refresh();

        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).build();
        allClinics.add(clinic, "admin");
        String clinicianId = unique("foo");
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic).withUserName(clinicianId).build();
        allClinicians.add(clinician, "admin");
        markForDeletion(city, clinic, clinician, allCliniciansIds.get(clinician));

        Clinician returnedClinician = allClinicians.findByUsername(clinicianId);
        assertNotNull(returnedClinician);
        assertNotNull(returnedClinician.getClinic());
        assertNotNull(returnedClinician.getClinic().getName().equals(clinic.getName()));
    }

    @Test
    public void shouldUpdateClinicianPassword() {
        City city = City.newCity("Test");
        allCities.add(city);

        allCitiesCache.refresh();

        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).build();
        allClinics.add(clinic, "admin");
        String clinicianId = unique("foo");
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic).withPassword("bar").withUserName(clinicianId).build();
        allClinicians.add(clinician, "admin");
        markForDeletion(city, clinic, clinician);

        Clinician returnedClinician = allClinicians.findByUsername(clinicianId);
        assertEquals("bar", returnedClinician.getPassword());

        returnedClinician.setPassword("foobar");
        allClinicians.updatePassword(returnedClinician, "admin");
        returnedClinician = allClinicians.findByUsername(clinicianId);
        markForDeletion(returnedClinician);
        assertEquals("foobar", returnedClinician.getPassword());
    }
}
