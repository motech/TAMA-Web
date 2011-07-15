package org.motechproject.tama.repository;

import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.integration.domain.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClinicsIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Clinicians clinicians;

    @Autowired
    private Clinics clinics;

    @Autowired
    private Cities cities;

    @Test
    public void shouldLoadCorrespondingCityWhenQueryingClinic() {
        City city = City.newCity("Pune");
        cities.add(city);
        Clinic clinic = ClinicBuilder.startRecording().withCity(city).build();
        clinics.add(clinic);
        Clinic returnedClinic = clinics.get(clinic.getId());
        assertNotNull(returnedClinic);
        assertNotNull(returnedClinic.getCity());
        assertEquals("Pune", returnedClinic.getCity().getName());
    }
}
