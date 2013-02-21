package org.motechproject.tama.facility.reporting;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.reports.contract.ClinicianRequest;

import static org.junit.Assert.assertEquals;

public class ClinicianRequestMapperTest {

    private Clinician clinician;
    private ClinicianRequestMapper clinicianRequestMapper;

    @Before
    public void setup() {
        clinician = ClinicianBuilder.startRecording().withDefaults().build();
        clinicianRequestMapper = new ClinicianRequestMapper(clinician);
    }

    @Test
    public void shouldMapClinicianId() {
        ClinicianRequest clinicianRequest = clinicianRequestMapper.map();
        assertEquals(clinician.getId(), clinicianRequest.getClinicianId());
    }

    @Test
    public void shouldMapClinicId() {
        ClinicianRequest clinicianRequest = clinicianRequestMapper.map();
        assertEquals(clinician.getClinicId(), clinicianRequest.getClinicId());
    }

    @Test
    public void shouldMapContactNumber() {
        ClinicianRequest clinicianRequest = clinicianRequestMapper.map();
        assertEquals(clinician.getContactNumber(), clinicianRequest.getContactNumber());
    }

    @Test
    public void shouldMapAlternateContact() {
        ClinicianRequest clinicianRequest = clinicianRequestMapper.map();
        assertEquals(clinician.getAlternateContactNumber(), clinicianRequest.getAlternateNumber());
    }

    @Test
    public void shouldMapRole() {
        ClinicianRequest clinicianRequest = clinicianRequestMapper.map();
        assertEquals(clinician.getRole().name(), clinicianRequest.getRole());
    }

    @Test
    public void shouldMapUserName() {
        ClinicianRequest clinicianRequest = clinicianRequestMapper.map();
        assertEquals(clinician.getUsername(), clinicianRequest.getUserName());
    }

    @Test
    public void shouldMapName() {
        ClinicianRequest clinicianRequest = clinicianRequestMapper.map();
        assertEquals(clinician.getName(), clinicianRequest.getName());
    }
}
