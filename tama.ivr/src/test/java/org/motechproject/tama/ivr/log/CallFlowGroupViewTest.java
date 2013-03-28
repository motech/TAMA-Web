package org.motechproject.tama.ivr.log;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;

public class CallFlowGroupViewTest {

    private CallFlowGroupView callFlowGroupView;
    private CallEventView callEventView;

    @Test
    public void shouldReturnCallFlowDuration() {
        callEventView = new CallEventView(new CallEvent(IVREvent.GotDTMF.toString()));
        callFlowGroupView = new CallFlowGroupView("flow", callEventView);
        DateTime flowStartTime = DateUtil.now();

        callFlowGroupView.setFlowStartTime(flowStartTime);
        callFlowGroupView.setFlowEndTime(flowStartTime.plusMinutes(2).plusSeconds(3));

        assertEquals(123, callFlowGroupView.getFlowDuration());
        assertEquals("flow", callFlowGroupView.toString());
    }

    @Test
    public void shouldAddNextEvent() {
        CallEventView firstEventView = new CallEventView(new CallEvent(IVREvent.GotDTMF.toString()));
        CallEventView secondEventView = new CallEventView(new CallEvent(IVREvent.GotDTMF.toString()));
        CallEventView thirdEventView = new CallEventView(new CallEvent(IVREvent.GotDTMF.toString()));

        callFlowGroupView = new CallFlowGroupView("flow", firstEventView);
        callFlowGroupView.add(secondEventView);
        callFlowGroupView.add(thirdEventView);

        assertEquals(secondEventView, firstEventView.getNextEvent());
        assertEquals(thirdEventView, secondEventView.getNextEvent());
        assertEquals(null, thirdEventView.getNextEvent());
    }

    @Test
    public void shouldReturnTheMissedCallFlow() {
        callEventView = new CallEventView(getMissedCallEvent());
        callFlowGroupView = new CallFlowGroupView(CallTypeConstants.MISSED, callEventView);
        assertEquals("Missed Pill Reminder", callFlowGroupView.toString());
    }

    private CallEvent getMissedCallEvent() {
        CallEvent callEvent = new CallEvent("Missed");
        CallEventCustomData callEventCustomData = new CallEventCustomData();
        callEventCustomData.add(IVRService.CALL_TYPE, "Pill Reminder");
        callEvent.setData(callEventCustomData);
        return callEvent;
    }
}
