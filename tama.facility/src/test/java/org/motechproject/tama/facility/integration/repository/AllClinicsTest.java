package org.motechproject.tama.facility.integration.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.repository.AllCities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@ContextConfiguration(locations = "classpath*:applicationFacilityContext.xml", inheritLocations = false)
public class AllClinicsTest extends SpringIntegrationTest {

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
