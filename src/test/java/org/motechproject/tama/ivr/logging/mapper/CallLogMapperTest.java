package org.motechproject.tama.ivr.logging.mapper;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class CallLogMapperTest {

    private CallLogMapper callLogMapper;

    @Before
    public void setUp(){
      callLogMapper = new CallLogMapper();
    }

    @Test
    public void shouldConvertKookooCallDetailRecordToCallLog() {
        CallDetailRecord callDetailRecord = CallDetailRecord.newIncomingCallRecord("phoneNumber");
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord);
        callDetailRecord.setEndDate(new Date());
        kookooCallDetailRecord.setId("callId");


        CallLog callLog = callLogMapper.toCallLog("patientDocId", kookooCallDetailRecord);

        assertEquals("patientDocId", callLog.getPatientDocumentId());
        assertEquals("callId", callLog.getCallId());
        assertEquals("phoneNumber", callLog.getPhoneNumber());
        assertEquals(callDetailRecord.getCallEvents(), callDetailRecord.getCallEvents());
        assertEquals(DateUtil.newDateTime(callDetailRecord.getStartDate()), callLog.getStartTime());
        assertEquals(DateUtil.newDateTime(callDetailRecord.getEndDate()), callLog.getEndTime());
    }
}
