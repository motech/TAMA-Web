package org.motechproject.tama.integration.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.repository.Cities;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ClinicsTest extends SpringIntegrationTest {

    @Autowired
    private Clinics clinics;
    @Autowired
    private Cities cities;

    @Test
    public void testShouldPersistClinic() {
        City city = City.newCity("TestCity");
        cities.add(city);
        String name = "testName";
        Clinic testClinic = ClinicBuilder.startRecording().withName(name).withCity(city).build();
        clinics.add(testClinic);

        Clinic clinic = clinics.get(testClinic.getId());

        assertNotNull(clinic);
        markForDeletion(testClinic);
    }

    @Test
    public void testShouldMergeClinic() {
        City city = City.newCity("TestCity");
        cities.add(city);

        String testName = "testName";
        String newName = "newName";
        Clinic testClinic = ClinicBuilder.startRecording().withName(testName).withCity(city).build();
        clinics.add(testClinic);

        testClinic.setName(newName);
        clinics.update(testClinic);

        Clinic clinic = clinics.get(testClinic.getId());

        assertNotNull(clinic);
        assertEquals(newName, clinic.getName());
        markForDeletion(testClinic);
    }


    @Test
    public void shouldLoadCorrespondingCityWhenQueryingClinic() {
        City city = City.newCity("Pune");
        cities.add(city);
        Clinic clinic = ClinicBuilder.startRecording().withCity(city).build();
        clinics.add(clinic);
        Clinic returnedClinic = clinics.get(clinic.getId());
        Assert.assertNotNull(returnedClinic);
        Assert.assertNotNull(returnedClinic.getCity());
        Assert.assertEquals("Pune", returnedClinic.getCity().getName());
    }
}
