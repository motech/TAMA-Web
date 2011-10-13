package org.motechproject.tama.web.view;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.tama.ivr.logging.domain.CallLog;

import static junit.framework.Assert.assertEquals;

public class CallLogViewTest {

    private CallLogView callLogView;

    @Test
    public void titleShouldBeTamaCalledPatientId_WhenCallDirectionIsOutbound() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Outbound);
        callLogView = new CallLogView("patientId", callLog, "clinicName");

        assertEquals("Tama called patientId || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void titleShouldBePatientIdCalledTama_WhenCallDirectionIsInbound() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Inbound);
        callLogView = new CallLogView("patientId", callLog, "clinicName");

        assertEquals("patientId called Tama || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void shouldFormatDateToRemove_IST_And_Time(){
        CallLog callLog = setUpCallLogs();

        callLogView = new CallLogView("patientId", callLog, "clinicName");

        assertEquals("Fri Oct 07 2011", callLogView.getCallDateFromCallLogDateTime());
    }

    private CallLog setUpCallLogs() {
        CallLog callLog = new CallLog();
        callLog.setStartTime(new DateTime(2011, 10, 7, 0, 0, 0));
        callLog.setEndTime(new DateTime(2011, 10, 7, 0, 0, 0).plusMinutes(2));
        return callLog;
    }
}
