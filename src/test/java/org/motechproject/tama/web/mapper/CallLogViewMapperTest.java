package org.motechproject.tama.web.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.web.view.CallLogView;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CallLogViewMapperTest {

    private CallLogViewMapper callLogViewMapper;

    @Mock
    private AllPatients allPatients;

    @Before
    public void setUp() {
        initMocks(this);
        callLogViewMapper = new CallLogViewMapper(allPatients);
    }

    @Test
    public void shouldSetPatientId() {
        CallLog callLog = new CallLog();
        callLog.setPatientDocumentId("patientDocumentId");
        CallLog anotherCallLog = new CallLog();
        anotherCallLog.setPatientDocumentId("anotherPatientDocumentId");

        List<CallLog> callLogs = Arrays.asList(callLog, anotherCallLog);

        when(allPatients.get("patientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("patientId").build());
        when(allPatients.get("anotherPatientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("anotherPatientId").build());
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(callLogs);

        assertEquals(2, callLogViews.size());
        assertEquals("patientId", callLogViews.get(0).getPatientId());
        assertEquals("anotherPatientId", callLogViews.get(1).getPatientId());
    }

    @Test
    public void shouldSetOtherCallDetails() {
        CallLog defaultCallLog = new CallLog();
        defaultCallLog.setPatientDocumentId("patientDocumentId");
        List<CallLog> callLogs = Arrays.asList(defaultCallLog);

        when(allPatients.get("patientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("patientId").build());
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(callLogs);
        CallLogView callLogView = callLogViews.get(0);

        assertEquals(defaultCallLog, callLogView.getCallLog());
    }
}
