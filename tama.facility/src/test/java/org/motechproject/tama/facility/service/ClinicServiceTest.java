package org.motechproject.tama.facility.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicServiceTest {

    @Mock
    private AllClinics allClinics;
    private ClinicService clinicService;

    @Before
    public void setup() {
        initMocks(this);
        clinicService = new ClinicService(allClinics);
    }

    @Test
    public void shouldFetchAllClinics() {
        List<Clinic> clinics = asList(ClinicBuilder.startRecording().withDefaults().build());
        when(allClinics.getAll()).thenReturn(clinics);
        assertEquals(clinics, clinicService.getAllClinics());
    }
}
