package org.motechproject.tama.repository;

import junit.framework.TestCase;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.integration.domain.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertNotNull;

public class CliniciansIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Clinicians clinicians;
    @Autowired
    private Clinics clinics;
    @Autowired
    private Cities cities;

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

        markForDeletion(clinic);
        markForDeletion(clinician);
    }
}
