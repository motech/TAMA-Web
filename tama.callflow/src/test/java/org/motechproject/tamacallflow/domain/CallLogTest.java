package org.motechproject.tamacallflow.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.tamadomain.domain.CallLog;
import org.motechproject.tamacallflow.ivr.StandardIVRResponse;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class CallLogTest {
    private TamaIVRMessage ivrMessage;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        properties.put(TamaIVRMessage.CONTENT_LOCATION_URL, "http://locallhost/");
        ivrMessage = new TamaIVRMessage(properties);
    }

    @Test
    public void callTypeIsAuthenticatedWhenPatientIdIsSet() {
        CallLog callLog = callWhichGoesBeyondAuthentication();
        assertEquals(CallLog.CALL_TYPE_AUTHENTICATED, callLog.getCallType());
        List<CallEvent> callEvents = callLog.getCallEvents();
        assertEquals(2, callEvents.size());
    }

    @Test
    public void maskAuthenticationDTMFData() {
        CallLog callLog = callWhichGoesBeyondAuthentication();
        callLog.maskAuthenticationPin();
        List<CallEvent> callEvents = callLog.getCallEvents();
        CallEventCustomData customData = callEvents.get(0).getData();
        assertEquals("****", customData.getFirst(CallEventConstants.DTMF_DATA));
    }

    private CallLog callWhichGoesBeyondAuthentication() {
        CallLog callLog = new CallLog("patientID");
        callLog.setCallEvents(Arrays.asList(addAuthenticationEventCustomData(new CallEvent(IVREvent.GotDTMF.toString())),
                                            appendNonAuthenticationEventParams(new CallEvent(IVREvent.GotDTMF.toString()))));
        return callLog;
    }

    private CallEvent addAuthenticationEventCustomData(CallEvent callEvent) {
        final KookooIVRResponseBuilder ivrResponseBuilder = StandardIVRResponse.signatureTuneAndCollectDTMF("3423434");
        callEvent.appendData(CallEventConstants.DTMF_DATA, "1234");
        callEvent.appendData(CallEventConstants.CUSTOM_DATA_LIST, ivrResponseBuilder.create(ivrMessage));
        return callEvent;
    }

    private CallEvent appendNonAuthenticationEventParams(CallEvent callEvent) {
        final KookooIVRResponseBuilder responseBuilder = new KookooIVRResponseBuilder();
        responseBuilder.withPlayAudios("foo");
        callEvent.appendData(CallEventConstants.DTMF_DATA, "2");
        callEvent.appendData(CallEventConstants.CUSTOM_DATA_LIST, responseBuilder.create(ivrMessage));
        return callEvent;
    }
}