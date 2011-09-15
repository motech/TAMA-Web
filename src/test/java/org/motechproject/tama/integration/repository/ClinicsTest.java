package org.motechproject.tama.integration.repository;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.repository.AllCities;
import org.motechproject.tama.repository.AllClinics;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ClinicsTest extends SpringIntegrationTest {

    @Autowired
    private AllClinics allClinics;
    @Autowired
    private AllCities allCities;

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
