package org.motechproject.tamacallflow.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.tamacallflow.mapper.CallLogMapper;
import org.motechproject.tamadomain.builder.ClinicBuilder;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.domain.CallLog;
import org.motechproject.tamadomain.domain.Clinic;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllCallLogs;
import org.motechproject.tamadomain.repository.AllPatients;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        when(allPatients.findAllByMobileNumber("phoneNumber")).thenReturn(new ArrayList<Patient>() {{ add(patient); }});
        when(kookooCallDetailRecordsService.get(callId)).thenReturn(kookooCallDetailRecord);
        when(callLogMapper.toCallLog(patientDocId, kookooCallDetailRecord)).thenReturn(new CallLog());
        callLoggingService.log(callId, patientDocId);

        ArgumentCaptor<CallLog> logCapture = ArgumentCaptor.forClass(CallLog.class);
        verify(allCallLogs).add(logCapture.capture());

        assertEquals(clinic.getId(), logCapture.getValue().clinicId());
        assertEquals(0, logCapture.getValue().getLikelyPatientIds().size());
    }

    @Test
    public void shouldLogCallWithoutClinicIdWhenPatientIsNotKnown() {
        KookooCallDetailRecord kookooCallDetailRecord = callDetailRecord();
        String patientDocumentId = null;

        String callId = "callId";
        when(kookooCallDetailRecordsService.get(callId)).thenReturn(kookooCallDetailRecord);
        when(allPatients.get(patient.getId())).thenReturn(patient);
        when(allPatients.findAllByMobileNumber("phoneNumber")).thenReturn(new ArrayList<Patient>() {{ add(patient); }});
        when(callLogMapper.toCallLog(patientDocumentId, kookooCallDetailRecord)).thenReturn(new CallLog());
        callLoggingService.log(callId, patientDocumentId);

        ArgumentCaptor<CallLog> logCapture = ArgumentCaptor.forClass(CallLog.class);
        verify(allCallLogs).add(logCapture.capture());

        assertEquals(clinic.getId(), logCapture.getValue().clinicId());
        assertEquals(1, logCapture.getValue().getLikelyPatientIds().size());
        assertEquals(patient.getId(), logCapture.getValue().getLikelyPatientIds().get(0));
    }

    private KookooCallDetailRecord callDetailRecord() {
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(null, "vendorCallId");
        CallDetailRecord cdc = new CallDetailRecord(null, null);
        cdc.setPhoneNumber("phoneNumber");
        kookooCallDetailRecord.setCallDetailRecord(cdc);
        return kookooCallDetailRecord;
    }
}