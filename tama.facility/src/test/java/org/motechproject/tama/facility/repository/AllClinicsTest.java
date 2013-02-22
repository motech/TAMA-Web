package org.motechproject.tama.facility.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.reporting.ClinicRequestMapper;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.reporting.service.ClinicReportingService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllClinicsTest {

    @Mock
    private ClinicReportingService clinicReportingService;
    @Mock
    private CouchDbConnector db;
    @Mock
    private AllCitiesCache allCities;
    @Mock
    private AllAuditRecords allAuditRecords;

    private AllClinics allClinics;

    @Before
    public void setup() {
        initMocks(this);
        allClinics = new AllClinics(db, allCities, allAuditRecords, clinicReportingService);
    }

    @Test
    public void shouldReportClinicAdded() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCityId("city_id").build();
        when(allCities.getBy("city_id")).thenReturn(clinic.getCity());

        allClinics.add(clinic, "user");

        verify(clinicReportingService).save(new ClinicRequestMapper(allCities,clinic).map());
    }
}
