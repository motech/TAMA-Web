package org.motechproject.tama.migration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.migration.repository.PagedSMSLogRepository;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.reporting.service.SMSReportingService;
import org.motechproject.tama.reports.contract.SMSLogRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSLogMigrationTest {

    @Mock
    private SMSReportingService smsReportingService;
    @Mock
    private AllClinics allClinics;
    @Mock
    private PagedSMSLogRepository repository;
    @Mock
    private AllPatients allPatients;

    private SMSLogMigration smsLogMigration;

    @Before
    public void setup() {
        initMocks(this);
        smsLogMigration = new SMSLogMigration(repository, allPatients, allClinics, smsReportingService);
    }

    @Test
    public void shouldMigrateToClinicianSMSWhenSMSWasSentToClinician() {
        SMSLog log = new SMSLog("1234", "(PID01) message");

        when(allClinics.findByPhoneNumber("1234")).thenReturn(new Clinic.ClinicianContact());
        smsLogMigration.save(log);

        ArgumentCaptor<SMSLogRequest> captor = ArgumentCaptor.forClass(SMSLogRequest.class);
        verify(smsReportingService).save(captor.capture());
        assertEquals("C", captor.getValue().getSmsType());
    }

    @Test
    public void shouldMigrateToOTCSMSWhenSMSWasSentToPatient() {
        SMSLog log = new SMSLog("1234", "message");

        when(allPatients.findByMobileNumber("1234")).thenReturn(new Patient());
        smsLogMigration.save(log);

        ArgumentCaptor<SMSLogRequest> captor = ArgumentCaptor.forClass(SMSLogRequest.class);
        verify(smsReportingService).save(captor.capture());
        assertEquals("O", captor.getValue().getSmsType());
    }
}
