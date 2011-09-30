package org.motechproject.tama.web.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.web.view.CallLogView;

import java.util.Arrays;
import java.util.HashMap;
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
        CallLog defaultCallLog = getDefaultCallLog();
        List<CallLog> callLogs = Arrays.asList(defaultCallLog);

        when(allPatients.get("patientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("patientId").build());
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(callLogs);
        CallLogView callLogView = callLogViews.get(0);

        assertCallLogView(callLogView);
    }

    private CallLog getDefaultCallLog() {
        CallLog callLog = new CallLog();
        callLog.setPatientDocumentId("patientDocumentId");
        callLog.setCallDirection(IVRRequest.CallDirection.Inbound);
        callLog.setCallId(new String("callId"));
        callLog.setCallType(new String("pill-reminder-call"));
        callLog.setEndTime(new DateTime(2012, 9, 30, 3, 15));
        callLog.setStartTime(new DateTime(2012, 9, 30, 3, 05));
        callLog.setPhoneNumber(new String("9191919191"));
        callLog.setCallEvents(Arrays.asList(new CallEvent("NewCall", new HashMap<String, String>() {
            {
                put("dtmfData", "1");
            }
        })));
        return callLog;
    }

    private static void assertCallLogView(CallLogView callLogView) {
        assertEquals("NewCall", callLogView.getCallEvents().get(0).getName());
        assertEquals("1", callLogView.getCallEvents().get(0).getData().get("dtmfData"));
        assertEquals(IVRRequest.CallDirection.Inbound, callLogView.getCallDirection());
        assertEquals("callId", callLogView.getCallId());
        assertEquals("pill-reminder-call", callLogView.getCallType());
        assertEquals(new DateTime(2012, 9, 30, 3, 05), callLogView.getStartTime());
        assertEquals(new DateTime(2012, 9, 30, 3, 15), callLogView.getEndTime());
        assertEquals("9191919191", callLogView.getPhoneNumber());
    }
}
