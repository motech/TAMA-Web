package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.ivr.logging.domain.CallLog;

import static junit.framework.Assert.assertEquals;

public class CallLogViewTest {

    private CallLogView callLogView;

    @Before
    public void setUp() {
        callLogView = new CallLogView("patientId", new CallLog());
    }

    @Test
    public void titleShouldBeTamaCalledPatientId_WhenCallDirectionIsOutbound() {
        CallLog callLog = new CallLog();
        callLog.setCallDirection(IVRRequest.CallDirection.Outbound);
        callLogView = new CallLogView("patientId", callLog);

        assertEquals("Tama called patientId", callLogView.getTitle());
    }

    @Test
    public void titleShouldBePatientIdCalledTama_WhenCallDirectionIsInbound() {
        CallLog callLog = new CallLog();
        callLog.setCallDirection(IVRRequest.CallDirection.Inbound);
        callLogView = new CallLogView("patientId", callLog);

        assertEquals("patientId called Tama", callLogView.getTitle());
    }
}
