package org.motechproject.tama.integration.repository;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.repository.AllCities;
import org.motechproject.tama.repository.AllClinics;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ClinicsTest extends SpringIntegrationTest {

    @Autowired
    private AllClinics allClinics;
    @Autowired
    private AllCities allCities;

    @Test
    public void testShouldPersistClinic() {
        City city = City.newCity("TestCity");
        allCities.add(city);
        String name = "testName";
        Clinic testClinic = ClinicBuilder.startRecording().withName(name).withCity(city).build();
        allClinics.add(testClinic);

        Clinic clinic = allClinics.get(testClinic.getId());

        assertNotNull(clinic);
        markForDeletion(testClinic);
    }

    @Test
    public void testShouldMergeClinic() {
        City city = City.newCity("TestCity");
        allCities.add(city);

        String testName = "testName";
        String newName = "newName";
        Clinic testClinic = ClinicBuilder.startRecording().withName(testName).withCity(city).build();
        allClinics.add(testClinic);

        testClinic.setName(newName);
        allClinics.update(testClinic);

        Clinic clinic = allClinics.get(testClinic.getId());

        assertNotNull(clinic);
        assertEquals(newName, clinic.getName());
        markForDeletion(testClinic);
    }


    @Test
    public void shouldLoadCorrespondingCityWhenQueryingClinic() {
        City city = City.newCity("Pune");
        allCities.add(city);
        Clinic clinic = ClinicBuilder.startRecording().withCity(city).build();
        allClinics.add(clinic);
        Clinic returnedClinic = allClinics.get(clinic.getId());
        Assert.assertNotNull(returnedClinic);
        Assert.assertNotNull(returnedClinic.getCity());
        Assert.assertEquals("Pune", returnedClinic.getCity().getName());

        markForDeletion(returnedClinic.getCity());
        markForDeletion(returnedClinic);
    }
}
