package org.motechproject.tamadomain.integration.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamadomain.builder.ClinicBuilder;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamadomain.domain.City;
import org.motechproject.tamadomain.domain.Clinic;
import org.motechproject.tamadomain.repository.AllCities;
import org.motechproject.tamadomain.repository.AllClinics;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ClinicsTest extends SpringIntegrationTest {

    @Autowired
    private AllClinics allClinics;
    @Autowired
    private AllCities allCities;

    @Before
    public void before() {
        super.before();
        markForDeletion(allClinics.getAll().toArray());
        markForDeletion(allCities.getAll().toArray());
        deleteAll();
    }

    @After
    public void after() {
        markForDeletion(allClinics.getAll().toArray());
        markForDeletion(allCities.getAll().toArray());
        super.after();
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
        Assert.assertEquals(city.getName(), returnedClinic.getCity().getName());

        markForDeletion(clinic);
        markForDeletion(city);
    }

    @Test
    public void getAllShouldLoadCorrespondingCities() {
        City city = City.newCity("Pune");
        allCities.add(city);

        City anotherCity = City.newCity("Chennai");
        allCities.add(anotherCity);

        Clinic clinic = ClinicBuilder.startRecording().withCity(city).build();
        allClinics.add(clinic);

        Clinic anotherClinic = ClinicBuilder.startRecording().withCity(anotherCity).build();
        allClinics.add(anotherClinic);

        List<Clinic> returnedClinics = allClinics.getAll();

        Assert.assertEquals(city.getName(), returnedClinics.get(0).getCity().getName());
        Assert.assertEquals(anotherCity.getName(), returnedClinics.get(1).getCity().getName());

        markForDeletion(anotherClinic);
        markForDeletion(anotherCity);

        markForDeletion(clinic);
        markForDeletion(city);
    }
}
