package org.motechproject.tama.ivr.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
import org.motechproject.tama.ivr.mapper.CallLogMapper;
import org.motechproject.tama.ivr.reporting.MessagesRequestMapper;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.reporting.service.CallLogReportingService;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogServiceTest {
    public static final String PATIENT_ID = "patientId";
    private CallLogService callLoggingService;
    @Mock
    private AllCallLogs allCallLogs;
    @Mock
    private CallLogReportingService callLogReportingService;
    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private CallLogMapper callLogMapper;
    @Mock
    private Patient patient;
    private Clinic clinic;

    @Before
    public void setUp() {
        initMocks(this);
        callLoggingService = new CallLogService(allCallLogs, kookooCallDetailRecordsService, callLogMapper, allPatients, callLogReportingService);

        clinic = ClinicBuilder.startRecording().withDefaults().withId("clinicId").build();
        patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).withPatientId("testPatient").build();
    }

    @Test
    public void shouldReturnAllCallLogs() {
        callLoggingService.getAll();
        verify(allCallLogs).getAll();
    }

    @Test
    public void shouldReportMessages() {
        String callId = "callId";
        String patientDocumentId = "patientDocumentId";
        KookooCallDetailRecord kookooCallDetailRecord = callDetailRecord();
        CallLog log = mock(CallLog.class);

        when(log.getStartTime()).thenReturn(DateUtil.now());
        when(allPatients.get(patientDocumentId)).thenReturn(PatientBuilder.startRecording().withDefaults().build());
        when(kookooCallDetailRecordsService.get(callId)).thenReturn(kookooCallDetailRecord);
        when(callLogMapper.toCallLog(patientDocumentId, kookooCallDetailRecord)).thenReturn(log);
        callLoggingService.log(callId, patientDocumentId);
        verify(callLogReportingService).reportMessages(new MessagesRequestMapper(log).map(CallTypeConstants.MESSAGES, CallTypeConstants.PUSHED_MESSAGES));
    }

    @Test
    public void shouldLogCall() {
        String patientDocId = "patientDocId";
        KookooCallDetailRecord kookooCallDetailRecord = callDetailRecord();
        String callId = "callId";
        CallLog callLog = new CallLog();
        callLog.setStartTime(DateUtil.now());

        when(allPatients.get(patientDocId)).thenReturn(patient);
        when(allPatients.findAllByMobileNumber("phoneNumber")).thenReturn(new ArrayList<Patient>() {{
            add(patient);
        }});

        when(kookooCallDetailRecordsService.get(callId)).thenReturn(kookooCallDetailRecord);
        when(callLogMapper.toCallLog(patientDocId, kookooCallDetailRecord)).thenReturn(callLog);
        callLoggingService.log(callId, patientDocId);

        ArgumentCaptor<CallLog> logCapture = ArgumentCaptor.forClass(CallLog.class);
        verify(allCallLogs).add(logCapture.capture());

        assertEquals(clinic.getId(), logCapture.getValue().clinicId());
        assertEquals(0, logCapture.getValue().getLikelyPatientIds().size());
        assertEquals(patient.getPatientId(), logCapture.getValue().patientId());
        assertEquals("en", logCapture.getValue().callLanguage());
    }

    @Test
    public void shouldLogCallWithoutClinicId_WhenPatientIsNotKnown() {
        KookooCallDetailRecord kookooCallDetailRecord = callDetailRecord();
        String patientDocumentId = null;
        CallLog callLog = new CallLog();
        callLog.setStartTime(DateUtil.now());
        String callId = "callId";

        when(kookooCallDetailRecordsService.get(callId)).thenReturn(kookooCallDetailRecord);
        when(allPatients.get(patient.getId())).thenReturn(patient);
        when(allPatients.findAllByMobileNumber("phoneNumber")).thenReturn(new ArrayList<Patient>() {{
            add(patient);
        }});

        when(callLogMapper.toCallLog(patientDocumentId, kookooCallDetailRecord)).thenReturn(callLog);
        callLoggingService.log(callId, patientDocumentId);

        ArgumentCaptor<CallLog> logCapture = ArgumentCaptor.forClass(CallLog.class);
        verify(allCallLogs).add(logCapture.capture());

        assertEquals(clinic.getId(), logCapture.getValue().clinicId());
        assertEquals(1, logCapture.getValue().getLikelyPatientIds().size());
        assertEquals(patient.getId(), logCapture.getValue().getLikelyPatientIds().get(0));
        assertNull(logCapture.getValue().patientId());
        assertNull(logCapture.getValue().callLanguage());
    }

    @Test
    public void shouldReturnCallLogsForADateRange() {
        DateTime startDate = DateUtil.now();
        DateTime endDate = DateUtil.now().plusDays(1);
        callLoggingService.getLogsForDateRange(new CallLogSearch(startDate, endDate, CallLog.CallLogType.Answered, null, true, null));

        verify(allCallLogs).findCallLogsForDateRange(Matchers.<CallLogSearch>any());
    }

    @Test
    public void shouldReturnCallLogsForADateRange_AndGivenPatientId() {
        DateTime startDate = DateUtil.now();
        DateTime endDate = DateUtil.now().plusDays(1);
        callLoggingService.getLogsForDateRange(new CallLogSearch(startDate, endDate, CallLog.CallLogType.Answered, PATIENT_ID, true, null));

        verify(allCallLogs).findCallLogsForDateRangeAndPatientId(Matchers.<CallLogSearch>any());
    }

    @Test
    public void shouldReturnTotalNumberOfCallLogsForADateRange() {
        DateTime startDate = DateUtil.now();
        DateTime endDate = DateUtil.now().plusDays(1);
        callLoggingService.getTotalNumberOfLogs(new CallLogSearch(startDate, endDate, CallLog.CallLogType.Answered, "", true, null));

        verify(allCallLogs).findTotalNumberOfCallLogsForDateRange(Matchers.<CallLogSearch>any());
    }

    @Test
    public void shouldReturnTotalNumberOfCallLogsForADateRange_AndPatientId() {
        DateTime startDate = DateUtil.now();
        DateTime endDate = DateUtil.now().plusDays(1);
        callLoggingService.getTotalNumberOfLogs(new CallLogSearch(startDate, endDate, CallLog.CallLogType.Answered, PATIENT_ID, true, null));

        verify(allCallLogs).findTotalNumberOfCallLogsForDateRangeAndPatientId(Matchers.<CallLogSearch>any());
    }

    @Test
    public void shouldReturnCallLogsForADateRange_AndClinic() {
        DateTime startDate = DateUtil.now();
        DateTime endDate = DateUtil.now().plusDays(1);
        callLoggingService.getLogsForDateRange(new CallLogSearch(startDate, endDate, CallLog.CallLogType.Answered, "", false, "clinic"));

        verify(allCallLogs).findCallLogsForDateRangeAndClinic(Matchers.<CallLogSearch>any());
    }

    @Test
    public void shouldReturnCallLogsForADateRange_AndPatientId_AndClinic() {
        DateTime startDate = DateUtil.now();
        DateTime endDate = DateUtil.now().plusDays(1);
        callLoggingService.getLogsForDateRange(new CallLogSearch(startDate, endDate, CallLog.CallLogType.Answered, PATIENT_ID, false, "clinic"));

        verify(allCallLogs).findCallLogsForDateRangePatientIdAndClinic(Matchers.<CallLogSearch>any());
    }

    @Test
    public void shouldReturnTotalNumberOfCallLogsForADateRange_AndClinic() {
        DateTime startDate = DateUtil.now();
        DateTime endDate = DateUtil.now().plusDays(1);
        callLoggingService.getTotalNumberOfLogs(new CallLogSearch(startDate, endDate, CallLog.CallLogType.Answered, "", false, "clinic"));

        verify(allCallLogs).findTotalNumberOfCallLogsForDateRangeAndClinic(Matchers.<CallLogSearch>any());
    }

    @Test
    public void shouldReturnTotalNumberOfCallLogsForADateRange_AndPatientId_AndClinic() {
        DateTime startDate = DateUtil.now();
        DateTime endDate = DateUtil.now().plusDays(1);
        callLoggingService.getTotalNumberOfLogs(new CallLogSearch(startDate, endDate, CallLog.CallLogType.Answered, PATIENT_ID, false, "clinic"));

        verify(allCallLogs).findTotalNumberOfCallLogsForDateRangePatientIdAndClinic(Matchers.<CallLogSearch>any());
    }

    private KookooCallDetailRecord callDetailRecord() {
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(null, "vendorCallId");
        CallDetailRecord cdc = new CallDetailRecord(null, null);
        cdc.setPhoneNumber("phoneNumber");
        kookooCallDetailRecord.setCallDetailRecord(cdc);
        return kookooCallDetailRecord;
    }
}