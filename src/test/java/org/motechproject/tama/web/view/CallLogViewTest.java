package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.service.ivr.IVRRequest;

import static junit.framework.Assert.assertEquals;

public class CallLogViewTest {

    private CallLogView callLogView;

    @Before
    public void setUp(){
        callLogView = new CallLogView();
    }

    @Test
    public void titleShouldBeTamaCalledPatientId_WhenCallDirectionIsOutbound() {
        callLogView.setCallDirection(IVRRequest.CallDirection.Outbound);
        callLogView.setPatientId("patientId");

        assertEquals("Tama called patientId", callLogView.getTitle());
    }

    @Test
    public void titleShouldBePatientIdCalledTama_WhenCallDirectionIsInbound() {
        callLogView.setCallDirection(IVRRequest.CallDirection.Inbound);
        callLogView.setPatientId("patientId");

        assertEquals("patientId called Tama", callLogView.getTitle());
    }
}
