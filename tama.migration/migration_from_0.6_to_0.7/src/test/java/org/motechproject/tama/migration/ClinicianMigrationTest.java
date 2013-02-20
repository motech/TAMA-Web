package org.motechproject.tama.migration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.reporting.ClinicianRequestMapper;
import org.motechproject.tama.migration.repository.PagedClinicianRepository;
import org.motechproject.tama.reporting.service.ClinicianReportingService;
import org.motechproject.tama.reports.contract.ClinicianRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicianMigrationTest {

    private ClinicianMigration clinicianMigration;

    @Mock
    private ClinicianReportingService clinicianReportingService;

    @Mock
    private PagedClinicianRepository pagedClinicianRepository;

    @Before
    public void setup(){
        initMocks(this);
        clinicianMigration = new ClinicianMigration(pagedClinicianRepository, clinicianReportingService);
    }

    @Test
    public void shouldSaveClinicianDocumentInReportingService() {
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().build();
        clinicianMigration.save(clinician);
        ClinicianRequest clinicianRequest = new ClinicianRequestMapper(clinician).map();

        verify(clinicianReportingService).save(clinicianRequest);
    }
}
