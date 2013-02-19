package org.motechproject.tama.facility.repository;

import org.ektorp.CouchDbConnector;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.reporting.ClinicianRequestMapper;
import org.motechproject.tama.reporting.service.ClinicianReportingService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllCliniciansTest {

    @Mock
    private ClinicianReportingService clinicianReportingService;
    @Mock
    private CouchDbConnector db;
    @Mock
    private AllClinicians allClinicians;
    @Mock
    private AllClinics allClinics;
    @Mock
    private AllClinicianIds allClinicIds;
    @Mock
    private AllAuditRecords allAuditRecords;
    @Mock
    private PBEStringEncryptor encryptor;

    private Clinician clinician;


    @Before
    public void setup() {
        initMocks(this);
        clinician = ClinicianBuilder.startRecording().withDefaults().build();
        allClinicians = new AllClinicians(db, encryptor, allClinics, allClinicIds, allAuditRecords, clinicianReportingService);
    }

    @Test
    public void shouldReportClinicianAdded() {
        allClinicians.add(clinician, "admin");
        verify(db).create(clinician);
        verify(clinicianReportingService).save(new ClinicianRequestMapper(clinician).map());
    }

    @Test
    public void shouldReportClinicianUpdated() {
        when(db.get(Clinician.class, clinician.getId())).thenReturn(clinician);
        allClinicians.update(clinician, "admin");
        verify(clinicianReportingService).update(new ClinicianRequestMapper(clinician).map());

    }
}
