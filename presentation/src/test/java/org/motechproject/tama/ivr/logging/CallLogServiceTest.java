package org.motechproject.tama.ivr.logging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.ivr.logging.mapper.CallLogMapper;
import org.motechproject.tama.ivr.logging.repository.AllCallLogs;
import org.motechproject.tama.ivr.logging.service.CallLogService;
import org.motechproject.tama.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogServiceTest {
    private CallLogService callLoggingService;
    @Mock
    private AllCallLogs allCallLogs;
    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private CallLogMapper callLogMapper;
    private Patient patient;
    private Clinic clinic;

    @Before
    public void setUp() {
        initMocks(this);
        callLoggingService = new CallLogService(allCallLogs, kookooCallDetailRecordsService, callLogMapper, allPatients);

        clinic = ClinicBuilder.startRecording().withDefaults().withId("clinicId").build();
        patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).build();
    }

    @Test
    public void shouldReturnAllCallLogs() {
        callLoggingService.getAll();
        verify(allCallLogs).getAll();
    }

    @Test
    public void shouldLogCall() {
        String patientDocId = "patientDocId";
        KookooCallDetailRecord kookooCallDetailRecord = callDetailRecord();
        String callId = "callId";

        when(allPatients.get(patientDocId)).thenReturn(patient);
        when(kookooCallDetailRecordsService.get(callId)).thenReturn(kookooCallDetailRecord);
        when(callLogMapper.toCallLog(patientDocId, kookooCallDetailRecord)).thenReturn(new CallLog());
        callLoggingService.log(callId, patientDocId);

        ArgumentCaptor<CallLog> logCapture = ArgumentCaptor.forClass(CallLog.class);
        verify(allCallLogs).add(logCapture.capture());
        assertEquals(clinic.getId(), logCapture.getValue().clinicId());
    }

    private KookooCallDetailRecord callDetailRecord() {
        return new KookooCallDetailRecord(null, "sdfdf");
    }

    @Test
    public void shouldLogCallWithoutClinicIdWhenPatientIsNotKnown() {
        KookooCallDetailRecord kookooCallDetailRecord = callDetailRecord();
        String patientDocumentId = null;

        String callId = "callId";
        when(kookooCallDetailRecordsService.get(callId)).thenReturn(kookooCallDetailRecord);
        when(callLogMapper.toCallLog(patientDocumentId, kookooCallDetailRecord)).thenReturn(new CallLog());
        callLoggingService.log(callId, patientDocumentId);

        ArgumentCaptor<CallLog> logCapture = ArgumentCaptor.forClass(CallLog.class);
        verify(allCallLogs).add(logCapture.capture());

        assertEquals(null, logCapture.getValue().clinicId());
    }
}