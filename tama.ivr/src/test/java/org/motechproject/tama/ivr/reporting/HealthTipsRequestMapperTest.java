package org.motechproject.tama.ivr.reporting;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.reports.contract.HealthTipsRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class HealthTipsRequestMapperTest extends BaseUnitTest {

    private CallLog callLog;
    private HealthTipsRequest healthTipsRequest;

    @Before
    public void setup() {
        setupCallLog();
        mockCurrentDate(DateUtil.now());
        healthTipsRequest = new HealthTipsRequestMapper(callLog).map();
    }

    private void setupCallLog() {
        callLog = new CallLog("patientDocumentId");
        callLog.setStartTime(DateUtil.now());
        callLog.setCallDirection(CallDirection.Inbound);
        callLog.setCallEvents(asList(healthTipsEvent(), endOfCallEvent()));
    }

    @Test
    public void shouldMapPatientDocumentId() {
        assertEquals(callLog.getPatientDocumentId(), healthTipsRequest.getPatientDocumentId());
    }

    @Test
    public void shouldMapNumberOfTimesHealthTipsAccessed() {
        assertEquals(Integer.valueOf(1), healthTipsRequest.getNumberOfTimesHealthTipsAccessed());
    }

    @Test
    public void shouldMapHealthTips() {
        assertEquals(asList("response1", "response2"), healthTipsRequest.getHealthTipsPlayed());
    }

    @Test
    public void shouldMapIndividualHealthTipFlowAccessDurationInSeconds() {
        assertEquals(asList(60), healthTipsRequest.getIndividualHealthTipsAccessDurations());
    }

    @Test
    public void shouldMapTotalHealthTipFlowsAccessDurationInSeconds() {
        assertEquals(Long.valueOf(60L), healthTipsRequest.getTotalHealthTipsAccessDuration());
    }

    @Test
    public void shouldMapCallDirection() {
        assertEquals("Incoming", healthTipsRequest.getCallDirection());
    }

    @Test
    public void shouldMapCallDate(){
        assertEquals(callLog.getStartTime().toDate(), healthTipsRequest.getCallDate());
    }

    private CallEvent endOfCallEvent() {
        CallEvent event = new CallEvent(CallState.END_OF_FLOW.name());
        CallEventCustomData data = new CallEventCustomData();
        event.setTimeStamp(DateUtil.now().plusMinutes(1));
        event.setData(data);
        return event;
    }

    private CallEvent healthTipsEvent() {
        CallEvent event = new CallEvent(CallState.HEALTH_TIPS.name());
        CallEventCustomData data = new CallEventCustomData();
        data.add(CallEventConstants.CUSTOM_DATA_LIST, "<response><playaudio>http://localhost/response1.wav</playaudio><playaudio>http://localhost/response2.wav</playaudio></response>");
        data.add(CallEventConstants.CALL_STATE, CallState.HEALTH_TIPS.name());
        event.setTimeStamp(DateUtil.now());
        event.setData(data);
        return event;
    }
}
