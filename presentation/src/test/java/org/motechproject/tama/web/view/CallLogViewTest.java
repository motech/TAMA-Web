package org.motechproject.tama.web.view;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.tama.ivr.logging.domain.CallLog;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class CallLogViewTest {

    private CallLogView callLogView;

    @Test
    public void titleShouldBeTamaCalledPatientId_WhenCallDirectionIsOutbound_AndPatientIsAuthenticated() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Outbound);
        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());

        assertEquals("Tama called patientId || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void titleShouldBeTamaCalledPatientId_WhenCallDirectionIsOutbound_AndPatientIsNotAuthenticated() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Outbound);
        callLogView = new CallLogView(null, callLog, "clinicName", new ArrayList<String>(){{add("P1");add("P2");}});

        assertEquals("Tama called P1 or P2 || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void titleShouldBePatientIdCalledTama_WhenCallDirectionIsInbound_AndPatientIsAuthenticated() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Inbound);
        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());

        assertEquals("patientId called Tama || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void titleShouldBePatientIdCalledTama_WhenCallDirectionIsInbound_AndPatientIsNotAuthenticated() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Inbound);
        callLogView = new CallLogView(null, callLog, "clinicName", new ArrayList<String>(){{add("P1");}});

        assertEquals("P1 called Tama || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void shouldFormatDateToRemove_IST_And_Time(){
        CallLog callLog = setUpCallLogs();

        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());

        assertEquals("Fri Oct 07 2011", callLogView.getCallDateFromCallLogDateTime());
    }

    private CallLog setUpCallLogs() {
        CallLog callLog = new CallLog();
        callLog.setStartTime(new DateTime(2011, 10, 7, 0, 0, 0));
        callLog.setEndTime(new DateTime(2011, 10, 7, 0, 0, 0).plusMinutes(2));
        return callLog;
    }
}
