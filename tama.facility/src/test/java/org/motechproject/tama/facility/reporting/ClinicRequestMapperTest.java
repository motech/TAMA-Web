package org.motechproject.tama.facility.reporting;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.reports.contract.ClinicRequest;

import static org.junit.Assert.assertEquals;

public class ClinicRequestMapperTest {

    private Clinic clinic;

    @Before
    public void setup() {
        clinic = ClinicBuilder.startRecording().withDefaults().build();
    }

    @Test
    public void shouldMapClinicId() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(clinic).map();
        assertEquals(clinic.getId(), clinicRequest.getClinicId());
    }

    @Test
    public void shouldMapClinicName() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(clinic).map();
        assertEquals(clinic.getName(), clinicRequest.getClinicName());
    }

    @Test
    public void shouldMapClinicCityName() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(clinic).map();
        assertEquals(clinic.getCity().getName(), clinicRequest.getCityName());
    }
}
