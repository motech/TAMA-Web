package org.motechproject.tama.ivr.logging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.EndOfCallEvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.ivr.logging.mapper.CallLogMapper;
import org.motechproject.tama.ivr.logging.repository.AllCallLogs;
import org.motechproject.tama.ivr.logging.service.CallLogService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogServiceTest {

    private CallLogService callLoggingService;

    @Mock
    private AllCallLogs allCallDetails;

    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;

    @Mock
    private CallLogMapper callDetailRecordMapper;

    @Before
    public void setUp(){
      initMocks(this);
      callLoggingService = new CallLogService(allCallDetails, kookooCallDetailRecordsService, callDetailRecordMapper);
    }

    @Test
    public void shouldCreateCallDetailRecord() {
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(CallDetailRecord.create("callId", "phoneNumber"));
        CallLog callLog = new CallLog();

        when(kookooCallDetailRecordsService.findByCallId("callId")).thenReturn(kookooCallDetailRecord);
        when(callDetailRecordMapper.toCallLog(kookooCallDetailRecord)).thenReturn(callLog);
        callLoggingService.create(new EndOfCallEvent("callId","patientId"));

        verify(allCallDetails).add(callLog);
    }
}
