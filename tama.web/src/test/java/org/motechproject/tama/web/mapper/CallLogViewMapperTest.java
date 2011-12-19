package org.motechproject.tama.web.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.web.view.CallLogView;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
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
        List<CallLog> callLogs = createCallLogs();

        when(allPatients.get("patientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("patientId").withClinic(ClinicBuilder.startRecording().withName("clinic").build()).build());
        when(allPatients.get("anotherPatientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("anotherPatientId").withClinic(ClinicBuilder.startRecording().withName("anotherClinic").build()).build());
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(callLogs);

        assertEquals(2, callLogViews.size());
        assertEquals("patientId", callLogViews.get(0).getPatientId());
        assertEquals("anotherPatientId", callLogViews.get(1).getPatientId());
    }

    @Test
    public void shouldSetOtherCallDetails() {
        CallLog defaultCallLog = new CallLog();
        defaultCallLog.setPatientDocumentId("patientDocumentId");
        defaultCallLog.setStartTime(DateUtil.now());
        defaultCallLog.setEndTime(DateUtil.now().plusMinutes(2));
        defaultCallLog.setLikelyPatientIds(new ArrayList<String>() {{
            add("patientDocumentId");
        }});

        List<CallLog> callLogs = Arrays.asList(defaultCallLog);

        when(allPatients.get("patientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("patientId").withClinic(ClinicBuilder.startRecording().withName("clinic").build()).build());
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(callLogs);
        CallLogView callLogView = callLogViews.get(0);

        assertEquals(defaultCallLog, callLogView.getCallLog());
        assertEquals("patientId", callLogView.getLikelyPatientIds().get(0));
    }

    @Test
    public void shouldSetClinicName() {
        List<CallLog> callLogs = createCallLogs();

        when(allPatients.get("patientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("patientId").withClinic(ClinicBuilder.startRecording().withName("clinic").build()).build());
        when(allPatients.get("anotherPatientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("anotherPatientId").withClinic(ClinicBuilder.startRecording().withName("anotherClinic").build()).build());

        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(callLogs);
        assertEquals(2, callLogViews.size());
        assertEquals("clinic", callLogViews.get(0).getClinicName());
        assertEquals("anotherClinic", callLogViews.get(1).getClinicName());
    }

    @Test
    public void shouldMapCallLogs_WhenPatientIsNotAuthenticated() {
        List<CallLog> callLogs = new ArrayList<CallLog>() {{
            add(new CallLog() {{
                setLikelyPatientIds(new ArrayList<String>() {{
                    add("patientDocumentId");
                }});
                setStartTime(DateUtil.now());
                setEndTime(DateUtil.now().plusMinutes(2));
            }});
        }};

        when(allPatients.get("patientDocumentId")).thenReturn(PatientBuilder.startRecording().withPatientId("patientId").withClinic(ClinicBuilder.startRecording().withName("clinic").build()).build());

        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(callLogs);
        assertEquals(1, callLogViews.size());
        assertEquals("clinic", callLogViews.get(0).getClinicName());
    }

    @Test
    public void shouldMapCallLogs_WhenPatientIsNotRegistered() {
        List<CallLog> callLogs = new ArrayList<CallLog>() {{
            add(new CallLog() {{
                setPatientDocumentId(null);
                setLikelyPatientIds(new ArrayList<String>());
            }});
        }};

        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(callLogs);
        assertEquals(0, callLogViews.size());
    }

    private List<CallLog> createCallLogs() {
        CallLog callLog = new CallLog();
        callLog.setPatientDocumentId("patientDocumentId");
        callLog.setStartTime(DateUtil.now());
        callLog.setEndTime(DateUtil.now().plusMinutes(2));
        CallLog anotherCallLog = new CallLog();
        anotherCallLog.setPatientDocumentId("anotherPatientDocumentId");
        anotherCallLog.setStartTime(DateUtil.now());
        anotherCallLog.setEndTime(DateUtil.now().plusMinutes(2));
        return Arrays.asList(callLog, anotherCallLog);
    }

}
