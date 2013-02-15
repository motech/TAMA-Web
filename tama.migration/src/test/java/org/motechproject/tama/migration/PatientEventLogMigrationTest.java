package org.motechproject.tama.migration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.migration.repository.PagedPatientEventsRepository;
import org.motechproject.tama.patient.domain.PatientEvent;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.tama.reporting.service.PatientEventReportingService;
import org.motechproject.tama.reports.contract.PatientEventRequest;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientEventLogMigrationTest {

    @Mock
    private PatientEventReportingService patientEventReportingService;
    @Mock
    private PagedPatientEventsRepository pagedPatientEventsRepository;

    private PatientEventLogMigration patientEventLogMigration;

    @Before
    public void setup() {
        initMocks(this);
        patientEventLogMigration = new PatientEventLogMigration(pagedPatientEventsRepository, patientEventReportingService);
    }

    @Test
    public void shouldMigratePatientEventLogs() {
        PatientEventLog patientEventLog = new PatientEventLog("patientDocId", PatientEvent.Activation);

        when(pagedPatientEventsRepository.get(0, 100)).thenReturn(asList(patientEventLog));
        patientEventLogMigration.migrate();
        verify(patientEventReportingService).save(any(PatientEventRequest.class));
    }
}
