package org.motechproject.tama.ivr.reporting;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.reports.contract.MessagesRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class MessagesRequestMapperTest extends BaseUnitTest {

    private CallLog callLog;
    private MessagesRequest messagesRequest;

    @Before
    public void setup() {
        setupCallLog();
        mockCurrentDate(DateUtil.now());
        messagesRequest = new MessagesRequestMapper(callLog).map(CallTypeConstants.MESSAGES, null);
    }

    private void setupCallLog() {
        callLog = new CallLog("patientDocumentId");
        callLog.setStartTime(DateUtil.now());
        callLog.setCallDirection(CallDirection.Inbound);
        callLog.setCallEvents(asList(messagesEvent(), endOfCallEvent()));
    }

    @Test
    public void shouldMapPatientDocumentId() {
        assertEquals(callLog.getPatientDocumentId(), messagesRequest.getPatientDocumentId());
    }

    @Test
    public void shouldMapNumberOfTimesMessagesAccessed() {
        assertEquals(Integer.valueOf(1), messagesRequest.getNumberOfTimesMessagesAccessed());
    }

    @Test
    public void shouldMapMessages() {
        assertEquals(asList("response1", "response2"), messagesRequest.getMessagesPlayed());
    }

    @Test
    public void shouldMapIndividualMessagesFlowAccessDurationInSeconds() {
        assertEquals(asList(60), messagesRequest.getIndividualMessagesAccessDurations());
    }

    @Test
    public void shouldMapTotalMessagesFlowsAccessDurationInSeconds() {
        assertEquals(Long.valueOf(60L), messagesRequest.getTotalMessagesAccessDuration());
    }

    @Test
    public void shouldMapCallDirection() {
        assertEquals("Incoming", messagesRequest.getCallDirection());
    }

    @Test
    public void shouldMapCallDate() {
        assertEquals(callLog.getStartTime().toDate(), messagesRequest.getCallDate());
    }

    private CallEvent endOfCallEvent() {
        CallEvent event = new CallEvent(CallState.MAIN_MENU.name());
        CallEventCustomData data = new CallEventCustomData();
        event.setTimeStamp(DateUtil.now().plusMinutes(1));
        event.setData(data);
        return event;
    }

    private CallEvent messagesEvent() {
        CallEvent event = new CallEvent(CallState.PULL_MESSAGES.name());
        CallEventCustomData data = new CallEventCustomData();
        data.add(CallEventConstants.CUSTOM_DATA_LIST, "<response><playaudio>http://localhost/response1.wav</playaudio><playaudio>http://localhost/response2.wav</playaudio></response>");
        data.add(CallEventConstants.CALL_STATE, CallState.PULL_MESSAGES.name());
        event.setTimeStamp(DateUtil.now());
        event.setData(data);
        return event;
    }
}
