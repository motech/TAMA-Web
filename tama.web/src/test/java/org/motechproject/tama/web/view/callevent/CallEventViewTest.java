package org.motechproject.tama.web.view.callevent;

import org.junit.Test;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.tama.ivr.StandardIVRResponse;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.common.domain.TAMAMessageTypes;
import org.motechproject.tama.ivr.log.CallEventView;

import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CallEventViewTest {
    @Test
    public void hasInputWhenEventTypeIsGotDtmf() {
        CallEventView callEventView = new CallEventView(new CallEvent(IVREvent.GotDTMF.toString()));
        assertTrue(callEventView.isUserInputAvailable());
    }

    @Test
    public void isMissedCall() {
        CallEventView callEventView = new CallEventView(new CallEvent(IVREvent.Missed.toString()));
        assertTrue(callEventView.isMissedCall());
    }

    @Test
    public void shouldReturnInputDtmf_IfDtmfDataAvilable() {
        CallEvent callEvent = new CallEvent(IVREvent.GotDTMF.toString());
        callEvent.appendData(CallEventConstants.DTMF_DATA, "1");
        CallEventView callEventView = new CallEventView(callEvent);
        assertEquals("1", callEventView.getUserInput());
    }

    @Test
    public void shouldReturnEmpty_WhenEventTypeNotDtmf() {
        CallEvent callEvent = new CallEvent(IVREvent.GotDTMF.toString());
        CallEventView callEventView = new CallEventView(callEvent);
        assertEquals("", callEventView.getUserInput());
    }

    @Test
    public void shouldReturnAListOfAllResponsesPlayed() {
        TamaIVRMessage ivrMessage = new TamaIVRMessage(new Properties());
        KookooIVRResponseBuilder responseBuilder = StandardIVRResponse.signatureTuneAndCollectDTMF("123");
        CallEvent callEvent = new CallEvent(IVREvent.NewCall.toString());
        callEvent.appendData(CallEventConstants.CUSTOM_DATA_LIST, responseBuilder.create(ivrMessage));
        CallEventView callEventView = new CallEventView(callEvent);

        List<String> content = callEventView.getResponses();
        assertEquals("signature_music", content.get(0));
    }

    @Test
    public void shouldReturnMessageCategory() {
        String category = TAMAMessageTypes.ALL_MESSAGES.name();

        CallEvent callEvent = new CallEvent(IVREvent.GotDTMF.toString());
        callEvent.appendData(TAMAIVRContext.MESSAGE_CATEGORY_NAME, category);

        CallEventView callEventView = new CallEventView(callEvent);
        assertEquals(TAMAMessageTypes.ALL_MESSAGES.getDisplayName(), callEventView.getPullMessagesCategory());
    }

    @Test
    public void shouldReturnEmptyWhenNoMessageCategoryIsPresent() {
        CallEvent callEvent = new CallEvent(IVREvent.GotDTMF.toString());
        CallEventView callEventView = new CallEventView(callEvent);
        assertTrue(callEventView.getPullMessagesCategory().isEmpty());
    }
}