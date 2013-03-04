package org.motechproject.tama.migration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.migration.repository.PagedClinicsRepository;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.reporting.ClinicReportingRequest;
import org.motechproject.tama.reporting.service.ClinicReportingService;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicMigrationTest {

    @Mock
    private PagedClinicsRepository allDocuments;
    @Mock
    private ClinicReportingService clinicReportingService;
    @Mock
    private AllCitiesCache allCitiesCache;

    private ClinicMigration clinicMigration;


    @Before
    public void setup() {
        initMocks(this);
        clinicMigration = new ClinicMigration(allDocuments, clinicReportingService, allCitiesCache);
        when(allCitiesCache.getBy(anyString())).thenReturn(City.newCity("delhi"));
    }

    @Test
    public void shouldMigrateExistingClinicianContactsToBeCompatibleWithLatestVersion() {
        Clinic.ClinicianContact clinicianContact = new Clinic.ClinicianContact("name", "phoneNumber");
        clinicianContact.setId(null);
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withClinicianContacts(clinicianContact).build();

        clinicMigration.save(clinic);
        assertNotNull(clinic.getClinicianContacts().get(0).getId());
        verify(allDocuments).update(clinic, "");
    }

    @Test
    public void shouldReportLatestVersionOfClinicianContactAndNotAnOlderVersion() {
        Clinic.ClinicianContact clinicianContact = new Clinic.ClinicianContact("name", "phoneNumber");
        clinicianContact.setId(null);

        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withClinicianContacts(clinicianContact).build();
        clinicMigration.save(clinic);

        ArgumentCaptor<ClinicReportingRequest> captor = ArgumentCaptor.forClass(ClinicReportingRequest.class);
        verify(clinicReportingService).save(captor.capture());
        assertNotNull(captor.getValue().getClinicianContactRequests().getClinicianContactRequests().get(0).getId());
    }
}
