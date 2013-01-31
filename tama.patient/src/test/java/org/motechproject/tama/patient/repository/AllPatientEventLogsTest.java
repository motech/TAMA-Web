package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.tama.patient.reporting.PatientEventRequestMapper;
import org.motechproject.tama.reporting.service.PatientEventReportingService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllPatientEventLogsTest {

    private AllPatientEventLogs allPatientEventLogs;
    @Mock
    private PatientEventReportingService patientEventReportingService;
    @Mock
    private CouchDbConnector couchDbConnector;

    @Before
    public void setup(){
        initMocks(this);
        allPatientEventLogs = new AllPatientEventLogs(couchDbConnector, patientEventReportingService);
    }

    @Test
    public void shouldReportPatientEvent(){
        PatientEventLog entity = new PatientEventLog();
        allPatientEventLogs.add(entity, "user");
        verify(patientEventReportingService).save(new PatientEventRequestMapper(entity).map("user"));
    }
}
