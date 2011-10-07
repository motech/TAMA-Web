package org.motechproject.tama.ivr.logging.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.server.service.ivr.CallEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class CallLogTest {

    private CallLog callLog;
    private Map<String, String> authenticationEventParams;
    private Map<String, String> dtmfEventParams;

    @Before
    public void setUp() {
        callLog = new CallLog();

        authenticationEventParams = sampleAuthenticationEventParams();
        dtmfEventParams = sampleDtmfEventParams();
    }

    @Test
    public void shouldReturnCallTypeForACallWithOneAuthenticationEvent() {
        CallLog callLog = callWithOneAuthenticationEvent();

        assertEquals("symptomsReporting", callLog.getCallType());
    }

    @Test
    public void shouldReturnCallTypeForACall_WithMultipleUnsuccessfulAuthenticationEvents_AndOneSuccessfulEvent() {
        CallLog callLog = callWithMultipleAuthenticationEvents();

        assertEquals("symptomsReporting", callLog.getCallType());
    }

    @Test
    public void shouldReturnEmptyCallTypeForACall_NotAuthenticated() {
        CallLog callLog = new CallLog();
        Map<String, String> eventParams = Collections.emptyMap();
        callLog.setCallEvents(Arrays.asList(new CallEvent("NewCall",eventParams), new CallEvent("HangUp", eventParams)));

        assertEquals("Unauthenticated", callLog.getCallType());
    }

    private CallLog callWithOneAuthenticationEvent() {

        callLog.setCallEvents(Arrays.asList(new CallEvent("gotDtmf", authenticationEventParams), new CallEvent("gotDtmf", dtmfEventParams)));
        return callLog;

    }

    private CallLog callWithMultipleAuthenticationEvents() {

        callLog.setCallEvents(Arrays.asList(new CallEvent("gotDtmf", authenticationEventParams), new CallEvent("gotDtmf", authenticationEventParams), new CallEvent("gotDtmf", authenticationEventParams), new CallEvent("gotDtmf", dtmfEventParams)));
        return callLog;

    }

    private HashMap<String, String> sampleAuthenticationEventParams() {
        return new HashMap<String, String>() {
            {
                put(CallEventConstants.AUTHENTICATION_EVENT, "true");
                put(CallEventConstants.CALL_TYPE, "symptomsReporting");
            }
        };
    }

    private HashMap<String, String> sampleDtmfEventParams() {
        return new HashMap<String, String>() {
            {
                put(CallEventConstants.DTMF_DATA, "2");
            }
        };
    }
}
