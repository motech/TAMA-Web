package org.motechproject.tama.ivr.logging.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AllCallLogsTest extends SpringIntegrationTest {

    @Autowired
    private AllCallLogs allCallLogs;

    @Test
    public void shouldReturnLatestCallLogForPatient() {
        CallLog callLog = new CallLog("patientDocId");
        callLog.setStartTime(new DateTime(2011, 10, 7, 10, 20, 0, 0));
        allCallLogs.add(callLog);
        callLog = new CallLog("patientDocId");
        callLog.setStartTime(new DateTime(2011, 10, 7, 10, 24, 0, 0));
        allCallLogs.add(callLog);
        callLog = new CallLog("patientDocId");
        DateTime expectedLogTime = new DateTime(2011, 10, 8, 10, 24, 0, 0);
        callLog.setStartTime(expectedLogTime);
        allCallLogs.add(callLog);
        callLog = new CallLog("patientDocId2");
        callLog.setStartTime(new DateTime(2011, 10, 9, 10, 24, 0, 0));
        allCallLogs.add(callLog);
        callLog = new CallLog("patientDocId");
        callLog.setCallId("callId");
        callLog.setStartTime(new DateTime(2011, 10, 9, 11, 24, 0, 0));
        allCallLogs.add(callLog);
        assertEquals(expectedLogTime.getMillis(), allCallLogs.getLatestOpenCallLog("patientDocId").getStartTime().getMillis());
    }
}
