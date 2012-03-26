package org.motechproject.tama.web.view;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;

public class CallFlowGroupViewTest {

    private CallFlowGroupView callFlowGroupView;
    private CallEventView callEventView;

    @Before
    public void setUp(){
        callEventView = new CallEventView(new CallEvent(IVREvent.GotDTMF.toString()));
        callFlowGroupView = new CallFlowGroupView("flow", callEventView);
    }

    @Test
    public void shouldReturnCallFlowDuration() {
        DateTime flowStartTime = DateUtil.now();

        callFlowGroupView.setFlowStartTime(flowStartTime);
        callFlowGroupView.setFlowEndTime(flowStartTime.plusMinutes(2).plusSeconds(3));

        assertEquals(123, callFlowGroupView.getFlowDuration());
    }
}
