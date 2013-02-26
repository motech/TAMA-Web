package org.motechproject.tama.facility.reporting;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.reports.contract.ClinicRequest;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicRequestMapperTest {

    private Clinic clinic;

    @Mock
    private AllCitiesCache allCitiesCache;

    @Before
    public void setup() {
        initMocks(this);
        clinic = ClinicBuilder.startRecording().withDefaults().withGreetingName("greeting").build();
        when(allCitiesCache.getBy(clinic.getCityId())).thenReturn(clinic.getCity());
    }

    @Test
    public void shouldPublishGreetingName() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCitiesCache, clinic).map();
        assertTrue(isNotBlank(clinicRequest.getGreetingName()));
        assertEquals(clinic.getGreetingName(), clinicRequest.getGreetingName());
    }

    @Test
    public void shouldPublishContactNumber() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCitiesCache, clinic).map();
        assertTrue(isNotBlank(clinicRequest.getContactNumber()));
        assertEquals(clinic.getPhone(), clinicRequest.getContactNumber());
    }

    @Test
    public void shouldPublishAddress() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCitiesCache, clinic).map();
        assertTrue(isNotBlank(clinicRequest.getAddress()));
        assertEquals(clinic.getAddress(), clinicRequest.getAddress());
    }

    @Test
    public void shouldMapClinicId() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCitiesCache, clinic).map();
        assertEquals(clinic.getId(), clinicRequest.getClinicId());
    }

    @Test
    public void shouldMapClinicName() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCitiesCache, clinic).map();
        assertEquals(clinic.getName(), clinicRequest.getClinicName());
    }

    @Test
    public void shouldMapClinicCityName() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCitiesCache, clinic).map();
        assertEquals(clinic.getCity().getName(), clinicRequest.getCityName());
    }

    @Test
    public void shouldMapClinicGreeting() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCitiesCache, clinic).map();
        assertEquals(clinic.getGreetingName(), clinicRequest.getGreetingName());
    }

    @Test
    public void shouldMapClinicAddress() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCitiesCache, clinic).map();
        assertEquals(clinic.getAddress(), clinicRequest.getAddress());
    }

    @Test
    public void shouldMapClinicContactNumber() {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCitiesCache, clinic).map();
        assertEquals(clinic.getPhone(), clinicRequest.getContactNumber());
    }
}
